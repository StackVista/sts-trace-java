package stackstate.opentracing.decorators;

import stackstate.opentracing.STSSpanContext;
import stackstate.trace.api.STSTags;

public class ResourceNameDecorator extends AbstractDecorator {

  public ResourceNameDecorator() {
    super();
    this.setMatchingTag(STSTags.RESOURCE_NAME);
  }

  @Override
  public boolean shouldSetTag(final STSSpanContext context, final String tag, final Object value) {
    context.setResourceName(String.valueOf(value));
    return false;
  }
}
