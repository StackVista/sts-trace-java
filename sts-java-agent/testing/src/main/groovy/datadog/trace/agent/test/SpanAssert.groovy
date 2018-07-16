package stackstate.trace.agent.test

import stackstate.opentracing.STSSpan

import static stackstate.trace.agent.test.TagsAssert.assertTags

class SpanAssert {
  private final STSSpan span

  private SpanAssert(span) {
    this.span = span
  }

  static void assertSpan(STSSpan span,
                         @DelegatesTo(value = SpanAssert, strategy = Closure.DELEGATE_FIRST) Closure spec) {
    def asserter = new SpanAssert(span)
    def clone = (Closure) spec.clone()
    clone.delegate = asserter
    clone.resolveStrategy = Closure.DELEGATE_FIRST
    clone(asserter)
    asserter
  }

  def serviceName(String name) {
    assert span.serviceName == name
  }

  def operationName(String name) {
    assert span.operationName == name
  }

  def resourceName(String name) {
    assert span.resourceName == name
  }

  def spanType(String type) {
    assert span.spanType == type
    assert span.tags["span.type"] == type
  }

  def parent() {
    assert span.parentId == 0
  }

  def parentId(long parentId) {
    assert span.parentId == parentId
  }

  def traceId(long traceId) {
    assert span.traceId == traceId
  }

  def childOf(STSSpan parent) {
    assert span.parentId == parent.spanId
    assert span.traceId == parent.traceId
  }

  def errored(boolean errored) {
    assert span.isError() == errored
  }

  void tags(@DelegatesTo(value = TagsAssert, strategy = Closure.DELEGATE_FIRST) Closure spec) {
    assertTags(span, spec)
  }
}
