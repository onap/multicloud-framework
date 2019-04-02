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

package org.onap.policy.distribution.model;

import java.util.List;
import java.util.Map;

import org.onap.sdc.api.notification.IArtifactInfo;

/**
 * Represents a CloudArtifact that a {@link Policy} can be decoded from.
 */
public class CloudArtifact implements PolicyInput {

    List<VfModuleModel> vfModulePayload;
    Map<String, IArtifactInfo> artifactMap;

    public CloudArtifact(List<VfModuleModel> vfModulePayload, Map<String, IArtifactInfo> artifactMap) {
        this.vfModulePayload = vfModulePayload;
        this.artifactMap = artifactMap;
    }

    /**
     * Get the path to the TOSCA file.
     *
     * @return the path of the TOSCA file
     */
    public List<VfModuleModel> getVfModulePayload() {
        return vfModulePayload;
    }

    /**
     * Get the path to the TOSCA file.
     *
     * @return the path of the TOSCA file
     */
    public Map<String, IArtifactInfo> getArtifactTypeMap() {
        return artifactMap;
    }

}
