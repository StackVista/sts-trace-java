package stackstate.opentracing.propagation;

import io.opentracing.propagation.TextMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import stackstate.opentracing.STSSpanContext;
import stackstate.trace.api.sampling.PrioritySampling;

/** A codec designed for HTTP transport via headers */
@Slf4j
public class HTTPCodec implements Codec<TextMap> {

  private static final String OT_BAGGAGE_PREFIX = "ot-baggage-";
  private static final String TRACE_ID_KEY = "x-stackstate-trace-id";
  private static final String SPAN_ID_KEY = "x-stackstate-parent-id";
  private static final String SAMPLING_PRIORITY_KEY = "x-stackstate-sampling-priority";

  private final Map<String, String> taggedHeaders;

  public HTTPCodec(final Map<String, String> taggedHeaders) {
    this.taggedHeaders = new HashMap<>();
    for (final Map.Entry<String, String> mapping : taggedHeaders.entrySet()) {
      this.taggedHeaders.put(mapping.getKey().trim().toLowerCase(), mapping.getValue());
    }
  }

  @Override
  public void inject(final STSSpanContext context, final TextMap carrier) {
    carrier.put(TRACE_ID_KEY, String.valueOf(context.getTraceId()));
    carrier.put(SPAN_ID_KEY, String.valueOf(context.getSpanId()));
    if (context.lockSamplingPriority()) {
      carrier.put(SAMPLING_PRIORITY_KEY, String.valueOf(context.getSamplingPriority()));
    }

    for (final Map.Entry<String, String> entry : context.baggageItems()) {
      carrier.put(OT_BAGGAGE_PREFIX + entry.getKey(), encode(entry.getValue()));
    }
    log.debug("{} - Parent context injected", context.getTraceId());
  }

  @Override
  public ExtractedContext extract(final TextMap carrier) {

    Map<String, String> baggage = Collections.emptyMap();
    Map<String, String> tags = Collections.emptyMap();
    Long traceId = 0L;
    Long spanId = 0L;
    int samplingPriority = PrioritySampling.UNSET;

    for (final Map.Entry<String, String> entry : carrier) {
      final String key = entry.getKey().toLowerCase();
      if (key.equalsIgnoreCase(TRACE_ID_KEY)) {
        traceId = Long.parseLong(entry.getValue());
      } else if (key.equalsIgnoreCase(SPAN_ID_KEY)) {
        spanId = Long.parseLong(entry.getValue());
      } else if (key.startsWith(OT_BAGGAGE_PREFIX)) {
        if (baggage.isEmpty()) {
          baggage = new HashMap<>();
        }
        baggage.put(key.replace(OT_BAGGAGE_PREFIX, ""), decode(entry.getValue()));
      } else if (key.equalsIgnoreCase(SAMPLING_PRIORITY_KEY)) {
        samplingPriority = Integer.parseInt(entry.getValue());
      }

      if (taggedHeaders.containsKey(key)) {
        if (tags.isEmpty()) {
          tags = new HashMap<>();
        }
        tags.put(taggedHeaders.get(key), decode(entry.getValue()));
      }
    }
    ExtractedContext context = null;
    if (traceId != 0L) {
      context = new ExtractedContext(traceId, spanId, samplingPriority, baggage, tags);
      context.lockSamplingPriority();

      log.debug("{} - Parent context extracted", context.getTraceId());
    }

    return context;
  }

  private String encode(final String value) {
    String encoded = value;
    try {
      encoded = URLEncoder.encode(value, "UTF-8");
    } catch (final UnsupportedEncodingException e) {
      log.info("Failed to encode value - {}", value);
    }
    return encoded;
  }

  private String decode(final String value) {
    String decoded = value;
    try {
      decoded = URLDecoder.decode(value, "UTF-8");
    } catch (final UnsupportedEncodingException e) {
      log.info("Failed to decode value - {}", value);
    }
    return decoded;
  }
}
