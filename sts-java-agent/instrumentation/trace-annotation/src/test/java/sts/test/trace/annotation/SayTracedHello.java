package sts.test.trace.annotation;

import io.opentracing.tag.StringTag;
import io.opentracing.util.GlobalTracer;
import java.util.concurrent.Callable;
import stackstate.trace.api.STSTags;
import stackstate.trace.api.Trace;

public class SayTracedHello {

  @Trace
  public static String sayHello() {
    new StringTag(STSTags.SERVICE_NAME)
        .set(GlobalTracer.get().scopeManager().active().span(), "test");
    return "hello!";
  }

  @Trace(operationName = "SAY_HA")
  public static String sayHA() {
    new StringTag(STSTags.SERVICE_NAME)
        .set(GlobalTracer.get().scopeManager().active().span(), "test");
    new StringTag(STSTags.SPAN_TYPE).set(GlobalTracer.get().scopeManager().active().span(), "DB");
    return "HA!!";
  }

  @Trace(operationName = "NEW_TRACE")
  public static String sayHELLOsayHA() {
    new StringTag(STSTags.SERVICE_NAME)
        .set(GlobalTracer.get().scopeManager().active().span(), "test2");
    return sayHello() + sayHA();
  }

  @Trace(operationName = "ERROR")
  public static String sayERROR() {
    throw new RuntimeException();
  }

  public static String fromCallable() throws Exception {
    return new Callable<String>() {
      @com.newrelic.api.agent.Trace
      @Override
      public String call() throws Exception {
        return "Howdy!";
      }
    }.call();
  }

  public static String fromCallableWhenDisabled() throws Exception {
    return new Callable<String>() {
      @com.newrelic.api.agent.Trace
      @Override
      public String call() throws Exception {
        return "Howdy!";
      }
    }.call();
  }
}
