package stackstate.opentracing.decorators;

import stackstate.opentracing.STSSpanContext;
import stackstate.trace.api.STSTags;

public class SpanTypeDecorator extends AbstractDecorator {

  public SpanTypeDecorator() {
    super();
    this.setMatchingTag(STSTags.SPAN_TYPE);
  }

  @Override
  public boolean shouldSetTag(final STSSpanContext context, final String tag, final Object value) {
    context.setSpanType(String.valueOf(value));
    // TODO: Do we really want a span type tag since it already exists on the span?
    return false;
  }
}
