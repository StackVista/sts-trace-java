package stackstate.trace.instrumentation.jsp;

import static io.opentracing.log.Fields.ERROR_OBJECT;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static stackstate.trace.agent.tooling.ClassLoaderMatcher.classLoaderHasClasses;

import com.google.auto.service.AutoService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.jasper.JspCompilationContext;
import stackstate.trace.agent.tooling.Instrumenter;
import stackstate.trace.api.STSSpanTypes;
import stackstate.trace.api.STSTags;

@AutoService(Instrumenter.class)
public final class JasperJSPCompilationContextInstrumentation extends Instrumenter.Default {

  public JasperJSPCompilationContextInstrumentation() {
    super("jsp", "jsp-compile");
  }

  @Override
  protected boolean defaultEnabled() {
    return false;
  }

  @Override
  public ElementMatcher typeMatcher() {
    return named("org.apache.jasper.JspCompilationContext");
  }

  @Override
  public ElementMatcher<? super ClassLoader> classLoaderMatcher() {
    return classLoaderHasClasses("org.apache.jasper.servlet.JspServletWrapper");
  }

  @Override
  public Map<ElementMatcher, String> transformers() {
    Map<ElementMatcher, String> transformers = new HashMap<>();
    transformers.put(
        named("compile").and(takesArguments(0)).and(isPublic()),
        JasperJspCompilationContext.class.getName());
    return transformers;
  }

  public static class JasperJspCompilationContext {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static Scope startSpan(@Advice.This JspCompilationContext jspCompilationContext) {

      final Scope scope =
          GlobalTracer.get()
              .buildSpan("jsp.compile")
              .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
              .withTag(STSTags.SPAN_TYPE, STSSpanTypes.WEB_SERVLET)
              .startActive(true);

      final Span span = scope.span();
      if (jspCompilationContext.getServletContext() != null) {
        span.setTag("servlet.context", jspCompilationContext.getServletContext().getContextPath());
      }
      span.setTag(STSTags.RESOURCE_NAME, jspCompilationContext.getJspFile());
      Tags.COMPONENT.set(span, "jsp-http-servlet");
      return scope;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
        @Advice.This final JspCompilationContext jspCompilationContext,
        @Advice.Enter final Scope scope,
        @Advice.Thrown final Throwable throwable) {

      final Span span = scope.span();
      if (jspCompilationContext != null) {
        if (jspCompilationContext.getCompiler() != null) {
          span.setTag("jsp.compiler", jspCompilationContext.getCompiler().getClass().getName());
        }
        span.setTag("jsp.classFQCN", jspCompilationContext.getFQCN());
        if (throwable != null) {
          span.setTag("jsp.javaFile", jspCompilationContext.getServletJavaFileName());
          span.setTag("jsp.classpath", jspCompilationContext.getClassPath());
        }
      }

      if (throwable != null) {
        Tags.ERROR.set(span, Boolean.TRUE);
        span.log(Collections.singletonMap(ERROR_OBJECT, throwable));
      }
      scope.close();
    }
  }
}