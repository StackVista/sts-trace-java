package stackstate.trace.instrumentation.okhttp3;

import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static stackstate.trace.agent.tooling.ClassLoaderMatcher.classLoaderHasClasses;
import static stackstate.trace.instrumentation.okhttp3.OkHttpClientSpanDecorator.STANDARD_TAGS;

import com.google.auto.service.AutoService;
import io.opentracing.util.GlobalTracer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import stackstate.trace.agent.tooling.Instrumenter;

@AutoService(Instrumenter.class)
public class OkHttp3Instrumentation extends Instrumenter.Default {

  public OkHttp3Instrumentation() {
    super("okhttp", "okhttp-3");
  }

  @Override
  public ElementMatcher typeMatcher() {
    return named("okhttp3.OkHttpClient");
  }

  @Override
  public ElementMatcher<? super ClassLoader> classLoaderMatcher() {
    return classLoaderHasClasses(
        "okhttp3.Request",
        "okhttp3.Response",
        "okhttp3.Connection",
        "okhttp3.Cookie",
        "okhttp3.ConnectionPool",
        "okhttp3.Headers");
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {
      "stackstate.trace.instrumentation.okhttp3.OkHttpClientSpanDecorator",
      "stackstate.trace.instrumentation.okhttp3.OkHttpClientSpanDecorator$1",
      "stackstate.trace.instrumentation.okhttp3.RequestBuilderInjectAdapter",
      "stackstate.trace.instrumentation.okhttp3.TagWrapper",
      "stackstate.trace.instrumentation.okhttp3.TracedCallable",
      "stackstate.trace.instrumentation.okhttp3.TracedExecutor",
      "stackstate.trace.instrumentation.okhttp3.TracedExecutorService",
      "stackstate.trace.instrumentation.okhttp3.TracedRunnable",
      "stackstate.trace.instrumentation.okhttp3.TracingInterceptor",
      "stackstate.trace.instrumentation.okhttp3.TracingCallFactory",
      "stackstate.trace.instrumentation.okhttp3.TracingCallFactory$NetworkInterceptor",
      "stackstate.trace.instrumentation.okhttp3.TracingCallFactory$1"
    };
  }

  @Override
  public Map<ElementMatcher, String> transformers() {
    Map<ElementMatcher, String> transformers = new HashMap<>();
    transformers.put(
        isConstructor().and(takesArgument(0, named("okhttp3.OkHttpClient$Builder"))),
        OkHttp3Advice.class.getName());
    return transformers;
  }

  public static class OkHttp3Advice {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void addTracingInterceptor(
        @Advice.Argument(0) final OkHttpClient.Builder builder) {
      for (final Interceptor interceptor : builder.interceptors()) {
        if (interceptor instanceof TracingInterceptor) {
          return;
        }
      }
      final TracingInterceptor interceptor =
          new TracingInterceptor(GlobalTracer.get(), Collections.singletonList(STANDARD_TAGS));
      builder.addInterceptor(interceptor);
      builder.addNetworkInterceptor(interceptor);
    }
  }
}