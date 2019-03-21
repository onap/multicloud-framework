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

import org.onap.policy.common.endpoints.report.HealthCheckReport;
import org.onap.policy.distribution.main.startstop.DistributionActivator;

/**
 * Class to fetch health check of distribution service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class HealthCheckProvider {

    private static final String NOT_ALIVE = "not alive";
    private static final String ALIVE = "alive";
    private static final String URL = "self";
    private static final String NAME = "Policy SSD";

    /**
     * Performs the health check of distribution service.
     *
     * @return Report containing health check status
     */
    public HealthCheckReport performHealthCheck() {
        final HealthCheckReport report = new HealthCheckReport();
        report.setName(NAME);
        report.setUrl(URL);
        report.setHealthy(DistributionActivator.isAlive());
        report.setCode(DistributionActivator.isAlive() ? 200 : 500);
        report.setMessage(DistributionActivator.isAlive() ? ALIVE : NOT_ALIVE);
        return report;
    }
}
