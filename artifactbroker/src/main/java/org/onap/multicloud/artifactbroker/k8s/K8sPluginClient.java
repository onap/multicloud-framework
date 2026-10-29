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

package org.onap.multicloud.artifactbroker.k8s;

import java.util.LinkedHashMap;
import java.util.Map;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.multicloud.artifactbroker.sdc.VfModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class K8sPluginClient {

    private static final Logger LOG = LoggerFactory.getLogger(K8sPluginClient.class);
    private static final String DEFINITION_PATH = "/v1/rb/definition";
    private static final String MODULE_PATH = DEFINITION_PATH + "/{name}/{version}";

    private final RestClient restClient;

    public K8sPluginClient(RestClient.Builder restClientBuilder, ArtifactBrokerProperties properties) {
        this.restClient = restClientBuilder.baseUrl(properties.k8sPlugin().baseUrl().toString()).build();
    }

    public void registerChart(VfModuleModel module, String chartName, byte[] chart)
            throws ArtifactProcessingException {
        String name = module.vfModuleModelInvariantUUID();
        String version = module.vfModuleModelCustomizationUUID();
        if (definitionExists(name, version)) {
            LOG.info("Definition {}/{} exists, uploading its content again", name, version);
        } else {
            createDefinition(module);
        }
        uploadContent(name, version, chartName, chart);
        LOG.info("Registered {} as definition {}/{}", chartName, name, version);
    }

    private boolean definitionExists(String name, String version) throws ArtifactProcessingException {
        try {
            return restClient.get().uri(MODULE_PATH, name, version)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange((request, response) -> response.getStatusCode().is2xxSuccessful());
        } catch (RestClientException e) {
            throw new ArtifactProcessingException(
                    "looking up definition " + name + "/" + version + " failed: " + e.getMessage(), e);
        }
    }

    private void createDefinition(VfModuleModel module) throws ArtifactProcessingException {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("vf_module_model_uuid", module.vfModuleModelUUID());
        labels.put("vf_module_model_name", module.vfModuleModelName());
        Map<String, Object> definition = new LinkedHashMap<>();
        definition.put("rb-name", module.vfModuleModelInvariantUUID());
        definition.put("rb-version", module.vfModuleModelCustomizationUUID());
        definition.put("description", module.vfModuleModelDescription());
        definition.put("labels", labels);
        try {
            restClient.post().uri(DEFINITION_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(definition)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ArtifactProcessingException("creating definition " + module.vfModuleModelInvariantUUID() + "/"
                    + module.vfModuleModelCustomizationUUID() + " failed: " + e.getMessage(), e);
        }
    }

    private void uploadContent(String name, String version, String chartName, byte[] chart)
            throws ArtifactProcessingException {
        try {
            restClient.post().uri(MODULE_PATH + "/content", name, version)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(chart)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new ArtifactProcessingException("uploading " + chartName + " failed: " + e.getMessage(), e);
        }
    }
}
