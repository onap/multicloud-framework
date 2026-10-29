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

package org.onap.multicloud.artifactbroker.sdc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.onap.multicloud.artifactbroker.sdc.TestArtifacts.artifact;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class NotificationHandlerTest {

    private static final String DISTRIBUTION_ID = "distribution-1";
    private static final byte[] CHART = {31, -117, 8, 0};
    private static final byte[] HEAT = "heat_template_version: 2013-05-23\n".getBytes(UTF_8);

    @Mock
    private IDistributionClient client;
    @Mock
    private StatusReporter statusReporter;
    @Mock
    private K8sPluginClient k8sPluginClient;

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private NotificationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new NotificationHandler(client, statusReporter, k8sPluginClient, JsonMapper.builder().build(),
                meterRegistry);
    }

    @Test
    void distributionWithoutVfIsReportedAsDone() {
        handler.activateCallback(notification(resource("pnf", "PNF")));

        verify(statusReporter).componentDone(DISTRIBUTION_ID);
        verifyNoInteractions(client, k8sPluginClient);
    }

    @Test
    void vfWithoutModulesMetadataIsReportedAsDoneWithoutDeployStatus() {
        handler.activateCallback(notification(resource("vf", "VF", artifact("helm", "HELM", "orphan.tgz"))));

        verify(statusReporter).componentDone(DISTRIBUTION_ID);
        verify(statusReporter, never()).deployOk(any(), any());
        verifyNoInteractions(client, k8sPluginClient);
    }

    @Test
    void everyModuleArtifactIsDownloadedAndOnlyTheChartIsRegistered() throws Exception {
        IArtifactInfo heat = artifact("heat-1", "HEAT", "base_template_dummy_ignore.yaml");
        IArtifactInfo helm = artifact("helm-1", "HELM", "helm_apache.tgz");
        downloads(heat, HEAT);
        downloads(helm, CHART);
        IArtifactInfo metadata = metadata("meta-1", modules(module("vf1..base_template_dummy_ignore..module-0",
                "heat-1"), module("vf1..helm_apache..module-1", "helm-1")));

        handler.activateCallback(notification(resource("vf1", "VF", metadata, heat, helm)));

        verify(statusReporter).downloadOk(DISTRIBUTION_ID, metadata);
        verify(statusReporter).downloadOk(DISTRIBUTION_ID, heat);
        verify(statusReporter).downloadOk(DISTRIBUTION_ID, helm);
        verify(k8sPluginClient).registerChart(
                argThat(module -> "vf1..helm_apache..module-1".equals(module.vfModuleModelName())),
                eq("helm_apache.tgz"), eq(CHART));
        verify(k8sPluginClient, times(1)).registerChart(any(), any(), any());
        verify(statusReporter).deployOk(DISTRIBUTION_ID, metadata);
        verify(statusReporter).componentDone(DISTRIBUTION_ID);
        assertThat(meterRegistry.counter("artifactbroker.distributions", "result", "success").count()).isEqualTo(1);
    }

    @Test
    void heatOnlyVfIsDeployedWithoutRegistration() {
        IArtifactInfo heat = artifact("heat-1", "HEAT", "base_ubuntu20.yaml");
        downloads(heat, HEAT);
        IArtifactInfo metadata = metadata("meta-1", modules(module("basicvm..base_ubuntu20..module-0", "heat-1")));

        handler.activateCallback(notification(resource("vf1", "VF", metadata, heat)));

        verify(statusReporter).downloadOk(DISTRIBUTION_ID, heat);
        verify(statusReporter).deployOk(DISTRIBUTION_ID, metadata);
        verify(statusReporter).componentDone(DISTRIBUTION_ID);
        verifyNoInteractions(k8sPluginClient);
    }

    @Test
    void distributionWithSeveralVfsIsReportedAsDoneOnce() {
        IArtifactInfo helm1 = artifact("helm-1", "HELM", "vf1.tgz");
        IArtifactInfo helm2 = artifact("helm-2", "HELM", "vf2.tgz");
        downloads(helm1, CHART);
        downloads(helm2, CHART);
        IArtifactInfo metadata1 = metadata("meta-1", modules(module("vf1..base..module-0", "helm-1")));
        IArtifactInfo metadata2 = metadata("meta-2", modules(module("vf2..base..module-0", "helm-2")));

        handler.activateCallback(notification(
                resource("vf1", "VF", metadata1, helm1), resource("vf2", "VF", metadata2, helm2)));

        verify(statusReporter).deployOk(DISTRIBUTION_ID, metadata1);
        verify(statusReporter).deployOk(DISTRIBUTION_ID, metadata2);
        verify(statusReporter, times(1)).componentDone(DISTRIBUTION_ID);
        verify(statusReporter, never()).componentError(any(), any());
    }

    @Test
    void failingModuleIsReportedOnceOnTheMetadataAndDoesNotStopTheOthers() throws Exception {
        IArtifactInfo broken = artifact("helm-1", "HELM", "helm_broken.tgz");
        IArtifactInfo healthy = artifact("helm-2", "HELM", "helm_healthy.tgz");
        downloads(broken, CHART);
        downloads(healthy, CHART);
        IArtifactInfo metadata = metadata("meta-1", modules(module("vf1..helm_broken..module-0", "helm-1"),
                module("vf1..helm_healthy..module-1", "helm-2")));
        doThrow(new ArtifactProcessingException("uploading helm_broken.tgz failed: 500 Internal Server Error"))
                .when(k8sPluginClient).registerChart(any(), eq("helm_broken.tgz"), any());

        handler.activateCallback(notification(resource("vf1", "VF", metadata, broken, healthy)));

        verify(k8sPluginClient).registerChart(any(), eq("helm_healthy.tgz"), eq(CHART));
        verify(statusReporter, times(1)).deployError(eq(DISTRIBUTION_ID), eq(metadata),
                argThat(reason -> reason.contains("helm_broken") && reason.contains("500")
                        && !reason.contains("helm_healthy")));
        verify(statusReporter, never()).deployOk(any(), any());
        verify(statusReporter, times(1)).componentError(eq(DISTRIBUTION_ID), contains("vf1"));
        verify(statusReporter, never()).componentDone(any());
        assertThat(meterRegistry.counter("artifactbroker.distributions", "result", "failure").count()).isEqualTo(1);
    }

    @Test
    void metadataThatCannotBeDownloadedIsReportedAsDownloadAndDeployError() {
        IArtifactInfo metadata = artifact("meta-1", "VF_MODULES_METADATA", "vfModulesMetadata.json");
        downloadFails(metadata);

        handler.activateCallback(notification(resource("vf1", "VF", metadata)));

        verify(statusReporter).downloadError(eq(DISTRIBUTION_ID), eq(metadata), contains("SDC unavailable"));
        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(metadata), contains("SDC unavailable"));
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), any());
        verifyNoInteractions(k8sPluginClient);
        assertThat(meterRegistry.counter("artifactbroker.downloads", "result", "failure").count()).isEqualTo(1);
    }

    @Test
    void chartThatCannotBeDownloadedIsReportedOnTheChartAndNothingIsRegistered() {
        IArtifactInfo missing = artifact("helm-1", "HELM", "helm_apache.tgz");
        IArtifactInfo other = artifact("helm-2", "HELM", "helm_other.tgz");
        downloadFails(missing);
        downloads(other, CHART);
        IArtifactInfo metadata = metadata("meta-1", modules(module("vf1..helm_apache..module-0", "helm-1"),
                module("vf1..helm_other..module-1", "helm-2")));

        handler.activateCallback(notification(resource("vf1", "VF", metadata, missing, other)));

        verify(statusReporter).downloadError(eq(DISTRIBUTION_ID), eq(missing), contains("SDC unavailable"));
        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(missing), contains("SDC unavailable"));
        verify(statusReporter, never()).deployOk(any(), any());
        verify(statusReporter, never()).deployError(any(), eq(metadata), any());
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), any());
        verifyNoInteractions(k8sPluginClient);
    }

    @ParameterizedTest
    @ValueSource(strings = {"not json", "null", "", "{}", "[null]"})
    void malformedMetadataIsReportedAsDeployError(String payload) {
        IArtifactInfo metadata = metadata("meta-1", payload);

        handler.activateCallback(notification(resource("vf1", "VF", metadata)));

        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(metadata), any());
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), any());
        verifyNoInteractions(k8sPluginClient);
    }

    @Test
    void ambiguousCloudArtifactsFailTheirModuleButOtherModulesAreStillRegistered() throws Exception {
        IArtifactInfo helm = artifact("helm-1", "HELM", "vf1.tgz");
        IArtifactInfo first = artifact("cloud-1", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "a.tgz");
        IArtifactInfo second = artifact("cloud-2", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "b.tgz");
        downloads(helm, CHART);
        downloads(first, CHART);
        downloads(second, CHART);
        IArtifactInfo metadata = metadata("meta-1", modules(module("vf1..base..module-0", "helm-1"),
                module("vf1..extra..module-1", "cloud-1", "cloud-2")));

        handler.activateCallback(notification(resource("vf1", "VF", metadata, helm, first, second)));

        verify(k8sPluginClient).registerChart(any(), eq("vf1.tgz"), eq(CHART));
        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(metadata),
                contains("no CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT artifact matches extra_"));
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), any());
    }

    @Test
    void unexpectedExceptionIsStillReportedAsError() throws Exception {
        IArtifactInfo helm = artifact("helm-1", "HELM", "vf1.tgz");
        downloads(helm, CHART);
        IArtifactInfo metadata = metadata("meta-1", modules(module("vf1..base..module-0", "helm-1")));
        doThrow(new IllegalStateException("boom")).when(k8sPluginClient).registerChart(any(), any(), any());

        handler.activateCallback(notification(resource("vf1", "VF", metadata, helm)));

        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(metadata), contains("boom"));
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), contains("boom"));
    }

    @Test
    void downloadThatThrowsFailsOnlyItsVf() {
        IArtifactInfo broken = artifact("helm-1", "HELM", "vf1.tgz");
        IArtifactInfo helm = artifact("helm-2", "HELM", "vf2.tgz");
        when(client.download(broken)).thenThrow(new IllegalArgumentException("Illegal character in path"));
        downloads(helm, CHART);
        IArtifactInfo metadata1 = metadata("meta-1", modules(module("vf1..base..module-0", "helm-1")));
        IArtifactInfo metadata2 = metadata("meta-2", modules(module("vf2..base..module-0", "helm-2")));

        handler.activateCallback(notification(
                resource("vf1", "VF", metadata1, broken), resource("vf2", "VF", metadata2, helm)));

        verify(statusReporter).deployError(eq(DISTRIBUTION_ID), eq(broken), contains("Illegal character"));
        verify(statusReporter).deployOk(DISTRIBUTION_ID, metadata2);
        verify(statusReporter).componentError(eq(DISTRIBUTION_ID), contains("vf1"));
    }

    private IArtifactInfo metadata(String uuid, String json) {
        IArtifactInfo metadata = artifact(uuid, "VF_MODULES_METADATA", uuid + ".json");
        downloads(metadata, json.getBytes(UTF_8));
        return metadata;
    }

    private void downloads(IArtifactInfo artifact, byte[] payload) {
        IDistributionClientDownloadResult result = lenientMock(IDistributionClientDownloadResult.class);
        when(result.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.SUCCESS);
        when(result.getArtifactPayload()).thenReturn(payload);
        lenient().when(client.download(artifact)).thenReturn(result);
    }

    private void downloadFails(IArtifactInfo artifact) {
        IDistributionClientDownloadResult result = lenientMock(IDistributionClientDownloadResult.class);
        when(result.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.GENERAL_ERROR);
        when(result.getDistributionMessageResult()).thenReturn("SDC unavailable");
        lenient().when(client.download(artifact)).thenReturn(result);
    }

    private static String module(String name, String... artifactUuids) {
        String artifacts = String.join("\",\"", artifactUuids);
        return """
                {"vfModuleModelName":"%s","vfModuleModelInvariantUUID":"invariant-%s",
                 "vfModuleModelVersion":"1","vfModuleModelUUID":"uuid-%s",
                 "vfModuleModelCustomizationUUID":"customization-%s","vfModuleModelDescription":"",
                 "isBase":true,"artifacts":["%s"],"properties":{}}
                """.formatted(name, name, name, name, artifacts);
    }

    private static String modules(String... modules) {
        return "[" + String.join(",", modules) + "]";
    }

    private static IResourceInstance resource(String name, String type, IArtifactInfo... artifacts) {
        IResourceInstance resource = lenientMock(IResourceInstance.class);
        when(resource.getResourceInstanceName()).thenReturn(name);
        when(resource.getResourceType()).thenReturn(type);
        when(resource.getArtifacts()).thenReturn(List.of(artifacts));
        return resource;
    }

    private static INotificationData notification(IResourceInstance... resources) {
        INotificationData notification = lenientMock(INotificationData.class);
        when(notification.getDistributionID()).thenReturn(DISTRIBUTION_ID);
        when(notification.getResources()).thenReturn(List.of(resources));
        return notification;
    }

    private static <T> T lenientMock(Class<T> type) {
        return mock(type, withSettings().strictness(Strictness.LENIENT));
    }
}
