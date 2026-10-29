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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.onap.multicloud.artifactbroker.sdc.TestArtifacts.artifact;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onap.multicloud.artifactbroker.config.TestProperties;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.notification.DistributionStatusEnum;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;

class StatusReporterTest {

    private final IDistributionClient client = mock(IDistributionClient.class);
    private final IArtifactInfo metadata = artifact("meta", "VF_MODULES_METADATA", "vfModulesMetadata.json");
    private StatusReporter reporter;

    @BeforeEach
    void setUp() {
        IDistributionClientResult success = mock(IDistributionClientResult.class);
        when(success.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.SUCCESS);
        when(client.sendDownloadStatus(any())).thenReturn(success);
        when(client.sendDownloadStatus(any(), anyString())).thenReturn(success);
        when(client.sendDeploymentStatus(any())).thenReturn(success);
        when(client.sendDeploymentStatus(any(), anyString())).thenReturn(success);
        when(client.sendComponentDoneStatus(any())).thenReturn(success);
        when(client.sendComponentDoneStatus(any(), anyString())).thenReturn(success);
        reporter = new StatusReporter(client, TestProperties.bind(Map.of(
                "artifactbroker.sdc.consumer-id", "multicloud-k8s")));
    }

    @Test
    void deployErrorCarriesTheArtifactUrlAndReason() {
        reporter.deployError("dist-1", metadata, "HTTP 500");

        ArgumentCaptor<IDistributionStatusMessage> message = ArgumentCaptor.forClass(IDistributionStatusMessage.class);
        verify(client).sendDeploymentStatus(message.capture(), eq("HTTP 500"));
        assertThat(message.getValue().getDistributionID()).isEqualTo("dist-1");
        assertThat(message.getValue().getConsumerID()).isEqualTo("multicloud-k8s");
        assertThat(message.getValue().getArtifactURL()).isEqualTo(metadata.getArtifactURL());
        assertThat(message.getValue().getStatus()).isEqualTo(DistributionStatusEnum.DEPLOY_ERROR);
        assertThat(message.getValue().getTimestamp()).isPositive();
    }

    @Test
    void downloadOkIsSentWithoutReason() {
        reporter.downloadOk("dist-1", metadata);

        ArgumentCaptor<IDistributionStatusMessage> message = ArgumentCaptor.forClass(IDistributionStatusMessage.class);
        verify(client).sendDownloadStatus(message.capture());
        assertThat(message.getValue().getStatus()).isEqualTo(DistributionStatusEnum.DOWNLOAD_OK);
    }

    @Test
    void componentDoneIsSentAsMulticloud() {
        reporter.componentDone("dist-1");

        ArgumentCaptor<IComponentDoneStatusMessage> message =
                ArgumentCaptor.forClass(IComponentDoneStatusMessage.class);
        verify(client).sendComponentDoneStatus(message.capture());
        assertThat(message.getValue().getComponentName()).isEqualTo("MULTICLOUD");
        assertThat(message.getValue().getStatus()).isEqualTo(DistributionStatusEnum.COMPONENT_DONE_OK);
    }

    @Test
    void componentErrorCarriesTheReason() {
        reporter.componentError("dist-1", "VF vfw: HTTP 500");

        ArgumentCaptor<IComponentDoneStatusMessage> message =
                ArgumentCaptor.forClass(IComponentDoneStatusMessage.class);
        verify(client).sendComponentDoneStatus(message.capture(), eq("VF vfw: HTTP 500"));
        assertThat(message.getValue().getStatus()).isEqualTo(DistributionStatusEnum.COMPONENT_DONE_ERROR);
    }

    @Test
    void failedSendIsNotThrown() {
        IDistributionClientResult failure = mock(IDistributionClientResult.class);
        when(failure.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.GENERAL_ERROR);
        when(client.sendDeploymentStatus(any())).thenReturn(failure);

        reporter.deployOk("dist-1", metadata);

        verify(client).sendDeploymentStatus(any());
    }
}
