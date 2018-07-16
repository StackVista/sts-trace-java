package stackstate.trace.instrumentation.ratpack;

import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static stackstate.trace.instrumentation.ratpack.RatpackInstrumentation.CLASSLOADER_CONTAINS_RATPACK_1_4_OR_ABOVE;

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import stackstate.trace.agent.tooling.Instrumenter;
import stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice;

@AutoService(Instrumenter.class)
public final class RatpackHttpClientInstrumentation extends Instrumenter.Default {

  public static final TypeDescription.ForLoadedType URI_TYPE_DESCRIPTION =
      new TypeDescription.ForLoadedType(URI.class);

  public RatpackHttpClientInstrumentation() {
    super(RatpackInstrumentation.EXEC_NAME);
  }

  @Override
  protected boolean defaultEnabled() {
    // FIXME: Injecting ContextualScopeManager is probably a bug. Verify and check all ratpack helpers before enabling.
    return false;
  }

  @Override
  public ElementMatcher typeMatcher() {
    return not(isInterface()).and(hasSuperType(named("ratpack.http.client.HttpClient")));
  }

  @Override
  public ElementMatcher<? super ClassLoader> classLoaderMatcher() {
    return CLASSLOADER_CONTAINS_RATPACK_1_4_OR_ABOVE;
  }

  @Override
  public String[] helperClassNames() {
    return new String[] {
      // http helpers
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$RatpackHttpClientRequestAdvice",
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$RatpackHttpClientRequestStreamAdvice",
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$RatpackHttpGetAdvice",
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$RequestAction",
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$ResponseAction",
      "stackstate.trace.instrumentation.ratpack.impl.RatpackHttpClientAdvice$StreamedResponseAction",
      "stackstate.trace.instrumentation.ratpack.impl.RequestSpecInjectAdapter",
      "stackstate.trace.instrumentation.ratpack.impl.WrappedRequestSpec",
      // core helpers
      "stackstate.opentracing.scopemanager.ContextualScopeManager",
      "stackstate.opentracing.scopemanager.ScopeContext"
    };
  }

  @Override
  public Map<ElementMatcher, String> transformers() {
    Map<ElementMatcher, String> transformers = new HashMap<>();
    transformers.put(
        named("request")
            .and(
                takesArguments(
                    URI_TYPE_DESCRIPTION, RatpackInstrumentation.ACTION_TYPE_DESCRIPTION)),
        RatpackHttpClientAdvice.RatpackHttpClientRequestAdvice.class.getName());
    transformers.put(
        named("requestStream")
            .and(
                takesArguments(
                    URI_TYPE_DESCRIPTION, RatpackInstrumentation.ACTION_TYPE_DESCRIPTION)),
        RatpackHttpClientAdvice.RatpackHttpClientRequestStreamAdvice.class.getName());
    transformers.put(
        named("get")
            .and(
                takesArguments(
                    URI_TYPE_DESCRIPTION, RatpackInstrumentation.ACTION_TYPE_DESCRIPTION)),
        RatpackHttpClientAdvice.RatpackHttpGetAdvice.class.getName());
    return transformers;
  }
}
