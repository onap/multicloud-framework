/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.distribution.reception.handling.sdc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.sdc.utils.DistributionStatusEnum;

public class TestDistributionStatusMessage {

    @Test
    public void testDistributionStatusMessage_Download() {
        final String artifactUrl = "http://dummyurl";
        final String consumerId = "dummyId";
        final String distributionId = "dummyDistribution";
        final long timestamp = System.currentTimeMillis();

        final DistributionStatusMessageBuilder messageBuilder = new DistributionStatusMessageBuilder()
                .setArtifactUrl(artifactUrl).setConsumerId(consumerId).setDistributionId(distributionId)
                .setDistributionStatus(DistributionStatusEnum.DOWNLOAD_OK).setTimestamp(timestamp);
        final DistributionStatusMessage message = new DistributionStatusMessage(messageBuilder);
        assertEquals(artifactUrl, message.getArtifactURL());
        assertEquals(consumerId, message.getConsumerID());
        assertEquals(distributionId, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DOWNLOAD_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void testDistributionStatusMessage_Deploy() {
        final String artifactUrl = "http://dummyurl";
        final String consumerId = "dummyId";
        final String distributionId = "dummyDistribution";
        final long timestamp = System.currentTimeMillis();

        final DistributionStatusMessageBuilder messageBuilder = new DistributionStatusMessageBuilder()
                .setArtifactUrl(artifactUrl).setConsumerId(consumerId).setDistributionId(distributionId)
                .setDistributionStatus(DistributionStatusEnum.DEPLOY_OK).setTimestamp(timestamp);
        final DistributionStatusMessage message = new DistributionStatusMessage(messageBuilder);
        assertEquals(artifactUrl, message.getArtifactURL());
        assertEquals(consumerId, message.getConsumerID());
        assertEquals(distributionId, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DEPLOY_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }
}
