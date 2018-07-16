import stackstate.opentracing.STSSpan
import stackstate.trace.agent.test.AgentTestRunner
import stackstate.trace.api.CorrelationIdentifier
import io.opentracing.Scope
import io.opentracing.util.GlobalTracer

class TraceCorrelationTest extends AgentTestRunner {

  def "access trace correlation only under trace" () {
    when:
    Scope scope = GlobalTracer.get().buildSpan("myspan").startActive(true)
    STSSpan span = (STSSpan) scope.span()

    then:
    CorrelationIdentifier.traceId == span.traceId
    CorrelationIdentifier.spanId == span.spanId

    when:
    scope.close()

    then:
    CorrelationIdentifier.traceId == 0
    CorrelationIdentifier.spanId == 0
  }
}
