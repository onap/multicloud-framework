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

import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.DistributionStatusEnum;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StatusReporter {

    private static final Logger LOG = LoggerFactory.getLogger(StatusReporter.class);
    private static final String COMPONENT_NAME = "MULTICLOUD";

    private final IDistributionClient client;
    private final ArtifactBrokerProperties.Sdc sdc;

    public StatusReporter(IDistributionClient client, ArtifactBrokerProperties properties) {
        this.client = client;
        this.sdc = properties.sdc();
    }

    public void downloadOk(String distributionId, IArtifactInfo artifact) {
        ArtifactStatus status = artifactStatus(distributionId, artifact, DistributionStatusEnum.DOWNLOAD_OK);
        log(status, client.sendDownloadStatus(status));
    }

    public void downloadError(String distributionId, IArtifactInfo artifact, String reason) {
        ArtifactStatus status = artifactStatus(distributionId, artifact, DistributionStatusEnum.DOWNLOAD_ERROR);
        log(status, client.sendDownloadStatus(status, reason));
    }

    public void deployOk(String distributionId, IArtifactInfo artifact) {
        ArtifactStatus status = artifactStatus(distributionId, artifact, DistributionStatusEnum.DEPLOY_OK);
        log(status, client.sendDeploymentStatus(status));
    }

    public void deployError(String distributionId, IArtifactInfo artifact, String reason) {
        ArtifactStatus status = artifactStatus(distributionId, artifact, DistributionStatusEnum.DEPLOY_ERROR);
        log(status, client.sendDeploymentStatus(status, reason));
    }

    public void componentDone(String distributionId) {
        ComponentDoneStatus status = componentStatus(distributionId, DistributionStatusEnum.COMPONENT_DONE_OK);
        log(status, client.sendComponentDoneStatus(status));
    }

    public void componentError(String distributionId, String reason) {
        ComponentDoneStatus status = componentStatus(distributionId, DistributionStatusEnum.COMPONENT_DONE_ERROR);
        log(status, client.sendComponentDoneStatus(status, reason));
    }

    private ArtifactStatus artifactStatus(String distributionId, IArtifactInfo artifact,
            DistributionStatusEnum status) {
        return new ArtifactStatus(distributionId, sdc.consumerId(), artifact.getArtifactURL(), status,
                System.currentTimeMillis());
    }

    private ComponentDoneStatus componentStatus(String distributionId, DistributionStatusEnum status) {
        return new ComponentDoneStatus(distributionId, sdc.consumerId(), COMPONENT_NAME, status,
                System.currentTimeMillis());
    }

    private static void log(Object status, IDistributionClientResult result) {
        if (result.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS) {
            LOG.debug("Sent {}", status);
        } else {
            LOG.warn("Failed to send {}: {}", status, result.getDistributionMessageResult());
        }
    }
}
