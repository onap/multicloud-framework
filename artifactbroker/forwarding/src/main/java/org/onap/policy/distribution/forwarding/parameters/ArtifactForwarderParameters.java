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

package org.onap.policy.distribution.forwarding.parameters;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to hold all the Artifact forwarder parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ArtifactForwarderParameters implements ParameterGroup {

    private static final Logger LOGGER = FlexLogger.getLogger(ArtifactForwarderParameters.class);

    private String forwarderType;
    private String forwarderClassName;
    private String forwarderConfigurationName;

    /**
     * Constructor for instantiating ArtifactForwarderParameters.
     *
     * @param forwarderType the Artifact forwarder type
     * @param forwarderClassName the Artifact forwarder class name
     * @param forwarderConfigurationName the name of the configuration for the Artifact forwarder
     */
    public ArtifactForwarderParameters(final String forwarderType, final String forwarderClassName,
            final String forwarderConfigurationName) {
        this.forwarderType = forwarderType;
        this.forwarderClassName = forwarderClassName;
        this.forwarderConfigurationName = forwarderConfigurationName;
    }

    /**
     * Return the forwarderType of this ArtifactForwarderParameters instance.
     *
     * @return the forwarderType
     */
    public String getForwarderType() {
        return forwarderType;
    }

    /**
     * Return the forwarderClassName of this ArtifactForwarderParameters instance.
     *
     * @return the forwarderClassName
     */
    public String getForwarderClassName() {
        return forwarderClassName;
    }

    /**
     * Return the name of the forwarder configuration of this ArtifactForwarderParameters instance.
     *
     * @return the the name of the forwarder configuration
     */
    public String getForwarderConfigurationName() {
        return forwarderConfigurationName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return getForwarderType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setName(final String name) {
        this.forwarderType = name;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (forwarderType == null || forwarderType.trim().length() == 0) {
            validationResult.setResult("forwarderType", ValidationStatus.INVALID, "must be a non-blank string");
        }
        if (forwarderClassName == null || forwarderClassName.trim().length() == 0) {
            validationResult.setResult("forwarderClassName", ValidationStatus.INVALID,
                    "must be a non-blank string containing full class name of the forwarder");
        } else {
            validateArtifactForwarderClass(validationResult);
        }
        return validationResult;
    }

    private void validateArtifactForwarderClass(final GroupValidationResult validationResult) {
        try {
            Class.forName(forwarderClassName);
        } catch (final ClassNotFoundException exp) {
            LOGGER.trace("Artifact forwarder class not found in classpath", exp);
            validationResult.setResult("forwarderClassName", ValidationStatus.INVALID,
                    "Artifact forwarder class not found in classpath");
        }
    }
}
