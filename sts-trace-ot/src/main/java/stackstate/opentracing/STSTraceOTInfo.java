package stackstate.opentracing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class STSTraceOTInfo {

  public static final String JAVA_VERSION = System.getProperty("java.version", "unknown");
  public static final String JAVA_VM_NAME = System.getProperty("java.vm.name", "unknown");

  public static final String VERSION;

  static {
    String v;
    try {
      final StringBuilder sb = new StringBuilder();

      final BufferedReader br =
          new BufferedReader(
              new InputStreamReader(
                  STSTraceOTInfo.class.getResourceAsStream("/sts-trace-ot.version"), "UTF-8"));
      for (int c = br.read(); c != -1; c = br.read()) sb.append((char) c);

      v = sb.toString().trim();
    } catch (final Exception e) {
      v = "unknown";
    }
    VERSION = v;
    log.info("sts-trace - version: {}", v);
  }

  public static void main(final String... args) {
    System.out.println(VERSION);
  }
}