package stackstate.opentracing;

import com.google.common.annotations.VisibleForTesting;
import stackstate.trace.api.CorrelationIdentifier;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

public class OTTraceCorrelation implements CorrelationIdentifier.Provider {
  public static final OTTraceCorrelation INSTANCE = new OTTraceCorrelation();

  private final Tracer tracer;

  private OTTraceCorrelation() {
    // GlobalTracer.get() is guaranteed to return a constant so we can keep reference to it
    this(GlobalTracer.get());
  }

  @VisibleForTesting
  OTTraceCorrelation(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public long getTraceId() {
    final Span activeSpan = tracer.activeSpan();
    if (activeSpan instanceof STSSpan) {
      return ((STSSpan) activeSpan).getTraceId();
    }
    return 0;
  }

  @Override
  public long getSpanId() {
    final Span activeSpan = tracer.activeSpan();
    if (activeSpan instanceof STSSpan) {
      return ((STSSpan) activeSpan).getSpanId();
    }
    return 0;
  }
}
