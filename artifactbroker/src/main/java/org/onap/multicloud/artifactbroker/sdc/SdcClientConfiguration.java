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

import java.util.List;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.sdc.api.consumer.IConfiguration;

public class SdcClientConfiguration implements IConfiguration {

    private final ArtifactBrokerProperties.Sdc sdc;

    public SdcClientConfiguration(ArtifactBrokerProperties.Sdc sdc) {
        this.sdc = sdc;
    }

    @Override
    public String getSdcAddress() {
        return sdc.address();
    }

    @Override
    public String getUser() {
        return sdc.user();
    }

    @Override
    public String getPassword() {
        return sdc.password();
    }

    @Override
    public int getPollingInterval() {
        return sdc.pollingInterval();
    }

    @Override
    public int getPollingTimeout() {
        return sdc.pollingTimeout();
    }

    @Override
    public List<String> getRelevantArtifactTypes() {
        return sdc.artifactTypes();
    }

    @Override
    public String getConsumerGroup() {
        return sdc.consumerGroup();
    }

    @Override
    public String getEnvironmentName() {
        return sdc.environmentName();
    }

    @Override
    public String getConsumerID() {
        return sdc.consumerId();
    }

    @Override
    public String getKeyStorePath() {
        return blankToNull(sdc.keyStorePath());
    }

    @Override
    public String getKeyStorePassword() {
        return blankToNull(sdc.keyStorePassword());
    }

    @Override
    public boolean activateServerTLSAuth() {
        return sdc.activateServerTlsAuth();
    }

    @Override
    public boolean isFilterInEmptyResources() {
        return sdc.filterInEmptyResources();
    }

    @Override
    public Boolean isUseHttpsWithSDC() {
        return sdc.useHttps();
    }

    @Override
    public String getHttpProxyHost() {
        return blankToNull(sdc.httpProxyHost());
    }

    @Override
    public int getHttpProxyPort() {
        return sdc.httpProxyPort();
    }

    @Override
    public String getHttpsProxyHost() {
        return blankToNull(sdc.httpsProxyHost());
    }

    @Override
    public int getHttpsProxyPort() {
        return sdc.httpsProxyPort();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
