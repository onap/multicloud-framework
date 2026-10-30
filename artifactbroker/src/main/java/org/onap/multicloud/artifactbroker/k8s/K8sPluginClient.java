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
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.multicloud.artifactbroker.sdc.VfModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

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
        if (definitionExists(name, version, chartName)) {
            LOG.info("Definition {}/{} exists, uploading its content again", name, version);
        } else {
            createDefinition(module, chartName);
        }
        uploadContent(name, version, chartName, chart);
        LOG.info("Registered {} as definition {}/{}", chartName, name, version);
    }

    private boolean definitionExists(String name, String version, String chartName)
            throws ArtifactProcessingException {
        return call("look up", name, version, chartName,
                () -> restClient.get().uri(MODULE_PATH, name, version)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange((request, response) -> response.getStatusCode().is2xxSuccessful()),
                "looking up definition " + name + "/" + version);
    }

    private void createDefinition(VfModuleModel module, String chartName) throws ArtifactProcessingException {
        Map<String, String> labels = new LinkedHashMap<>();
        labels.put("vf_module_model_uuid", module.vfModuleModelUUID());
        labels.put("vf_module_model_name", module.vfModuleModelName());
        String name = module.vfModuleModelInvariantUUID();
        String version = module.vfModuleModelCustomizationUUID();
        Map<String, Object> definition = new LinkedHashMap<>();
        definition.put("rb-name", name);
        definition.put("rb-version", version);
        definition.put("description", module.vfModuleModelDescription());
        definition.put("labels", labels);
        call("create", name, version, chartName,
                () -> restClient.post().uri(DEFINITION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(definition)
                        .retrieve()
                        .toBodilessEntity(),
                "creating definition " + name + "/" + version);
    }

    private void uploadContent(String name, String version, String chartName, byte[] chart)
            throws ArtifactProcessingException {
        call("upload the content of", name, version, chartName,
                () -> restClient.post().uri(MODULE_PATH + "/content", name, version)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(chart)
                        .retrieve()
                        .toBodilessEntity(),
                "uploading " + chartName);
    }

    private <T> T call(String operation, String name, String version, String chartName, Supplier<T> request,
            String failure) throws ArtifactProcessingException {
        fields(LOG.atInfo(), name, version, chartName)
                .log("Calling the k8s plugin to {} definition {}/{}", operation, name, version);
        long start = System.nanoTime();
        try {
            T result = request.get();
            fields(LOG.atInfo(), name, version, chartName).addKeyValue("duration_ms", millisSince(start))
                    .log("k8s plugin call to {} definition {}/{} succeeded", operation, name, version);
            return result;
        } catch (RestClientException e) {
            LoggingEventBuilder event = fields(LOG.atError(), name, version, chartName)
                    .addKeyValue("duration_ms", millisSince(start));
            if (e instanceof RestClientResponseException response) {
                event.addKeyValue("status_code", response.getStatusCode().value());
            }
            event.log("k8s plugin call to {} definition {}/{} failed: {}", operation, name, version, e.getMessage());
            throw new ArtifactProcessingException(failure + " failed: " + e.getMessage(), e);
        }
    }

    private static LoggingEventBuilder fields(LoggingEventBuilder builder, String name, String version,
            String chartName) {
        return builder.addKeyValue("rb_name", name)
                .addKeyValue("rb_version", version)
                .addKeyValue("artifact_name", chartName);
    }

    private static long millisSince(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
