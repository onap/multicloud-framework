/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2026 Deutsche Telekom AG. All rights reserved.
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

package org.onap.policy.distribution.forwarding.k8s;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwarder;
import org.onap.policy.distribution.forwarding.ArtifactForwardingException;
import org.onap.policy.distribution.model.CloudArtifact;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.model.VfModuleModel;
import org.onap.sdc.api.notification.IArtifactInfo;

/**
 * Forwards the charts of the VF modules to the multicloud k8s plugin as resource bundle definitions.
 */
public class K8sArtifactForwarder implements ArtifactForwarder {

    private static final Logger LOGGER = FlexLogger.getLogger(K8sArtifactForwarder.class);
    private static final String DEFINITION_PATH = "/v1/rb/definition";
    private static final String CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT = "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT";
    private static final String HELM_ARTIFACT = "HELM";

    private String definitionUrl;
    private Path dataDirectory = Paths.get("/data");

    @Override
    public void configure(final String parameterGroupName) {
        final K8sArtifactForwarderParameterGroup parameters = ParameterService.get(parameterGroupName);
        definitionUrl = (parameters.isUseHttps() ? "https" : "http") + "://" + parameters.getHostname() + ":"
                + parameters.getPort() + DEFINITION_PATH;
    }

    @Override
    public void forward(final PolicyInput policyInput) throws ArtifactForwardingException {
        if (!(policyInput instanceof CloudArtifact)) {
            LOGGER.warn("Ignoring input that is not a CloudArtifact: " + policyInput);
            return;
        }
        final CloudArtifact cloudArtifact = (CloudArtifact) policyInput;
        final List<String> failedModules = new ArrayList<>();
        for (final VfModuleModel vfModule : cloudArtifact.getVfModulePayload()) {
            try {
                forwardAndUpload(vfModule, cloudArtifact.getArtifactTypeMap());
            } catch (final ArtifactForwardingException exp) {
                LOGGER.error("Failed to forward VF module " + vfModule.getVfModuleModelName(), exp);
                failedModules.add(vfModule.getVfModuleModelName() + ": " + exp.getMessage());
            }
        }
        if (!failedModules.isEmpty()) {
            throw new ArtifactForwardingException("Failed to forward VF modules " + failedModules);
        }
    }

    private void forwardAndUpload(final VfModuleModel vfModule, final Map<String, IArtifactInfo> artifactMap)
            throws ArtifactForwardingException {
        final IArtifactInfo chart = selectChart(vfModule, artifactMap);
        if (chart == null) {
            LOGGER.debug("Skipping VF module " + vfModule.getVfModuleModelName() + ", it has no "
                    + CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT + " or " + HELM_ARTIFACT + " artifact");
            return;
        }
        final String moduleUrl = definitionUrl + "/" + vfModule.getVfModuleModelInvariantUUID() + "/"
                + vfModule.getVfModuleModelCustomizationUUID();
        if (definitionExists(moduleUrl)) {
            LOGGER.debug("Definition " + moduleUrl + " already exists, uploading its content again");
        } else {
            createDefinition(vfModule);
        }
        uploadArtifact(vfModule, chart, moduleUrl);
    }

    private IArtifactInfo selectChart(final VfModuleModel vfModule, final Map<String, IArtifactInfo> artifactMap)
            throws ArtifactForwardingException {
        String vfNamePrefix = vfModule.getVfModuleModelName().toLowerCase();
        if (vfNamePrefix.indexOf("..") > 0) {
            vfNamePrefix = vfNamePrefix.substring(vfNamePrefix.indexOf("..") + 2);
            if (vfNamePrefix.indexOf("..") > 0) {
                vfNamePrefix = vfNamePrefix.substring(0, vfNamePrefix.indexOf("..")) + "_";
            } else {
                vfNamePrefix = "";
            }
        } else {
            vfNamePrefix = "";
        }

        for (final String artifact : vfModule.getArtifacts()) {
            if (artifactMap.get(artifact) != null
                    && artifactMap.get(artifact).getArtifactType().equals(HELM_ARTIFACT)) {
                return artifactMap.get(artifact);
            }
        }

        IArtifactInfo firstCloudArtifact = null;
        int cloudArtifactCount = 0;
        for (final String artifact : vfModule.getArtifacts()) {
            final IArtifactInfo cloudArtifact = artifactMap.get(artifact);
            if (cloudArtifact != null && cloudArtifact.getArtifactType().equals(CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT)) {
                if (cloudArtifactCount == 0) {
                    firstCloudArtifact = cloudArtifact;
                }
                cloudArtifactCount++;
                if (cloudArtifact.getArtifactName().toLowerCase().startsWith(vfNamePrefix)) {
                    return cloudArtifact;
                }
            }
        }

        if (firstCloudArtifact == null || cloudArtifactCount == 1 || vfNamePrefix.isEmpty()) {
            return firstCloudArtifact;
        }
        throw new ArtifactForwardingException("no " + CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT + " artifact matches "
                + vfNamePrefix);
    }

    private boolean definitionExists(final String moduleUrl) throws ArtifactForwardingException {
        final HttpGet httpGet = new HttpGet(moduleUrl);
        httpGet.addHeader("Accept", "application/json");
        return isSuccess(execute(httpGet));
    }

    private void createDefinition(final VfModuleModel vfModule) throws ArtifactForwardingException {
        final HttpPost httpPost = new HttpPost(definitionUrl);
        httpPost.addHeader("Accept", "application/json");

        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("rb-name", vfModule.getVfModuleModelInvariantUUID());
        map.put("rb-version", vfModule.getVfModuleModelCustomizationUUID());
        map.put("descritpion", vfModule.getVfModuleModelDescription());
        final Map<String, String> labelMap = new LinkedHashMap<>();
        labelMap.put("vf_module_model_uuid", vfModule.getVfModuleModelUUID());
        labelMap.put("vf_module_model_name", vfModule.getVfModuleModelName());
        map.put("labels", labelMap);
        final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        httpPost.setEntity(new StringEntity(gson.toJson(map), ContentType.APPLICATION_JSON));

        final int status = execute(httpPost);
        if (!isSuccess(status)) {
            throw new ArtifactForwardingException("creating the definition returned HTTP " + status);
        }
    }

    private void uploadArtifact(final VfModuleModel vfModule, final IArtifactInfo chart, final String moduleUrl)
            throws ArtifactForwardingException {
        final HttpPost httpPost = new HttpPost(moduleUrl + "/content");
        httpPost.addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");
        httpPost.setEntity(new FileEntity(dataDirectory.resolve(vfModule.getVfModuleModelCustomizationUUID())
                .resolve(chart.getArtifactName()).toFile()));

        final int status = execute(httpPost);
        if (!isSuccess(status)) {
            throw new ArtifactForwardingException("uploading " + chart.getArtifactName() + " returned HTTP " + status);
        }
    }

    private static int execute(final HttpUriRequest request) throws ArtifactForwardingException {
        LOGGER.debug("Sending " + request);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {
            final int status = response.getStatusLine().getStatusCode();
            final String body = response.getEntity() == null ? ""
                    : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            LOGGER.debug(request + " returned HTTP " + status + ": " + body);
            return status;
        } catch (final IOException exp) {
            throw new ArtifactForwardingException(request + " failed: " + exp.getMessage(), exp);
        }
    }

    private static boolean isSuccess(final int status) {
        return status >= 200 && status < 300;
    }
}
