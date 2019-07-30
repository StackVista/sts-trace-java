#!/bin/bash

sed -i 's/"dd."/"sts."/g' dd-trace-api/src/main/java/datadog/trace/api/Config.java
sed -i 's/"dd.service.name"/"sts.service.name"/g' dd-trace-api/src/main/java/datadog/trace/api/Config.java
sed -i 's/"dd.trace_id"/"sts.trace_id"/g' dd-java-agent/src/test/java/jvmbootstraptest/LogManagerSetter.java
sed -i 's/"dd.span_id"/"sts.span_id"/g' dd-trace-api/src/main/java/datadog/trace/api/CorrelationIdentifier.java
sed -i 's/"dd.app.customlogmanager"/"sts.app.customlogmanager"/g' dd-java-agent/src/main/java/datadog/trace/agent/TracingAgent.java


find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_APM_ENABLED"/"STS_APM_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_BIND_HOST"/"STS_BIND_HOST"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_API_KEY"/"STS_API_KEY"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_LOGS_STDOUT"/"STS_LOGS_STDOUT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_SERVICE_NAME"/"STS_SERVICE_NAME"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_TRACE_ENABLED"/"STS_TRACE_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_WRITER_TYPE"/"STS_WRITER_TYPE"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_SERVICE_MAPPING"/"STS_SERVICE_MAPPING"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_SPAN_TAGS"/"STS_SPAN_TAGS"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_HEADER_TAGS"/"STS_HEADER_TAGS"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_PROPAGATION_STYLE_EXTRACT"/"STS_PROPAGATION_STYLE_EXTRACT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_PROPAGATION_STYLE_INJECT"/"STS_PROPAGATION_STYLE_INJECT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_JMXFETCH_METRICS_CONFIGS"/"STS_JMXFETCH_METRICS_CONFIGS"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_TRACE_AGENT_PORT"/"STS_TRACE_AGENT_PORT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_TRACE_AGENT_PORT"/"STS_TRACE_AGENT_PORT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_TRACE_REPORT_HOSTNAME"/"STS_TRACE_REPORT_HOSTNAME"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_INTEGRATION_ORDER_ENABLED"/"STS_INTEGRATION_ORDER_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_AGENT_PORT"/"STS_AGENT_PORT"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_INTEGRATION_TEST_ENV_ENABLED"/"STS_INTEGRATION_TEST_ENV_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_INTEGRATION_DISABLED_ENV_ENABLED"/"STS_INTEGRATION_DISABLED_ENV_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_JMXFETCH_ORDER_ENABLED"/"STS_JMXFETCH_ORDER_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_JMXFETCH_TEST_ENV_ENABLED"/"STS_JMXFETCH_TEST_ENV_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_JMXFETCH_DISABLED_ENV_ENABLED"/"STS_JMXFETCH_DISABLED_ENV_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_ORDER_ANALYTICS_ENABLED"/"STS_ORDER_ANALYTICS_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_TEST_ENV_ANALYTICS_ENABLED"/"STS_TEST_ENV_ANALYTICS_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_DISABLED_ENV_ANALYTICS_ENABLED"/"STS_DISABLED_ENV_ANALYTICS_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_ENV_ZERO_TEST"/"STS_ENV_ZERO_TEST"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_ENV_FLOAT_TEST"/"STS_ENV_FLOAT_TEST"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_INTEGRATIONS_ENABLED"/"STS_INTEGRATIONS_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_INTEGRATION_\${value}_ENABLED"/"STS_INTEGRATION_\${value}_ENABLED"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_FLOAT_TEST"/"STS_FLOAT_TEST"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"DD_"/"STS_"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd."/"sts."/g' {} \;

find . -type f -name '*TestBase.groovy' -exec sed -i 's/"dd.logs.injection"/"sts.logs.injection"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.trace.span.tags"/"sts.trace.span.tags"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.order.enabled"/"sts.integration.order.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.test-prop.enabled"/"sts.integration.test-prop.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.disabled-prop.enabled"/"sts.integration.disabled-prop.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.order.enabled"/"sts.jmxfetch.order.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.test-prop.enabled"/"sts.jmxfetch.test-prop.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.disabled-prop.enabled"/"sts.jmxfetch.disabled-prop.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.order.analytics.enabled"/"sts.order.analytics.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.test-prop.analytics.enabled"/"sts.test-prop.analytics.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.disabled-prop.analytics.enabled"/"sts.disabled-prop.analytics.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.prop.zero.test"/"sts.prop.zero.test"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.prop.float.test"/"sts.prop.float.test"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.float.test"/"sts.float.test"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.garbage.test"/"sts.garbage.test"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.negative.test"/"sts.negative.test"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.trace.resolver.enabled"/"sts.trace.resolver.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.statsd.port"/"sts.jmxfetch.statsd.port"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.enabled"/"sts.jmxfetch.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.writer.type"/"sts.writer.type"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.jmxfetch.${it}.enabled"/"sts.jmxfetch.\${it}.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.${it}.enabled"/"sts.integration.\${it}.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.trace.methods"/"sts.trace.methods"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.trace.annotations"/"sts.trace.annotations"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.logs.injection"/"sts.logs.injection"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.jetty.enabled"/"sts.integration.jetty.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.sparkjava.enabled"/"sts.integration.sparkjava.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.jetty.enabled"/"sts.integration.jetty.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.trace.executors"/"sts.trace.executors"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.test.enabled"/"sts.integration.test.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integration.\${value}.enabled"/"sts.integration.\${value}.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.integrations.enabled"/"sts.integrations.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.\${integName}.analytics.enabled"/"sts.\${integName}.analytics.enabled"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"dd.\${integName}.analytics.sample-rate"/"sts.\${integName}.analytics.sample-rate"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.jmxfetch.enabled=true"/"-Dsts.jmxfetch.enabled=true"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.writer.type=LoggingWriter"/"-Dsts.writer.type=LoggingWriter"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.jmxfetch.refresh-beans-period=1"/"-Dsts.jmxfetch.refresh-beans-period=1"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.service.name=smoke-test-java-app"/"-Dsts.service.name=smoke-test-java-app"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.app.customlogmanager=true"/"-Dsts.app.customlogmanager=true"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.service.name=smoke-test-java-app"/"-Dsts.service.name=smoke-test-java-app"/g' {} \;
find . -type f -name '*Test.groovy' -exec sed -i 's/"-Ddd.app.customlogmanager=false"/"-Dsts.app.customlogmanager=false"/g' {} \;


find . -type f -name '*.gradle' -exec sed -i 's/"-Ddd.service.name=java-agent-tests"/"-Dsts.service.name=java-agent-tests"/g' {} \;
find . -type f -name '*.gradle' -exec sed -i 's/"-Ddd.writer.type=LoggingWriter"/"-Dsts.writer.type=LoggingWriter"/g' {} \;
find . -type f -name '*.gradle' -exec sed -i 's/"dd.trace.runtime.context.field.injection"/"sts.trace.runtime.context.field.injection"/g' {} \;





find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_API_KEY"/"STS_API_KEY"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_COLLECT_KUBERNETES_EVENTS"/"STS_COLLECT_KUBERNETES_EVENTS"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_LEADER_ELECTION"/"STS_LEADER_ELECTION"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_KUBERNETES_COLLECT_METADATA_TAGS"/"STS_KUBERNETES_COLLECT_METADATA_TAGS"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_KUBERNETES_METADATA_TAG_UPDATE_FREQ"/"STS_KUBERNETES_METADATA_TAG_UPDATE_FREQ"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_HOSTNAME"/"STS_HOSTNAME"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_KUBERNETES_KUBELET_HOST"/"STS_KUBERNETES_KUBELET_HOST"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_FULL_CARDINALITY_TAGGING"/"STS_FULL_CARDINALITY_TAGGING"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_SD_BACKEND"/"STS_SD_BACKEND"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_LOG_LEVEL"/"STS_LOG_LEVEL"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_DOGSTATSD_NON_LOCAL_TRAFFIC"/"STS_DOGSTATSD_NON_LOCAL_TRAFFIC"/g' {} \;
find dd-java-agent/agent-jmxfetch/integrations-core/kubelet/tests/fixtures/ -type f -name '*.json' -exec sed -i 's/"DD_KUBERNETES_POD_LABELS_AS_TAGS"/"STS_KUBERNETES_POD_LABELS_AS_TAGS"/g' {} \;


UNAME="$(uname)"
if [[ "$UNAME" != MSYS* ]]; then
    echo "Checking replacements..."

    grep -r --include=*.groovy "\"DD_"  $PWD

    RESULT=$?
    if [ $RESULT -eq 0 ]; then
      echo "Please fix branding: there is still something using DD_ prefix"
      exit 1
    else
      echo "Groovy tests DD_ branding ok, return code $RESULT"
    fi

    grep -r --include=*.groovy "\"dd\."  $PWD

    RESULT=$?
    if [ $RESULT -eq 0 ]; then
      echo "Please fix branding: there is still something using dd. prefix"
      exit 1
    else
      echo "Groovy tests dd. branding ok, return code $RESULT"
    fi

    grep -r --include=*.groovy "\"-Ddd."  $PWD

    RESULT=$?
    if [ $RESULT -eq 0 ]; then
      echo "Please fix branding: there is still something using -Ddd. prefix"
      exit 1
    else
      echo "Groovy tests dd. branding ok, return code $RESULT"
    fi



    grep -r --include=*.json "\"DD_"  $PWD

    RESULT=$?
    if [ $RESULT -eq 0 ]; then
      echo "Please fix branding: there is still something using DD_ prefix"
      exit 1
    else
      echo "Branding of json fixtures was successful, return code $RESULT"
    fi

fi
