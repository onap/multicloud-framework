/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2026 Deutsche Telekom AG
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.multicloud.artifactbroker;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.onap.multicloud.artifactbroker.sdc.NotificationHandler;
import org.onap.multicloud.artifactbroker.sdc.StatusReporter;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    "TRACING_ENABLED=true",
    "management.tracing.export.otlp.enabled=false",
    "artifactbroker.sdc.enabled=false"})
@Import(TracingTest.K8sPluginStub.class)
class TracingTest {

    private static final String TRACEPARENT = "00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}";
    private static final byte[] CHART = {31, -117, 8, 0};
    private static final String MODULES = """
            [{"vfModuleModelName":"vf1..helm_apache..module-0","vfModuleModelInvariantUUID":"invariant",
              "vfModuleModelUUID":"uuid","vfModuleModelCustomizationUUID":"customization",
              "artifacts":["helm-1"]}]
            """;

    @Autowired
    private NotificationHandler handler;
    @Autowired
    private K8sPluginStub k8sPlugin;
    @Autowired
    private PrometheusMeterRegistry prometheus;
    @MockitoBean
    private IDistributionClient client;
    @MockitoBean
    private StatusReporter statusReporter;

    @BeforeEach
    void setUp() {
        k8sPlugin.traceparents.clear();
    }

    @Test
    void everyDistributionIsOneTraceThatReachesTheK8sPlugin() {
        INotificationData notification = distributionOfOneChart();

        handler.activateCallback(notification);
        handler.activateCallback(notification);

        List<String> traceparents = k8sPlugin.traceparents;
        assertThat(traceparents).hasSize(4).allMatch(traceparent -> traceparent.matches(TRACEPARENT))
                .doesNotHaveDuplicates();
        assertThat(traceparents).allMatch(traceparent -> isSampled(traceparent.split("-")[3]));
        List<String> traceIds = traceparents.stream().map(traceparent -> traceparent.split("-")[1]).toList();
        assertThat(traceIds.get(1)).isEqualTo(traceIds.get(0));
        assertThat(traceIds.get(3)).isEqualTo(traceIds.get(2));
        assertThat(traceIds.get(2)).isNotEqualTo(traceIds.get(0));
    }

    @Test
    void observationsDoNotReplaceTheExistingCounters() {
        handler.activateCallback(distributionOfOneChart());

        assertThat(prometheus.scrape())
                .contains("artifactbroker_distributions_total{result=\"success\"}")
                .contains("artifactbroker_downloads_total{result=\"success\"}")
                .contains("artifactbroker_distribution_seconds_count")
                .contains("artifactbroker_download_seconds_count");
    }

    @Nested
    @TestPropertySource(properties = "management.tracing.export.otlp.enabled=true")
    class Export {

        @Autowired
        private ApplicationContext context;

        @Test
        void spansAreExportedOverOtlpHttpToTheJaegerCollector() {
            assertThat(context.getBeansOfType(SpanExporter.class).values()).singleElement()
                    .isInstanceOf(OtlpHttpSpanExporter.class)
                    .asString().contains("endpoint=http://jaeger-collector.istio-system:4318/v1/traces");
        }
    }

    private static boolean isSampled(String traceFlags) {
        return (Integer.parseInt(traceFlags, 16) & 1) == 1;
    }

    private INotificationData distributionOfOneChart() {
        return notification(artifact("meta-1", "VF_MODULES_METADATA", MODULES.getBytes(UTF_8)),
                artifact("helm-1", "HELM", CHART));
    }

    private IArtifactInfo artifact(String uuid, String type, byte[] payload) {
        IArtifactInfo artifact = mock(IArtifactInfo.class);
        when(artifact.getArtifactUUID()).thenReturn(uuid);
        when(artifact.getArtifactType()).thenReturn(type);
        when(artifact.getArtifactName()).thenReturn(uuid + ".tgz");
        IDistributionClientDownloadResult result = mock(IDistributionClientDownloadResult.class);
        when(result.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.SUCCESS);
        when(result.getArtifactPayload()).thenReturn(payload);
        when(client.download(artifact)).thenReturn(result);
        return artifact;
    }

    private static INotificationData notification(IArtifactInfo... artifacts) {
        IResourceInstance resource = mock(IResourceInstance.class);
        when(resource.getResourceInstanceName()).thenReturn("vf1");
        when(resource.getResourceType()).thenReturn("VF");
        when(resource.getArtifacts()).thenReturn(List.of(artifacts));
        INotificationData notification = mock(INotificationData.class);
        when(notification.getDistributionID()).thenReturn("distribution-1");
        when(notification.getResources()).thenReturn(List.of(resource));
        return notification;
    }

    static class K8sPluginStub implements RestClientCustomizer {

        private final List<String> traceparents = new ArrayList<>();

        @Override
        public void customize(RestClient.Builder builder) {
            MockRestServiceServer.bindTo(builder).build()
                    .expect(manyTimes(), anything())
                    .andExpect(request -> traceparents.add(request.getHeaders().getFirst("traceparent")))
                    .andRespond(withSuccess());
        }
    }
}
