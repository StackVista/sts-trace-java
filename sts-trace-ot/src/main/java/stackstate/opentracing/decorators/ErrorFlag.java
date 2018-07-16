package stackstate.opentracing.decorators;

import stackstate.opentracing.STSSpanContext;
import io.opentracing.tag.Tags;

public class ErrorFlag extends AbstractDecorator {
  public ErrorFlag() {
    super();
    this.setMatchingTag(Tags.ERROR.getKey());
  }

  @Override
  public boolean shouldSetTag(final STSSpanContext context, final String tag, final Object value) {
    // Assign resource name
    try {
      context.setErrorFlag(Boolean.parseBoolean(String.valueOf(value)));
    } catch (final Throwable t) {
      // DO NOTHING
    }
    // TODO: Do we really want an error tag if the error flag is already set?
    return true;
  }
}
