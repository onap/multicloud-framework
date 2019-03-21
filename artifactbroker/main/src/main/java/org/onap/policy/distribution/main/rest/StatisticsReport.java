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

package org.onap.policy.distribution.main.rest;

/**
 * Class to represent statistics report of distribution service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class StatisticsReport {

    private int code;
    private long totalDistributionCount;
    private long distributionSuccessCount;
    private long distributionFailureCount;
    private long totalDownloadCount;
    private long downloadSuccessCount;
    private long downloadFailureCount;

    /**
     * Returns the code of this {@link StatisticsReport} instance.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Set code in this {@link StatisticsReport} instance.
     *
     * @param code the code to set
     */
    public void setCode(final int code) {
        this.code = code;
    }

    /**
     * Returns the totalDistributionCount of this {@link StatisticsReport} instance.
     *
     * @return the totalDistributionCount
     */
    public long getTotalDistributionCount() {
        return totalDistributionCount;
    }

    /**
     * Set totalDistributionCount in this {@link StatisticsReport} instance.
     *
     * @param totalDistributionCount the totalDistributionCount to set
     */
    public void setTotalDistributionCount(final long totalDistributionCount) {
        this.totalDistributionCount = totalDistributionCount;
    }

    /**
     * Returns the distributionSuccessCount of this {@link StatisticsReport} instance.
     *
     * @return the distributionSuccessCount
     */
    public long getDistributionSuccessCount() {
        return distributionSuccessCount;
    }

    /**
     * Set distributionSuccessCount in this {@link StatisticsReport} instance.
     *
     * @param distributionSuccessCount the distributionSuccessCount to set
     */
    public void setDistributionSuccessCount(final long distributionSuccessCount) {
        this.distributionSuccessCount = distributionSuccessCount;
    }

    /**
     * Returns the distributionFailureCount of this {@link StatisticsReport} instance.
     *
     * @return the distributionFailureCount
     */
    public long getDistributionFailureCount() {
        return distributionFailureCount;
    }

    /**
     * Set distributionFailureCount in this {@link StatisticsReport} instance.
     *
     * @param distributionFailureCount the distributionFailureCount to set
     */
    public void setDistributionFailureCount(final long distributionFailureCount) {
        this.distributionFailureCount = distributionFailureCount;
    }

    /**
     * Returns the totalDownloadCount of this {@link StatisticsReport} instance.
     *
     * @return the totalDownloadCount
     */
    public long getTotalDownloadCount() {
        return totalDownloadCount;
    }

    /**
     * Set totalDownloadCount in this {@link StatisticsReport} instance.
     *
     * @param totalDownloadCount the totalDownloadCount to set
     */
    public void setTotalDownloadCount(final long totalDownloadCount) {
        this.totalDownloadCount = totalDownloadCount;
    }

    /**
     * Returns the downloadSuccessCount of this {@link StatisticsReport} instance.
     *
     * @return the downloadSuccessCount
     */
    public long getDownloadSuccessCount() {
        return downloadSuccessCount;
    }

    /**
     * Set downloadSuccessCount in this {@link StatisticsReport} instance.
     *
     * @param downloadSuccessCount the downloadSuccessCount to set
     */
    public void setDownloadSuccessCount(final long downloadSuccessCount) {
        this.downloadSuccessCount = downloadSuccessCount;
    }

    /**
     * Returns the downloadFailureCount of this {@link StatisticsReport} instance.
     *
     * @return the downloadFailureCount
     */
    public long getDownloadFailureCount() {
        return downloadFailureCount;
    }

    /**
     * Set downloadFailureCount in this {@link StatisticsReport} instance.
     *
     * @param downloadFailureCount the downloadFailureCount to set
     */
    public void setDownloadFailureCount(final long downloadFailureCount) {
        this.downloadFailureCount = downloadFailureCount;
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("StatisticsReport [code=");
        builder.append(getCode());
        builder.append(", totalDistributionCount=");
        builder.append(getTotalDistributionCount());
        builder.append(", distributionSuccessCount=");
        builder.append(getDistributionSuccessCount());
        builder.append(", distributionFailureCount=");
        builder.append(getDistributionFailureCount());
        builder.append(", totalDownloadCount=");
        builder.append(getTotalDownloadCount());
        builder.append(", downloadSuccessCount=");
        builder.append(getDownloadSuccessCount());
        builder.append(", downloadFailureCount=");
        builder.append(getDownloadFailureCount());
        builder.append("]");
        return builder.toString();
    }
}
