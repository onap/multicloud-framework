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

import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component
public class NotificationHandler implements INotificationCallback {

    static final String VF_MODULES_METADATA = "VF_MODULES_METADATA";

    private static final Logger LOG = LoggerFactory.getLogger(NotificationHandler.class);
    private static final TypeReference<List<VfModuleModel>> MODULE_LIST = new TypeReference<>() {
    };

    private final IDistributionClient client;
    private final StatusReporter statusReporter;
    private final K8sPluginClient k8sPluginClient;
    private final JsonMapper jsonMapper;
    private final MeterRegistry meterRegistry;

    public NotificationHandler(IDistributionClient client, StatusReporter statusReporter,
            K8sPluginClient k8sPluginClient, JsonMapper jsonMapper, MeterRegistry meterRegistry) {
        this.client = client;
        this.statusReporter = statusReporter;
        this.k8sPluginClient = k8sPluginClient;
        this.jsonMapper = jsonMapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void activateCallback(INotificationData notification) {
        String distributionId = notification.getDistributionID();
        LOG.info("Received distribution {}", distributionId);
        List<String> failedVfs = new ArrayList<>();
        for (IResourceInstance resource : notification.getResources()) {
            if (!"VF".equals(resource.getResourceType())) {
                continue;
            }
            try {
                processVf(distributionId, resource).ifPresent(failedVfs::add);
            } catch (RuntimeException e) {
                LOG.error("Failed to process VF {} of distribution {}", resource.getResourceInstanceName(),
                        distributionId, e);
                failedVfs.add(resource.getResourceInstanceName() + ": " + e.getMessage());
            }
        }
        if (failedVfs.isEmpty()) {
            statusReporter.componentDone(distributionId);
        } else {
            statusReporter.componentError(distributionId, "Failed to deploy " + String.join("; ", failedVfs));
        }
        meterRegistry.counter("artifactbroker.distributions", "result", failedVfs.isEmpty() ? "success" : "failure")
                .increment();
        LOG.info("Processed distribution {}: {}", distributionId, failedVfs.isEmpty() ? "success" : failedVfs);
    }

    private Optional<String> processVf(String distributionId, IResourceInstance resource) {
        String vf = resource.getResourceInstanceName();
        Map<String, IArtifactInfo> artifactsByUuid = new HashMap<>();
        IArtifactInfo metadata = null;
        for (IArtifactInfo artifact : resource.getArtifacts()) {
            artifactsByUuid.putIfAbsent(artifact.getArtifactUUID(), artifact);
            if (metadata == null && VF_MODULES_METADATA.equals(artifact.getArtifactType())) {
                metadata = artifact;
            }
        }
        if (metadata == null) {
            LOG.debug("VF {} of distribution {} has no {}", vf, distributionId, VF_MODULES_METADATA);
            return Optional.empty();
        }

        List<VfModuleModel> modules;
        try {
            modules = parseModules(download(distributionId, metadata));
        } catch (ArtifactProcessingException | RuntimeException e) {
            return deployFailed(distributionId, vf, metadata, String.valueOf(e.getMessage()), e);
        }

        Map<String, byte[]> payloads = new HashMap<>();
        List<String> failedDownloads = new ArrayList<>();
        for (VfModuleModel module : modules) {
            for (String uuid : module.artifacts()) {
                IArtifactInfo artifact = artifactsByUuid.get(uuid);
                if (artifact == null) {
                    continue;
                }
                try {
                    payloads.put(uuid, download(distributionId, artifact));
                } catch (ArtifactProcessingException | RuntimeException e) {
                    statusReporter.deployError(distributionId, artifact, String.valueOf(e.getMessage()));
                    failedDownloads.add(String.valueOf(e.getMessage()));
                }
            }
        }
        if (!failedDownloads.isEmpty()) {
            return Optional.of(vf + ": " + String.join("; ", failedDownloads));
        }

        List<String> failedModules = new ArrayList<>();
        for (VfModuleModel module : modules) {
            try {
                Optional<IArtifactInfo> chart = ChartSelector.select(module, artifactsByUuid);
                if (chart.isEmpty()) {
                    LOG.debug("Skipping VF module {}, it has no {} or {} artifact", module.vfModuleModelName(),
                            ChartSelector.CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT, ChartSelector.HELM);
                    continue;
                }
                k8sPluginClient.registerChart(module, chart.get().getArtifactName(),
                        payloads.get(chart.get().getArtifactUUID()));
            } catch (ArtifactProcessingException | RuntimeException e) {
                LOG.error("Failed to register VF module {}", module.vfModuleModelName(), e);
                failedModules.add(module.vfModuleModelName() + ": " + e.getMessage());
            }
        }
        if (!failedModules.isEmpty()) {
            return deployFailed(distributionId, vf, metadata, "Failed to forward VF modules " + failedModules, null);
        }
        statusReporter.deployOk(distributionId, metadata);
        return Optional.empty();
    }

    private Optional<String> deployFailed(String distributionId, String vf, IArtifactInfo metadata, String reason,
            Exception cause) {
        LOG.error("Failed to deploy VF {} of distribution {}: {}", vf, distributionId, reason, cause);
        statusReporter.deployError(distributionId, metadata, reason);
        return Optional.of(vf + ": " + reason);
    }

    private List<VfModuleModel> parseModules(byte[] payload) throws ArtifactProcessingException {
        try {
            List<VfModuleModel> modules = jsonMapper.readValue(payload, MODULE_LIST);
            if (modules == null || modules.contains(null)) {
                throw new ArtifactProcessingException(VF_MODULES_METADATA + " does not hold a list of VF modules");
            }
            return modules;
        } catch (JacksonException e) {
            throw new ArtifactProcessingException("Cannot parse " + VF_MODULES_METADATA + ": " + e.getMessage(), e);
        }
    }

    private byte[] download(String distributionId, IArtifactInfo artifact) throws ArtifactProcessingException {
        IDistributionClientDownloadResult result = client.download(artifact);
        if (result.getDistributionActionResult() != DistributionActionResultEnum.SUCCESS) {
            meterRegistry.counter("artifactbroker.downloads", "result", "failure").increment();
            String reason = "Failed to download " + artifact.getArtifactName() + ": "
                    + result.getDistributionMessageResult();
            statusReporter.downloadError(distributionId, artifact, reason);
            throw new ArtifactProcessingException(reason);
        }
        meterRegistry.counter("artifactbroker.downloads", "result", "success").increment();
        statusReporter.downloadOk(distributionId, artifact);
        return result.getArtifactPayload();
    }
}
