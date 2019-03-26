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

package org.onap.policy.distribution.reception.parameters;

import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.parameters.ArtifactForwarderParameters;

/**
 * Class to hold all the plugin handler parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PluginHandlerParameters implements ParameterGroup {

    private static final String PLUGIN_HANDLER = "_PluginHandler";

    private String name;
    private Map<String, ArtifactForwarderParameters> artifactForwarders;

    /**
     * Constructor for instantiating PluginHandlerParameters.
     *
     * @param artifactForwarders the map of policy forwarders
     */
    public PluginHandlerParameters(
            final Map<String, ArtifactForwarderParameters> artifactForwarders) {
        this.artifactForwarders = artifactForwarders;
    }

    /**
     * Return the artifactForwarders of this PluginHandlerParameters instance.
     *
     * @return the artifactForwarders
     */
    public Map<String, ArtifactForwarderParameters> getArtifactForwarders() {
        return artifactForwarders;
    }

    @Override
    public String getName() {
        return name + PLUGIN_HANDLER;
    }

    /**
     * Validate the plugin handler parameters.
     *
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (artifactForwarders == null || artifactForwarders.size() == 0) {
            validationResult.setResult("artifactForwarders", ValidationStatus.INVALID,
                    "must have at least one policy forwarder");
        } else {
            for (final Entry<String, ArtifactForwarderParameters> nestedGroupEntry : artifactForwarders.entrySet()) {
                validationResult.setResult("artifactForwarders", nestedGroupEntry.getKey(),
                        nestedGroupEntry.getValue().validate());
            }
        }
        return validationResult;
    }

    /**
     * Set the name of this group.
     *
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
