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

package org.onap.policy.distribution.reception.handling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwarder;
import org.onap.policy.distribution.forwarding.parameters.ArtifactForwarderParameters;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;

/**
 * Handles the plugins to policy distribution.
 */
public class PluginHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(PluginHandler.class);

    private Collection<ArtifactForwarder> artifactForwarders;

    /**
     * Create an instance to instantiate plugins based on the given parameter group.
     *
     * @param parameterGroupName the name of the parameter group
     * @throws PluginInitializationException exception if it occurs
     */
    public PluginHandler(final String parameterGroupName) throws PluginInitializationException {
        final PluginHandlerParameters params = ParameterService.get(parameterGroupName);
        initArtifactForwarders(params.getArtifactForwarders());
    }

    /**
     * Get the policy forwarders.
     *
     * @return the policy forwarders
     */
    public Collection<ArtifactForwarder> getArtifactForwarders() {
        return artifactForwarders;
    }

    /**
     * Initialize policy forwarders.
     *
     * @param artifactForwarderParameters exception if it occurs
     * @throws PluginInitializationException exception if it occurs
     */
    @SuppressWarnings("unchecked")
    private void initArtifactForwarders(final Map<String, ArtifactForwarderParameters> artifactForwarderParameters)
            throws PluginInitializationException {
        artifactForwarders = new ArrayList<>();
        for (final ArtifactForwarderParameters forwarderParameters : artifactForwarderParameters.values()) {
            try {
                final Class<ArtifactForwarder> artifactForwarderClass =
                        (Class<ArtifactForwarder>) Class.forName(forwarderParameters.getForwarderClassName());
                final ArtifactForwarder artifactForwarder = artifactForwarderClass.newInstance();
                artifactForwarder.configure(forwarderParameters.getForwarderConfigurationName());
                artifactForwarders.add(artifactForwarder);
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
                LOGGER.error("exception occured while initializing forwarders", exp);
                throw new PluginInitializationException(exp.getMessage(), exp.getCause());
            }
        }
    }

}
