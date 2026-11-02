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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.onap.multicloud.artifactbroker.sdc.NotificationHandler;
import org.onap.multicloud.artifactbroker.sdc.StatusReporter;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.notification.NotificationMessageContext;
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
@Import({TracingTest.K8sPluginStub.class, TracingTest.CapturedSpans.class})
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
    @Autowired
    private Tracer tracer;
    @Autowired
    private ObservationRegistry observationRegistry;
    @Autowired
    private CapturedSpans capturedSpans;
    @MockitoBean
    private IDistributionClient client;
    @MockitoBean
    private StatusReporter statusReporter;

    @BeforeEach
    void setUp() {
        k8sPlugin.traceparents.clear();
        capturedSpans.spans.clear();
    }

    @Test
        void kafkaEventContinuesTheProducerTraceAndIdentifiesTheConsumer() {
        INotificationData notification = distributionOfOneChart();
        when(notification.getMessageContext()).thenReturn(new NotificationMessageContext(
            "SDC-DISTR-NOTIF", 2, 42, Map.of("traceparent",
            "00-11111111111111111111111111111111-2222222222222222-01")));

        handler.activateCallback(notification);

        assertThat(capturedSpans.spans).filteredOn(span -> span.getKind() == SpanKind.CONSUMER)
            .singleElement().satisfies(span -> {
                assertThat(span.getTraceId()).isEqualTo("11111111111111111111111111111111");
                assertThat(span.getParentSpanId()).isEqualTo("2222222222222222");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.system")))
                    .isEqualTo("kafka");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.destination.name")))
                    .isEqualTo("SDC-DISTR-NOTIF");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.operation.type")))
                    .isEqualTo("process");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.destination.partition.id")))
                    .isEqualTo("2");
                assertThat(span.getAttributes().get(AttributeKey.stringKey("messaging.kafka.offset")))
                    .isEqualTo("42");
                assertThat(capturedSpans.spans).filteredOn(child -> child.getName()
                    .equals("artifactbroker.distribution")).singleElement()
                    .satisfies(child -> assertThat(child.getParentSpanId()).isEqualTo(span.getSpanId()));
            });
        assertThat(k8sPlugin.traceparents).hasSize(2)
            .allMatch(parent -> parent.startsWith("00-11111111111111111111111111111111-"));
        assertThat(tracer.currentSpan()).isNull();
        }

        @Test
        void failedMessageDoesNotLeakItsContextIntoTheNextMessage() {
        INotificationData failed = distributionOfOneChart();
        when(failed.getMessageContext()).thenReturn(new NotificationMessageContext(
            "SDC-DISTR-NOTIF", 0, 1, Map.of("traceparent",
            "00-33333333333333333333333333333333-4444444444444444-01")));
        doThrow(new IllegalStateException("status failed")).doNothing()
            .when(statusReporter).componentDone("distribution-1");

        assertThatThrownBy(() -> handler.activateCallback(failed)).isInstanceOf(IllegalStateException.class);
        assertThat(tracer.currentSpan()).isNull();
        handler.activateCallback(distributionOfOneChart());

        assertThat(capturedSpans.spans).filteredOn(span -> span.getKind() == SpanKind.CONSUMER)
            .hasSize(2).anySatisfy(span -> {
                assertThat(span.getTraceId()).isNotEqualTo("33333333333333333333333333333333");
                assertThat(span.getParentSpanId()).isEqualTo("0000000000000000");
            });
        assertThat(tracer.currentSpan()).isNull();
        }

        @Test
            void consecutiveMessagesUseTheirOwnProducerParents() {
            INotificationData first = distributionOfOneChart();
            INotificationData second = distributionOfOneChart();
            when(first.getMessageContext()).thenReturn(new NotificationMessageContext(
                "SDC-DISTR-NOTIF", 0, 1, Map.of("traceparent",
                "00-11111111111111111111111111111111-2222222222222222-01")));
            when(second.getMessageContext()).thenReturn(new NotificationMessageContext(
                "SDC-DISTR-NOTIF", 0, 2, Map.of("traceparent",
                "00-55555555555555555555555555555555-6666666666666666-01")));

            handler.activateCallback(first);
            handler.activateCallback(second);

            assertThat(capturedSpans.spans).filteredOn(span -> span.getKind() == SpanKind.CONSUMER)
                .extracting(SpanData::getParentSpanId).containsExactly("2222222222222222", "6666666666666666");
            assertThat(k8sPlugin.traceparents).hasSize(4);
            assertThat(k8sPlugin.traceparents.subList(0, 2))
                .allMatch(parent -> parent.startsWith("00-11111111111111111111111111111111-"));
            assertThat(k8sPlugin.traceparents.subList(2, 4))
                .allMatch(parent -> parent.startsWith("00-55555555555555555555555555555555-"));
            assertThat(tracer.currentSpan()).isNull();
            }

            @Test
            void malformedTraceparentStartsANewConsumerTrace() {
            INotificationData notification = distributionOfOneChart();
            when(notification.getMessageContext()).thenReturn(new NotificationMessageContext(
                "SDC-DISTR-NOTIF", 0, 1, Map.of("traceparent", "malformed")));

            handler.activateCallback(notification);

            assertThat(capturedSpans.spans).filteredOn(span -> span.getKind() == SpanKind.CONSUMER)
                .singleElement().satisfies(span -> {
                    assertThat(span.getParentSpanId()).isEqualTo("0000000000000000");
                    assertThat(k8sPlugin.traceparents).hasSize(2)
                        .allMatch(parent -> parent.startsWith("00-" + span.getTraceId() + "-"));
                });
            assertThat(tracer.currentSpan()).isNull();
            }

            @Test
            void messagesWithoutValidParentsDoNotInheritAnAmbientObservation() {
                Observation ambient = Observation.start("unrelated", observationRegistry);
                try (Observation.Scope ignored = ambient.openScope()) {
                    String ambientTraceId = tracer.currentSpan().context().traceId();
                    INotificationData malformed = distributionOfOneChart();
                    when(malformed.getMessageContext()).thenReturn(new NotificationMessageContext(
                            "SDC-DISTR-NOTIF", 0, 1, Map.of("traceparent", "malformed")));
                    handler.activateCallback(distributionOfOneChart());
                    handler.activateCallback(malformed);
                    doThrow(new IllegalStateException("status failed"))
                            .when(statusReporter).componentDone("distribution-1");
                    assertThatThrownBy(() -> handler.activateCallback(distributionOfOneChart()))
                            .isInstanceOf(IllegalStateException.class);

                    assertThat(capturedSpans.spans).filteredOn(span -> span.getKind() == SpanKind.CONSUMER)
                            .hasSize(3).allSatisfy(span -> {
                                assertThat(span.getTraceId()).isNotEqualTo(ambientTraceId);
                                assertThat(span.getParentSpanId()).isEqualTo("0000000000000000");
                            });
                    assertThat(tracer.currentSpan().context().traceId()).isEqualTo(ambientTraceId);
                    assertThat(observationRegistry.getCurrentObservation()).isSameAs(ambient);
                } finally {
                    ambient.stop();
                }
                assertThat(tracer.currentSpan()).isNull();
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
        void spansAreExportedOverOtlpGrpcToTheJaegerCollector() {
            assertThat(context.getBeansOfType(SpanExporter.class).values()).singleElement()
                    .isInstanceOf(OtlpGrpcSpanExporter.class)
                    .asString().contains("endpoint=http://jaeger-collector.istio-config:4317");
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

    static class CapturedSpans implements SpanProcessor {

        private final List<SpanData> spans = new CopyOnWriteArrayList<>();

        @Override
        public void onStart(Context parent, ReadWriteSpan span) {
        }

        @Override
        public boolean isStartRequired() {
            return false;
        }

        @Override
        public void onEnd(ReadableSpan span) {
            spans.add(span.toSpanData());
        }

        @Override
        public boolean isEndRequired() {
            return true;
        }
    }
}
