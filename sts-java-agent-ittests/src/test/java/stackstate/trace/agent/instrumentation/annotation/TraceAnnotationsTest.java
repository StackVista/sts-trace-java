package stackstate.trace.agent.instrumentation.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentracing.util.GlobalTracer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;
import stackstate.opentracing.STSSpan;
import stackstate.opentracing.STSTracer;
import stackstate.opentracing.decorators.ErrorFlag;
import stackstate.trace.agent.test.IntegrationTestUtils;
import stackstate.trace.agent.test.SayTracedHello;
import stackstate.trace.common.writer.ListWriter;

public class TraceAnnotationsTest {
  private final ListWriter writer = new ListWriter();
  private final STSTracer tracer = new STSTracer(writer);

  @Before
  public void beforeTest() throws Exception {
    IntegrationTestUtils.registerOrReplaceGlobalTracer(tracer);

    writer.start();
    assertThat(GlobalTracer.isRegistered()).isTrue();
  }

  @Test
  public void testSimpleCaseAnnotations() {
    // Test single span in new trace
    SayTracedHello.sayHello();

    assertThat(writer.firstTrace().size()).isEqualTo(1);
    assertThat(writer.firstTrace().get(0).getOperationName()).isEqualTo("SayTracedHello.sayHello");
    assertThat(writer.firstTrace().get(0).getServiceName()).isEqualTo("test");
  }

  @Test
  public void testComplexCaseAnnotations() {

    // Test new trace with 2 children spans
    SayTracedHello.sayHELLOsayHA();
    assertThat(writer.firstTrace().size()).isEqualTo(3);
    final long parentId = writer.firstTrace().get(0).context().getSpanId();

    assertThat(writer.firstTrace().get(0).getOperationName()).isEqualTo("NEW_TRACE");
    assertThat(writer.firstTrace().get(0).getParentId()).isEqualTo(0); // ROOT / no parent
    assertThat(writer.firstTrace().get(0).context().getParentId()).isEqualTo(0);
    assertThat(writer.firstTrace().get(0).getServiceName()).isEqualTo("test2");

    assertThat(writer.firstTrace().get(1).getOperationName()).isEqualTo("SAY_HA");
    assertThat(writer.firstTrace().get(1).getParentId()).isEqualTo(parentId);
    assertThat(writer.firstTrace().get(1).context().getSpanType()).isEqualTo("DB");

    assertThat(writer.firstTrace().get(2).getOperationName()).isEqualTo("SayTracedHello.sayHello");
    assertThat(writer.firstTrace().get(2).getServiceName()).isEqualTo("test");
    assertThat(writer.firstTrace().get(2).getParentId()).isEqualTo(parentId);
  }

  @Test
  public void testExceptionExit() {

    tracer.addDecorator(new ErrorFlag());

    Throwable error = null;
    try {
      SayTracedHello.sayERROR();
    } catch (final Throwable ex) {
      error = ex;
    }

    final StringWriter errorString = new StringWriter();
    error.printStackTrace(new PrintWriter(errorString));

    final STSSpan span = writer.firstTrace().get(0);
    assertThat(span.getOperationName()).isEqualTo("ERROR");
    assertThat(span.getTags().get("error")).isEqualTo(true);
    assertThat(span.getTags().get("error.msg")).isEqualTo(error.getMessage());
    assertThat(span.getTags().get("error.type")).isEqualTo(error.getClass().getName());
    assertThat(span.getTags().get("error.stack")).isEqualTo(errorString.toString());
    assertThat(span.getError()).isEqualTo(1);
  }
}
