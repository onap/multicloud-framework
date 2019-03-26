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
 * This class builds an instance of {@link DistributionStatusMessage} class.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DistributionStatusMessageBuilder {

    private String artifactUrl;
    private String consumerId;
    private String distributionId;
    private DistributionStatusEnum distributionStatus;
    private long timestamp;

    /**
     * Returns artifact url of this {@link DistributionStatusMessageBuilder} instance.
     *
     * @return the artifactUrl
     */
    public String getArtifactUrl() {
        return artifactUrl;
    }

    /**
     * Returns consumer id of this {@link DistributionStatusMessageBuilder} instance.
     *
     * @return the consumerId
     */
    public String getConsumerId() {
        return consumerId;
    }

    /**
     * Returns distribution id of this {@link DistributionStatusMessageBuilder} instance.
     *
     * @return the distributionId
     */
    public String getDistributionId() {
        return distributionId;
    }

    /**
     * Returns distribution status of this {@link DistributionStatusMessageBuilder} instance.
     *
     * @return the distributionStatus
     */
    public DistributionStatusEnum getDistributionStatus() {
        return distributionStatus;
    }

    /**
     * Returns time of this {@link DistributionStatusMessageBuilder} instance.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set artifact url to this {@link DistributionStatusMessageBuilder} instance.
     *
     * @param artifactUrl the artifactUrl to set
     */
    public DistributionStatusMessageBuilder setArtifactUrl(final String artifactUrl) {
        this.artifactUrl = artifactUrl;
        return this;
    }

    /**
     * Set consumer id url to this {@link DistributionStatusMessageBuilder} instance.
     *
     * @param consumerId the consumerId to set
     */
    public DistributionStatusMessageBuilder setConsumerId(final String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    /**
     * Set distribution id to this {@link DistributionStatusMessageBuilder} instance.
     *
     * @param distributionId the distributionId to set
     */
    public DistributionStatusMessageBuilder setDistributionId(final String distributionId) {
        this.distributionId = distributionId;
        return this;
    }

    /**
     * Set distribution status to this {@link DistributionStatusMessageBuilder} instance.
     *
     * @param distributionStatus the distributionStatus to set
     */
    public DistributionStatusMessageBuilder setDistributionStatus(final DistributionStatusEnum distributionStatus) {
        this.distributionStatus = distributionStatus;
        return this;
    }

    /**
     * Set time to this {@link DistributionStatusMessageBuilder} instance.
     *
     * @param timestamp the timestamp to set
     */
    public DistributionStatusMessageBuilder setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
