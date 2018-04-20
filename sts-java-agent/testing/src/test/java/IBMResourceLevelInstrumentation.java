import static net.bytebuddy.matcher.ElementMatchers.named;

import com.google.auto.service.AutoService;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import stackstate.trace.agent.tooling.Instrumenter;
import stackstate.trace.agent.tooling.STSAdvice;
import stackstate.trace.agent.tooling.STSTransformers;

@AutoService(Instrumenter.class)
public class IBMResourceLevelInstrumentation extends Instrumenter.Configurable {
  public IBMResourceLevelInstrumentation() {
    super(IBMResourceLevelInstrumentation.class.getName());
  }

  @Override
  protected AgentBuilder apply(AgentBuilder agentBuilder) {
    return agentBuilder
        .type(named("com.ibm.as400.resource.ResourceLevel"))
        .transform(STSTransformers.defaultTransformers())
        .transform(STSAdvice.create().advice(named("toString"), ToStringAdvice.class.getName()))
        .asDecorator();
  }

  public static class ToStringAdvice {
    @Advice.OnMethodExit(suppress = Throwable.class)
    static void toStringReplace(@Advice.Return(readOnly = false) String ret) {
      ret = "instrumented";
    }
  }
}
