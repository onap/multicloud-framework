/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.utils.DistributionStatusEnum;

/**
 * This class represents the component done status of the distribution service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ComponentDoneStatusMessage implements IComponentDoneStatusMessage {

    private String consumerId;
    private String distributionId;
    private DistributionStatusEnum distributionStatus;
    private long timestamp;

    /**
     * Constructor for instantiating {@link ComponentDoneStatusMessage} class.
     *
     * @param messageBuilder the message builder
     */
    public ComponentDoneStatusMessage(final ComponentDoneStatusMessageBuilder messageBuilder) {
        this.consumerId = messageBuilder.getConsumerId();
        this.distributionId = messageBuilder.getDistributionId();
        this.distributionStatus = messageBuilder.getDistributionStatus();
        this.timestamp = messageBuilder.getTimestamp();
    }

    @Override
    public DistributionStatusEnum getStatus() {
        return distributionStatus;
    }

    @Override
    public String getDistributionID() {
        return distributionId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getConsumerID() {
        return consumerId;
    }

    @Override
    public String getComponentName() {
        return "POLICY";
    }
}
