package stackstate.opentracing.decorators;

import stackstate.opentracing.STSSpanContext;
import stackstate.opentracing.STSTracer;

public class ServletContextDecorator extends AbstractDecorator {

  public ServletContextDecorator() {
    super();
    this.setMatchingTag("servlet.context");
  }

  @Override
  public boolean shouldSetTag(final STSSpanContext context, final String tag, final Object value) {
    String contextName = String.valueOf(value).trim();
    if (contextName.equals("/")
        || (!context.getServiceName().equals(STSTracer.UNASSIGNED_DEFAULT_SERVICE_NAME)
            && !context.getServiceName().isEmpty())) {
      return true;
    }
    if (contextName.startsWith("/")) {
      if (contextName.length() > 1) {
        contextName = contextName.substring(1);
      }
    }
    if (!contextName.isEmpty()) {
      context.setServiceName(contextName);
    }
    return true;
  }
}
