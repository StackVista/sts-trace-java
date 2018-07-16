package stackstate.trace.instrumentation.netty40.client;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.util.AttributeKey;
import io.opentracing.Span;

public class HttpClientTracingHandler
    extends CombinedChannelDuplexHandler<
        HttpClientResponseTracingHandler, HttpClientRequestTracingHandler> {

  static final AttributeKey<Span> attributeKey =
      new AttributeKey<>(HttpClientTracingHandler.class.getName());

  public HttpClientTracingHandler() {
    super(new HttpClientResponseTracingHandler(), new HttpClientRequestTracingHandler());
  }
}
