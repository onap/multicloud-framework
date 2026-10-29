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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.multicloud.artifactbroker.config.TestProperties;

class SdcClientConfigurationTest {

    @Test
    void defaultsMatchTheOomDeployment() {
        ArtifactBrokerProperties properties = TestProperties.bind(Map.of());

        assertThat(properties.sdc().enabled()).isTrue();
        assertThat(properties.sdc().environmentName()).isEqualTo("AUTO");
        assertThat(properties.sdc().pollingInterval()).isEqualTo(20);
        assertThat(properties.sdc().pollingTimeout()).isEqualTo(30);
        assertThat(properties.sdc().retryDelay()).isEqualTo(Duration.ofSeconds(30));
        assertThat(properties.sdc().artifactTypes()).containsExactly("TOSCA_CSAR", "HEAT", "HEAT_ARTIFACT",
                "HEAT_ENV", "HEAT_NESTED", "HEAT_VOL", "OTHER", "VF_MODULES_METADATA",
                "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "HELM");
        assertThat(properties.sdc().useHttps()).isTrue();
        assertThat(properties.sdc().filterInEmptyResources()).isTrue();
        assertThat(properties.k8sPlugin().baseUrl()).isEqualTo(URI.create("http://localhost:9015"));
    }

    @Test
    void configurationIsPassedToTheSdcClient() {
        SdcClientConfiguration configuration = new SdcClientConfiguration(TestProperties.bind(Map.of(
                "artifactbroker.sdc.address", "sdc-be.onap:8443",
                "artifactbroker.sdc.user", "multicloud",
                "artifactbroker.sdc.password", "secret",
                "artifactbroker.sdc.consumer-id", "multicloud-k8s",
                "artifactbroker.sdc.consumer-group", "multicloud",
                "artifactbroker.sdc.use-https", "false",
                "artifactbroker.sdc.https-proxy-host", "proxy",
                "artifactbroker.sdc.https-proxy-port", "3128")).sdc());

        assertThat(configuration.getSdcAddress()).isEqualTo("sdc-be.onap:8443");
        assertThat(configuration.getUser()).isEqualTo("multicloud");
        assertThat(configuration.getPassword()).isEqualTo("secret");
        assertThat(configuration.getConsumerID()).isEqualTo("multicloud-k8s");
        assertThat(configuration.getConsumerGroup()).isEqualTo("multicloud");
        assertThat(configuration.isUseHttpsWithSDC()).isFalse();
        assertThat(configuration.getHttpsProxyHost()).isEqualTo("proxy");
        assertThat(configuration.getHttpsProxyPort()).isEqualTo(3128);
        assertThat(configuration.getRelevantArtifactTypes()).contains("VF_MODULES_METADATA");
    }

    @Test
    void blankOptionalValuesReachTheSdcClientAsNull() {
        SdcClientConfiguration configuration = new SdcClientConfiguration(TestProperties.bind(Map.of(
                "artifactbroker.sdc.key-store-path", "",
                "artifactbroker.sdc.key-store-password", "",
                "artifactbroker.sdc.http-proxy-host", " ",
                "artifactbroker.sdc.https-proxy-host", "")).sdc());

        assertThat(configuration.getKeyStorePath()).isNull();
        assertThat(configuration.getKeyStorePassword()).isNull();
        assertThat(configuration.getHttpProxyHost()).isNull();
        assertThat(configuration.getHttpsProxyHost()).isNull();
    }
}
