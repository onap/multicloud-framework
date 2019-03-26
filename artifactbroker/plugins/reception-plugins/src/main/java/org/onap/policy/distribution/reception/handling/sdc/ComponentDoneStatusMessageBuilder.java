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

import org.onap.sdc.utils.DistributionStatusEnum;

/**
 * This class builds an instance of {@link ComponentDoneStatusMessage} class.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ComponentDoneStatusMessageBuilder {

    private String consumerId;
    private String distributionId;
    private DistributionStatusEnum distributionStatus;
    private long timestamp;

    /**
     * Returns consumer id of this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @return the consumerId
     */
    public String getConsumerId() {
        return consumerId;
    }

    /**
     * Returns distribution id of this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @return the distributionId
     */
    public String getDistributionId() {
        return distributionId;
    }

    /**
     * Returns distribution status of this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @return the distributionStatus
     */
    public DistributionStatusEnum getDistributionStatus() {
        return distributionStatus;
    }

    /**
     * Returns time of this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set consumer id url to this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @param consumerId the consumerId to set
     */
    public ComponentDoneStatusMessageBuilder setConsumerId(final String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    /**
     * Set distribution id to this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @param distributionId the distributionId to set
     */
    public ComponentDoneStatusMessageBuilder setDistributionId(final String distributionId) {
        this.distributionId = distributionId;
        return this;
    }

    /**
     * Set distribution status to this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @param distributionStatus the distributionStatus to set
     */
    public ComponentDoneStatusMessageBuilder setDistributionStatus(final DistributionStatusEnum distributionStatus) {
        this.distributionStatus = distributionStatus;
        return this;
    }

    /**
     * Set time to this {@link ComponentDoneStatusMessageBuilder} instance.
     *
     * @param timestamp the timestamp to set
     */
    public ComponentDoneStatusMessageBuilder setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
