package datadog.opentracing;

import java.net.UnknownHostException;

public interface ISTSSpanContextHostNameProvider {
  String getHostName() throws UnknownHostException;
}
