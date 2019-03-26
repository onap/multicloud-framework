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

package org.onap.policy.distribution.reception.statistics;

/**
 * Class to hold statistical data for distribution component.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DistributionStatisticsManager {

    private static long totalDistributionCount;
    private static long distributionSuccessCount;
    private static long distributionFailureCount;
    private static long totalDownloadCount;
    private static long downloadSuccessCount;
    private static long downloadFailureCount;

    private DistributionStatisticsManager() {
        throw new IllegalStateException("Instantiation of the class is not allowed");
    }

    /**
     * Method to update the total distribution count.
     *
     * @return the updated value of totalDistributionCount
     */
    public static long updateTotalDistributionCount() {
        return ++totalDistributionCount;
    }

    /**
     * Method to update the distribution success count.
     *
     * @return the updated value of distributionSuccessCount
     */
    public static long updateDistributionSuccessCount() {
        return ++distributionSuccessCount;
    }

    /**
     * Method to update the distribution failure count.
     *
     * @return the updated value of distributionFailureCount
     */
    public static long updateDistributionFailureCount() {
        return ++distributionFailureCount;
    }

    /**
     * Method to update the total download count.
     *
     * @return the updated value of totalDownloadCount
     */
    public static long updateTotalDownloadCount() {
        return ++totalDownloadCount;
    }

    /**
     * Method to update the download success count.
     *
     * @return the updated value of downloadSuccessCount
     */
    public static long updateDownloadSuccessCount() {
        return ++downloadSuccessCount;
    }

    /**
     * Method to update the download failure count.
     *
     * @return the updated value of downloadFailureCount
     */
    public static long updateDownloadFailureCount() {
        return ++downloadFailureCount;
    }

    /**
     * Returns the current value of totalDistributionCount.
     *
     * @return the totalDistributionCount
     */
    public static long getTotalDistributionCount() {
        return totalDistributionCount;
    }

    /**
     * Returns the current value of distributionSuccessCount.
     *
     * @return the distributionSuccessCount
     */
    public static long getDistributionSuccessCount() {
        return distributionSuccessCount;
    }

    /**
     * Returns the current value of distributionFailureCount.
     *
     * @return the distributionFailureCount
     */
    public static long getDistributionFailureCount() {
        return distributionFailureCount;
    }

    /**
     * Returns the current value of totalDownloadCount.
     *
     * @return the totalDownloadCount
     */
    public static long getTotalDownloadCount() {
        return totalDownloadCount;
    }

    /**
     * Returns the current value of downloadSuccessCount.
     *
     * @return the downloadSuccessCount
     */
    public static long getDownloadSuccessCount() {
        return downloadSuccessCount;
    }

    /**
     * Returns the current value of downloadFailureCount.
     *
     * @return the downloadFailureCount
     */
    public static long getDownloadFailureCount() {
        return downloadFailureCount;
    }

    /**
     * Reset all the statistics counts to 0.
     */
    public static void resetAllStatistics() {
        totalDistributionCount = 0L;
        distributionSuccessCount = 0L;
        distributionFailureCount = 0L;
        totalDownloadCount = 0L;
        downloadSuccessCount = 0L;
        downloadFailureCount = 0L;
    }
}
