package datadog.opentracing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import datadog.opentracing.decorators.AbstractDecorator;
import datadog.trace.api.DDTags;
import datadog.trace.api.sampling.PrioritySampling;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

class STSSpanContextPidProvider implements ISTSSpanContextPidProvider {

  @Override
  public long getPid() {
    // The class ManagementFactory in the package java.lang.management provides access to the
    // "managed bean for the runtime system of the Java virtual machine".
    // The getName() method of this class is described as:
    //  Returns the name representing the running Java virtual machine.
    //  This name, as it happens, contains the process id in the Sun/Oracle JVM implementation
    // of this methods in a format such as: PID@host ,
    // Not guaranteed to work on all JVM implementations
    // Further options:
    // todo: support java 9 natively
    // https://docs.oracle.com/javase/9/docs/api/java/lang/ProcessHandle.html
    // public interface CLibrary extends Library {
    //     CLibrary INSTANCE = (CLibrary)Native.loadLibrary("c", CLibrary.class);
    //       int getpid ();
    // }

    String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
    return Long.parseLong(processName.split("@")[0]);
  }
}

class STSSpanContextHostnameProvider implements ISTSSpanContextHostNameProvider {

  @Override
  public String getHostName() throws UnknownHostException {
    return InetAddress.getLocalHost().getHostName();
  }
}

class STSSpanContextStartTimeProvider implements ISTSSpanContextStartTimeProvider {

  @Override
  public long getStartTime() {
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    long startTime = runtimeBean.getStartTime();
    return startTime;
  }
}

/**
 * SpanContext represents Span state that must propagate to descendant Spans and across process
 * boundaries.
 *
 * <p>SpanContext is logically divided into two pieces: (1) the user-level "Baggage" that propagates
 * across Span boundaries and (2) any Datadog fields that are needed to identify or contextualize
 * the associated Span instance
 */
@Slf4j
public class DDSpanContext implements io.opentracing.SpanContext {
  public static final String PRIORITY_SAMPLING_KEY = "_sampling_priority_v1";
  public static final String SAMPLE_RATE_KEY = "_sample_rate";
  public static final String ORIGIN_KEY = "_dd.origin";

  private static final Map<String, Number> EMPTY_METRICS = Collections.emptyMap();

  // Shared with other span contexts
  /** For technical reasons, the ref to the original tracer */
  private final DDTracer tracer;

  /** The collection of all span related to this one */
  private final PendingTrace trace;

  /** Baggage is associated with the whole trace and shared with other spans */
  private final Map<String, String> baggageItems;

  // Not Shared with other span contexts
  private final String traceId;
  private final String spanId;
  private final String parentId;
  private long pid = 0;
  private String hostName = "";
  private long starttime = 0;

  private ISTSSpanContextPidProvider pidProvider;
  private ISTSSpanContextHostNameProvider hostNameProvider;
  private ISTSSpanContextStartTimeProvider startTimeProvider;

  /** Tags are associated to the current span, they will not propagate to the children span */
  private final Map<String, Object> tags = new ConcurrentHashMap<>();

  /** The service name is required, otherwise the span are dropped by the agent */
  private volatile String serviceName;
  /** The resource associated to the service (server_web, database, etc.) */
  private volatile String resourceName;
  /** Each span have an operation name describing the current span */
  private volatile String operationName;
  /** The type of the span. If null, the Datadog Agent will report as a custom */
  private volatile String spanType;
  /** True indicates that the span reports an error */
  private volatile boolean errorFlag;
  /**
   * When true, the samplingPriority cannot be changed. This prevents the sampling flag from
   * changing after the context has propagated.
   *
   * <p>For thread safety, this boolean is only modified or accessed under instance lock.
   */
  private boolean samplingPriorityLocked = false;
  /** The origin of the trace. (eg. Synthetics) */
  private final String origin;
  /** Metrics on the span */
  private final AtomicReference<Map<String, Number>> metrics = new AtomicReference<>();

  // Additional Metadata
  private final String threadName = Thread.currentThread().getName();
  private final long threadId = Thread.currentThread().getId();

  public DDSpanContext(
      final String traceId,
      final String spanId,
      final String parentId,
      final String serviceName,
      final String operationName,
      final String resourceName,
      final int samplingPriority,
      final String origin,
      final Map<String, String> baggageItems,
      final boolean errorFlag,
      final String spanType,
      final Map<String, Object> tags,
      final PendingTrace trace,
      final DDTracer tracer) {

    assert tracer != null;
    assert trace != null;
    this.tracer = tracer;
    this.trace = trace;

    assert traceId != null;
    assert spanId != null;
    assert parentId != null;
    this.traceId = traceId;
    this.spanId = spanId;
    this.parentId = parentId;

    if (baggageItems == null) {
      this.baggageItems = new ConcurrentHashMap<>(0);
    } else {
      this.baggageItems = baggageItems;
    }

    if (tags != null) {
      this.tags.putAll(tags);
    }

    this.serviceName = serviceName;
    this.operationName = operationName;
    this.resourceName = resourceName;
    this.errorFlag = errorFlag;
    this.spanType = spanType;
    this.origin = origin;

    this.pidProvider = new STSSpanContextPidProvider();
    this.hostNameProvider = new STSSpanContextHostnameProvider();
    this.startTimeProvider = new STSSpanContextStartTimeProvider();

    if (samplingPriority != PrioritySampling.UNSET) {
      setSamplingPriority(samplingPriority);
    }

    if (origin != null) {
      this.tags.put(ORIGIN_KEY, origin);
    }
    this.tags.put(DDTags.THREAD_NAME, threadName);
    this.tags.put(DDTags.THREAD_ID, threadId);

    this.tags.put(DDTags.SPAN_HOSTNAME, getHostName());
    long pid = getPID();
    if (pid != 0L) {
      this.tags.put(DDTags.SPAN_PID, pid);
    }
    this.tags.put(DDTags.SPAN_STARTTIME, getStartTime());
  }

  public String getTraceId() {
    return traceId;
  }

  public String getParentId() {
    return parentId;
  }

  public String getSpanId() {
    return spanId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(final String serviceName) {
    this.serviceName = serviceName;
  }

  public String getResourceName() {
    return resourceName == null || resourceName.isEmpty() ? operationName : resourceName;
  }

  public void setResourceName(final String resourceName) {
    this.resourceName = resourceName;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(final String operationName) {
    this.operationName = operationName;
  }

  public boolean getErrorFlag() {
    return errorFlag;
  }

  public void setErrorFlag(final boolean errorFlag) {
    this.errorFlag = errorFlag;
  }

  public String getSpanType() {
    return spanType;
  }

  public void setSpanType(final String spanType) {
    this.spanType = spanType;
  }

  public void setSamplingPriority(final int newPriority) {
    if (trace != null) {
      final DDSpan rootSpan = trace.getRootSpan();
      if (null != rootSpan && rootSpan.context() != this) {
        rootSpan.context().setSamplingPriority(newPriority);
        return;
      }
    }
    if (newPriority == PrioritySampling.UNSET) {
      log.debug("{}: Refusing to set samplingPriority to UNSET", this);
      return;
    }
    // sync with lockSamplingPriority
    synchronized (this) {
      if (samplingPriorityLocked) {
        log.debug(
            "samplingPriority locked at {}. Refusing to set to {}",
            getMetrics().get(PRIORITY_SAMPLING_KEY),
            newPriority);
      } else {
        setMetric(PRIORITY_SAMPLING_KEY, newPriority);
        log.debug("Set sampling priority to {}", getMetrics().get(PRIORITY_SAMPLING_KEY));
      }
    }
  }

  public void setPidProvider(final ISTSSpanContextPidProvider provider) {
    this.pidProvider = provider;
    this.pid = 0;
    this.tags.remove(DDTags.SPAN_PID);
    this.tags.put(DDTags.SPAN_PID, getPID());
  }

  public void setHostNameProvider(final ISTSSpanContextHostNameProvider provider) {
    this.hostNameProvider = provider;
    this.hostName = "";
    this.tags.put(DDTags.SPAN_HOSTNAME, getHostName());
  }

  public void setStartTimeProvider(final ISTSSpanContextStartTimeProvider provider) {
    this.startTimeProvider = provider;
    this.starttime = 0;
    this.tags.remove(DDTags.SPAN_STARTTIME);
    this.tags.put(DDTags.SPAN_STARTTIME, getStartTime());
  }

  /** @return the sampling priority of this span's trace, or null if no priority has been set */
  public int getSamplingPriority() {
    if (trace != null) {
      final DDSpan rootSpan = trace.getRootSpan();
      if (null != rootSpan && rootSpan.context() != this) {
        return rootSpan.context().getSamplingPriority();
      }
    }
    final Number val = getMetrics().get(PRIORITY_SAMPLING_KEY);
    return null == val ? PrioritySampling.UNSET : val.intValue();
  }

  /**
   * Prevent future changes to the context's sampling priority.
   *
   * <p>Used when a span is extracted or injected for propagation.
   *
   * <p>Has no effect if the sampling priority is unset.
   *
   * @return true if the sampling priority was locked.
   */
  public boolean lockSamplingPriority() {
    if (trace != null) {
      final DDSpan rootSpan = trace.getRootSpan();
      if (null != rootSpan && rootSpan.context() != this) {
        return rootSpan.context().lockSamplingPriority();
      }
    }
    // sync with setSamplingPriority
    synchronized (this) {
      if (getMetrics().get(PRIORITY_SAMPLING_KEY) == null) {
        log.debug("{} : refusing to lock unset samplingPriority", this);
      } else if (samplingPriorityLocked == false) {
        samplingPriorityLocked = true;
        log.debug(
            "{} : locked samplingPriority to {}", this, getMetrics().get(PRIORITY_SAMPLING_KEY));
      }
      return samplingPriorityLocked;
    }
  }

  public String getOrigin() {
    final DDSpan rootSpan = trace.getRootSpan();
    if (null != rootSpan) {
      return rootSpan.context().origin;
    } else {
      return origin;
    }
  }

  public void setBaggageItem(final String key, final String value) {
    baggageItems.put(key, value);
  }

  public String getBaggageItem(final String key) {
    return baggageItems.get(key);
  }

  public Map<String, String> getBaggageItems() {
    return baggageItems;
  }

  /* (non-Javadoc)
   * @see io.opentracing.SpanContext#baggageItems()
   */
  @Override
  public Iterable<Map.Entry<String, String>> baggageItems() {
    return baggageItems.entrySet();
  }

  @JsonIgnore
  public PendingTrace getTrace() {
    return trace;
  }

  @JsonIgnore
  public DDTracer getTracer() {
    return tracer;
  }

  public Map<String, Number> getMetrics() {
    final Map<String, Number> metrics = this.metrics.get();
    return metrics == null ? EMPTY_METRICS : metrics;
  }

  public void setMetric(final String key, final Number value) {
    if (metrics.get() == null) {
      metrics.compareAndSet(null, new ConcurrentHashMap<String, Number>());
    }
    if (value instanceof Float) {
      metrics.get().put(key, value.doubleValue());
    } else {
      metrics.get().put(key, value);
    }
  }
  /**
   * Add a tag to the span. Tags are not propagated to the children
   *
   * @param tag the tag-name
   * @param value the value of the tag. tags with null values are ignored.
   */
  public synchronized void setTag(final String tag, final Object value) {
    if (value == null || (value instanceof String && ((String) value).isEmpty())) {
      tags.remove(tag);
      return;
    }

    boolean addTag = true;

    // Call decorators
    final List<AbstractDecorator> decorators = tracer.getSpanContextDecorators(tag);
    if (decorators != null) {
      for (final AbstractDecorator decorator : decorators) {
        try {
          addTag &= decorator.shouldSetTag(this, tag, value);
        } catch (final Throwable ex) {
          log.debug(
              "Could not decorate the span decorator={}: {}",
              decorator.getClass().getSimpleName(),
              ex.getMessage());
        }
      }
    }

    if (addTag) {
      tags.put(tag, value);
    }
  }

  public synchronized Map<String, Object> getTags() {
    return Collections.unmodifiableMap(tags);
  }

  @Override
  public String toString() {
    final StringBuilder s =
        new StringBuilder()
            .append("DDSpan [ t_id=")
            .append(traceId)
            .append(", s_id=")
            .append(spanId)
            .append(", p_id=")
            .append(parentId)
            .append("] trace=")
            .append(getServiceName())
            .append("/")
            .append(getOperationName())
            .append("/")
            .append(getResourceName())
            .append(" metrics=")
            .append(new TreeMap(getMetrics()));
    if (errorFlag) {
      s.append(" *errored*");
    }
    if (tags != null) {
      s.append(" tags=").append(new TreeMap(tags));
    }
    return s.toString();
  }

  private long getPID() {
    if (this.pid == 0) {
      try {
        this.pid = this.pidProvider.getPid();
      } catch (Exception e) {
        log.debug("Failed to detect pid");
      }
    }
    return this.pid;
  }

  private String getHostName() {
    if (this.hostName.equals("")) {
      try {
        this.hostName = this.hostNameProvider.getHostName();
      } catch (Exception e) {
        log.debug("Failed to detect hostname");
      }
    }
    return this.hostName;
  }

  private long getStartTime() {
    if (this.starttime == 0) {
      try {
        this.starttime = this.startTimeProvider.getStartTime();
      } catch (Exception e) {
        log.debug("Failed to detect start time");
      }
    }
    return this.starttime;
  }
}
