package stackstate.trace.agent.tooling;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.AgentBuilder.LocationStrategy;
import net.bytebuddy.dynamic.ClassFileLocator;

/** A bytebuddy advice builder with default Stackstate settings. */
public class STSAdvice extends AgentBuilder.Transformer.ForAdvice {
  private static final ClassFileLocator AGENT_CLASS_LOCATOR =
      ClassFileLocator.ForClassLoader.of(Utils.getAgentClassLoader());
  /** Location strategy for resolving classes in the agent's jar. */
  private static final LocationStrategy AGENT_CLASS_LOCATION_STRATEGY =
      new AgentBuilder.LocationStrategy.Simple(AGENT_CLASS_LOCATOR);

  /**
   * Create bytebuddy advice with default stackstate settings.
   *
   * @return the bytebuddy advice
   */
  public static AgentBuilder.Transformer.ForAdvice create() {
    return create(true);
  }

  public static AgentBuilder.Transformer.ForAdvice create(final boolean includeExceptionHandler) {
    ForAdvice advice = new STSAdvice().with(AGENT_CLASS_LOCATION_STRATEGY);
    if (includeExceptionHandler) {
      advice = advice.withExceptionHandler(ExceptionHandlers.defaultExceptionHandler());
    }
    return advice;
  }

  private STSAdvice() {}
}
