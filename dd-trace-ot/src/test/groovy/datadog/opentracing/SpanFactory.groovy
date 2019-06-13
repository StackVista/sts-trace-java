package datadog.opentracing

import datadog.trace.api.sampling.PrioritySampling
import datadog.trace.common.writer.ListWriter

class SpanFactory {
  static newSpanOf(long timestampMicro, String threadName = Thread.currentThread().name) {
    def writer = new ListWriter()
    def tracer = new DDTracer(writer)
    def currentThreadName = Thread.currentThread().getName()
    def fakePidProvider = [getPid: {-> return (Long)42}] as ISTSSpanContextPidProvider
    def fakeHostNameProvider = [getHostName: {-> return "fakehost"}] as ISTSSpanContextHostNameProvider
    def fakeStartTimeProvider = [getStartTime: {-> return  (Long)228650400}] as ISTSSpanContextStartTimeProvider
    Thread.currentThread().setName(threadName)
    def context = new DDSpanContext(
      "1",
      "1",
      "0",
      "fakeService",
      "fakeOperation",
      "fakeResource",
      PrioritySampling.UNSET,
      null,
      Collections.emptyMap(),
      false,
      "fakeType",
      Collections.emptyMap(),
      new PendingTrace(tracer, "1", [:]),
      tracer)
    context.setPidProvider(fakePidProvider)
    context.setHostNameProvider(fakeHostNameProvider)
    context.setStartTimeProvider(fakeStartTimeProvider)
    Thread.currentThread().setName(currentThreadName)
    return new DDSpan(timestampMicro, context)
  }

  static newSpanOf(DDTracer tracer) {

    def fakePidProvider = [getPid: {-> return (Long)42}] as ISTSSpanContextPidProvider
    def fakeHostNameProvider = [getHostName: {-> return "fakehost"}] as ISTSSpanContextHostNameProvider
    def fakeStartTimeProvider = [getStartTime: {-> return  (Long)228650400}] as ISTSSpanContextStartTimeProvider

    def context = new DDSpanContext(
      "1",
      "1",
      "0",
      "fakeService",
      "fakeOperation",
      "fakeResource",
      PrioritySampling.UNSET,
      null,
      Collections.emptyMap(),
      false,
      "fakeType",
      Collections.emptyMap(),
      new PendingTrace(tracer, "1", [:]),
      tracer)
    context.setPidProvider(fakePidProvider)
    context.setHostNameProvider(fakeHostNameProvider)
    context.setStartTimeProvider(fakeStartTimeProvider)
    return new DDSpan(1, context)
  }

  static newSpanOf(PendingTrace trace) {

    def fakePidProvider = [getPid: {-> return (Long)42}] as ISTSSpanContextPidProvider
    def fakeHostNameProvider = [getHostName: {-> return "fakehost"}] as ISTSSpanContextHostNameProvider
    def fakeStartTimeProvider = [getStartTime: {-> return  (Long)228650400}] as ISTSSpanContextStartTimeProvider

    def context = new DDSpanContext(
      trace.traceId,
      "1",
      "0",
      "fakeService",
      "fakeOperation",
      "fakeResource",
      PrioritySampling.UNSET,
      null,
      Collections.emptyMap(),
      false,
      "fakeType",
      Collections.emptyMap(),
      trace,
      trace.tracer)

    context.setPidProvider(fakePidProvider)
    context.setHostNameProvider(fakeHostNameProvider)
    context.setStartTimeProvider(fakeStartTimeProvider)

    return new DDSpan(1, context)
  }

  static DDSpan newSpanOf(String serviceName, String envName) {
    def writer = new ListWriter()
    def tracer = new DDTracer(writer)
    def fakePidProvider = [getPid: {-> return (Long)42}] as ISTSSpanContextPidProvider
    def fakeHostNameProvider = [getHostName: {-> return "fakehost"}] as ISTSSpanContextHostNameProvider
    def fakeStartTimeProvider = [getStartTime: {-> return  (Long)228650400}] as ISTSSpanContextStartTimeProvider
    def context = new DDSpanContext(
      "1",
      "1",
      "0",
      serviceName,
      "fakeOperation",
      "fakeResource",
      PrioritySampling.UNSET,
      null,
      Collections.emptyMap(),
      false,
      "fakeType",
      Collections.emptyMap(),
      new PendingTrace(tracer, "1", [:]),
      tracer)
    context.setTag("env", envName)
    context.setPidProvider(fakePidProvider)
    context.setHostNameProvider(fakeHostNameProvider)
    context.setStartTimeProvider(fakeStartTimeProvider)
    return new DDSpan(0l, context)
  }
}
