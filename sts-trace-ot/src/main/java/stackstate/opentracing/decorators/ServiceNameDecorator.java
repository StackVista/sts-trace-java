package stackstate.opentracing.decorators;

import java.util.Map;
import stackstate.opentracing.STSSpanContext;
import stackstate.trace.api.STSTags;

public class ServiceNameDecorator extends AbstractDecorator {

  private final Map<String, String> mappings;

  public ServiceNameDecorator(final Map<String, String> mappings) {
    super();
    this.setMatchingTag(STSTags.SERVICE_NAME);
    this.mappings = mappings;
  }

  @Override
  public boolean shouldSetTag(final STSSpanContext context, final String tag, final Object value) {
    if (mappings.containsKey(String.valueOf(value))) {
      context.setServiceName(mappings.get(String.valueOf(value)));
    } else {
      context.setServiceName(String.valueOf(value));
    }
    return false;
  }
}
