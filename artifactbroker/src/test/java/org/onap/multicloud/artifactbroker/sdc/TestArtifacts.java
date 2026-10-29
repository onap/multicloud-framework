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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mockito.quality.Strictness;
import org.onap.sdc.api.notification.IArtifactInfo;

final class TestArtifacts {

    private TestArtifacts() {
    }

    static IArtifactInfo artifact(String uuid, String type, String name) {
        IArtifactInfo artifact = mock(IArtifactInfo.class, withSettings().strictness(Strictness.LENIENT));
        when(artifact.getArtifactUUID()).thenReturn(uuid);
        when(artifact.getArtifactType()).thenReturn(type);
        when(artifact.getArtifactName()).thenReturn(name);
        when(artifact.getArtifactURL()).thenReturn("/sdc/v1/catalog/services/svc/artifacts/" + name);
        return artifact;
    }

    static Map<String, IArtifactInfo> byUuid(IArtifactInfo... artifacts) {
        return Arrays.stream(artifacts).collect(Collectors.toMap(IArtifactInfo::getArtifactUUID, Function.identity()));
    }

    static VfModuleModel module(String name, String... artifactUuids) {
        return new VfModuleModel(name, "invariant-" + name, "uuid-" + name, "customization-" + name,
                "description of " + name, List.of(artifactUuids));
    }
}
