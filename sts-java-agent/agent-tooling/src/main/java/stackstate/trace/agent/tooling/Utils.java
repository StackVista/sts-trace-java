package stackstate.trace.agent.tooling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import stackstate.trace.bootstrap.StackStateClassLoader;
import stackstate.trace.bootstrap.StackStateClassLoader.BootstrapClassLoaderProxy;

public class Utils {
  /* packages which will be loaded on the bootstrap classloader*/
  public static final String[] BOOTSTRAP_PACKAGE_PREFIXES = {
    "io.opentracing",
    "stackstate.slf4j",
    "stackstate.trace.bootstrap",
    "stackstate.trace.api",
    "stackstate.trace.context"
  };
  public static final String[] AGENT_PACKAGE_PREFIXES = {
    "stackstate.trace.common",
    "stackstate.trace.agent",
    "stackstate.trace.instrumentation",
    // guava
    "com.google.auto",
    "com.google.common",
    "com.google.thirdparty.publicsuffix",
    // bytebuddy
    "net.bytebuddy",
    // OT contribs for dd trace resolver
    "io.opentracing.contrib",
    // jackson
    "org.msgpack",
    "com.fasterxml.jackson",
    "org.yaml.snakeyaml"
  };

  private static Method findLoadedClassMethod = null;

  private static BootstrapClassLoaderProxy unitTestBootstrapProxy =
      new BootstrapClassLoaderProxy(new URL[0], null);

  static {
    try {
      findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new IllegalStateException(e);
    }
  }

  /** Return the classloader the core agent is running on. */
  public static ClassLoader getAgentClassLoader() {
    return AgentInstaller.class.getClassLoader();
  }

  /** Return a classloader which can be used to look up bootstrap resources. */
  public static BootstrapClassLoaderProxy getBootstrapProxy() {
    if (getAgentClassLoader() instanceof StackStateClassLoader) {
      return ((StackStateClassLoader) getAgentClassLoader()).getBootstrapProxy();
    } else {
      // in a unit test
      return unitTestBootstrapProxy;
    }
  }

  /** com.foo.Bar -> com/foo/Bar.class */
  public static String getResourceName(final String className) {
    if (!className.endsWith(".class")) {
      return className.replace('.', '/') + ".class";
    } else {
      return className;
    }
  }

  /** com/foo/Bar.class -> com.foo.Bar */
  public static String getClassName(final String resourceName) {
    return resourceName.replaceAll("\\.class\\$", "").replace('/', '.');
  }

  public static boolean isClassLoaded(final String className, final ClassLoader classLoader) {
    Class<?> loadedClass = findLoadedClass(className, classLoader);
    return loadedClass != null && loadedClass.getClassLoader() == classLoader;
  }

  public static Class<?> findLoadedClass(final String className, ClassLoader classLoader) {
    if (classLoader == ClassLoaderMatcher.BOOTSTRAP_CLASSLOADER) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    try {
      findLoadedClassMethod.setAccessible(true);
      return (Class<?>) findLoadedClassMethod.invoke(classLoader, className);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    } finally {
      findLoadedClassMethod.setAccessible(false);
    }
  }

  static boolean getConfigEnabled(final String name, final boolean fallback) {
    final String property =
        System.getProperty(
            name, System.getenv(name.toUpperCase().replaceAll("[^a-zA-Z0-9_]", "_")));
    return property == null ? fallback : Boolean.parseBoolean(property);
  }

  private Utils() {}
}