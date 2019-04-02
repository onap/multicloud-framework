/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Intel. All rights reserved.
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


/**
 * Represents a VfModuleModel that a {@link Policy} can be decoded from.
 */

@SuppressWarnings("unchecked")
public class VfModuleModel {

    private String vfModuleModelName;
    private String vfModuleModelInvariantUUID;
    private String vfModuleModelVersion;
    private String vfModuleModelUUID;
    private String vfModuleModelCustomizationUUID;
    private String vfModuleModelDescription;
    private Boolean isBase;
    private List<String> artifacts;
    private Map<String, Object> properties;

    public String getVfModuleModelName() {
        return vfModuleModelName;
    }

    public String getVfModuleModelVersion() {
        return vfModuleModelVersion;
    }

    public String getVfModuleModelCustomizationUUID() {
        return vfModuleModelCustomizationUUID;
    }

    public String getVfModuleModelDescription() {
        return vfModuleModelDescription;
    }

    public List<String> getArtifacts() {
        return artifacts;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
}
