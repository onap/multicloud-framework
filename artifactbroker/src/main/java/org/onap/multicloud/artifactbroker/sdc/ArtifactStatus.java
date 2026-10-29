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

import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.notification.DistributionStatusEnum;

record ArtifactStatus(String distributionId, String consumerId, String artifactUrl, DistributionStatusEnum status,
        long timestamp) implements IDistributionStatusMessage {

    @Override
    public String getDistributionID() {
        return distributionId;
    }

    @Override
    public String getConsumerID() {
        return consumerId;
    }

    @Override
    public String getArtifactURL() {
        return artifactUrl;
    }

    @Override
    public DistributionStatusEnum getStatus() {
        return status;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
