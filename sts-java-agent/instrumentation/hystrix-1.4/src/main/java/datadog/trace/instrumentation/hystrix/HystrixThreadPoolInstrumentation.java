package stackstate.trace.instrumentation.hystrix;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static stackstate.trace.agent.tooling.ClassLoaderMatcher.classLoaderHasClasses;

import com.google.auto.service.AutoService;
import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;
import java.util.*;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatcher;
import stackstate.trace.agent.tooling.Instrumenter;
import stackstate.trace.context.TraceScope;

@AutoService(Instrumenter.class)
public class HystrixThreadPoolInstrumentation extends Instrumenter.Default {

  public HystrixThreadPoolInstrumentation() {
    super("hystrix");
  }

  @Override
  public ElementMatcher typeMatcher() {
    return named(
        "com.netflix.hystrix.strategy.concurrency.HystrixContextScheduler$ThreadPoolWorker");
  }

  @Override
  public ElementMatcher<? super ClassLoader> classLoaderMatcher() {
    return classLoaderHasClasses("com.netflix.hystrix.AbstractCommand");
  }

  @Override
  public Map<ElementMatcher, String> transformers() {
    Map<ElementMatcher, String> transformers = new HashMap<>();
    transformers.put(
        isMethod().and(named("schedule")).and(takesArguments(1)),
        EnableAsyncAdvice.class.getName());
    return transformers;
  }

  public static class EnableAsyncAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static boolean enableAsyncTracking() {
      final Scope scope = GlobalTracer.get().scopeManager().active();
      if (scope instanceof TraceScope) {
        if (!((TraceScope) scope).isAsyncPropagating()) {
          ((TraceScope) scope).setAsyncPropagation(true);
          return true;
        }
      }
      return false;
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void disableAsyncTracking(@Advice.Enter final boolean wasEnabled) {
      if (wasEnabled) {
        final Scope scope = GlobalTracer.get().scopeManager().active();
        if (scope instanceof TraceScope) {
          ((TraceScope) scope).setAsyncPropagation(false);
        }
      }
    }
  }
}
