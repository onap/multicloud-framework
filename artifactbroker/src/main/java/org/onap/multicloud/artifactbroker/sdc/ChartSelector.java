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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.sdc.api.notification.IArtifactInfo;

final class ChartSelector {

    static final String HELM = "HELM";
    static final String CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT = "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT";

    private ChartSelector() {
    }

    static Optional<IArtifactInfo> select(VfModuleModel module, Map<String, IArtifactInfo> artifactsByUuid)
            throws ArtifactProcessingException {
        List<IArtifactInfo> artifacts = module.artifacts().stream()
                .map(artifactsByUuid::get)
                .filter(Objects::nonNull)
                .toList();
        for (IArtifactInfo artifact : artifacts) {
            if (HELM.equals(artifact.getArtifactType())) {
                return Optional.of(artifact);
            }
        }
        List<IArtifactInfo> cloudArtifacts = artifacts.stream()
                .filter(artifact -> CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT.equals(artifact.getArtifactType()))
                .toList();
        if (cloudArtifacts.isEmpty()) {
            return Optional.empty();
        }
        String prefix = namePrefix(module.vfModuleModelName());
        if (cloudArtifacts.size() == 1 || prefix.isEmpty()) {
            return Optional.of(cloudArtifacts.get(0));
        }
        for (IArtifactInfo artifact : cloudArtifacts) {
            if (artifact.getArtifactName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                return Optional.of(artifact);
            }
        }
        throw new ArtifactProcessingException(
                "no " + CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT + " artifact matches " + prefix);
    }

    // SDC names VF modules "<vf>..<module>..module-<n>" and the onboarded chart of <module> "<module>_<...>".
    private static String namePrefix(String vfModuleModelName) {
        String name = vfModuleModelName == null ? "" : vfModuleModelName.toLowerCase(Locale.ROOT);
        int first = name.indexOf("..");
        if (first <= 0) {
            return "";
        }
        String rest = name.substring(first + 2);
        int second = rest.indexOf("..");
        return second > 0 ? rest.substring(0, second) + "_" : "";
    }
}
