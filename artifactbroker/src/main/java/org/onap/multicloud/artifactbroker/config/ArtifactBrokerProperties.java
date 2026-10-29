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

package org.onap.multicloud.artifactbroker.config;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("artifactbroker")
public record ArtifactBrokerProperties(@DefaultValue Sdc sdc, @DefaultValue K8sPlugin k8sPlugin) {

    public record Sdc(
            @DefaultValue("true") boolean enabled,
            String address,
            String user,
            String password,
            String consumerId,
            String consumerGroup,
            @DefaultValue("AUTO") String environmentName,
            @DefaultValue("20") int pollingInterval,
            @DefaultValue("30") int pollingTimeout,
            @DefaultValue("30s") Duration retryDelay,
            @DefaultValue({"TOSCA_CSAR", "HEAT", "HEAT_ARTIFACT", "HEAT_ENV", "HEAT_NESTED", "HEAT_VOL", "OTHER",
                "VF_MODULES_METADATA", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "HELM"})
            List<String> artifactTypes,
            @DefaultValue("true") boolean useHttps,
            @DefaultValue("false") boolean activateServerTlsAuth,
            @DefaultValue("true") boolean filterInEmptyResources,
            String keyStorePath,
            String keyStorePassword,
            String httpProxyHost,
            @DefaultValue("0") int httpProxyPort,
            String httpsProxyHost,
            @DefaultValue("0") int httpsProxyPort) {
    }

    public record K8sPlugin(@DefaultValue("http://localhost:9015") URI baseUrl) {
    }
}
