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

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to hold all the reception handler parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ReceptionHandlerParameters implements ParameterGroup {

    private static final Logger LOGGER = FlexLogger.getLogger(ReceptionHandlerParameters.class);

    private String name;
    private String receptionHandlerType;
    private String receptionHandlerClassName;
    private String receptionHandlerConfigurationName;
    private PluginHandlerParameters pluginHandlerParameters;

    /**
     * Constructor for instantiating ReceptionHandlerParameters.
     *
     * @param receptionHandlerType the reception handler type
     * @param receptionHandlerClassName the reception handler class name
     * @param receptionHandlerConfigurationName the name of the configuration for the reception
     *        handler
     * @param pluginHandlerParameters the plugin handler parameters
     */
    public ReceptionHandlerParameters(final String receptionHandlerType, final String receptionHandlerClassName,
            final String receptionHandlerConfigurationName, final PluginHandlerParameters pluginHandlerParameters) {
        this.receptionHandlerType = receptionHandlerType;
        this.receptionHandlerClassName = receptionHandlerClassName;
        this.receptionHandlerConfigurationName = receptionHandlerConfigurationName;
        this.pluginHandlerParameters = pluginHandlerParameters;
    }

    /**
     * Return the receptionHandlerType of this ReceptionHandlerParameters instance.
     *
     * @return the receptionHandlerType
     */
    public String getReceptionHandlerType() {
        return receptionHandlerType;
    }

    /**
     * Return the receptionHandlerClassName of this ReceptionHandlerParameters instance.
     *
     * @return the receptionHandlerClassName
     */
    public String getReceptionHandlerClassName() {
        return receptionHandlerClassName;
    }

    /**
     * Return the name of the reception handler configuration for this ReceptionHandlerParameters
     * instance.
     *
     * @return the PssdConfigurationParametersGroup
     */
    public String getReceptionHandlerConfigurationName() {
        return receptionHandlerConfigurationName;
    }

    /**
     * Return the pluginHandlerParameters of this ReceptionHandlerParameters instance.
     *
     * @return the pluginHandlerParameters
     */
    public PluginHandlerParameters getPluginHandlerParameters() {
        return pluginHandlerParameters;
    }

    @Override
    public String getName() {
        return name + "_" + receptionHandlerType;
    }

    /**
     * Validate the reception handler parameters.
     *
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (receptionHandlerType == null || receptionHandlerType.trim().length() == 0) {
            validationResult.setResult("receptionHandlerType", ValidationStatus.INVALID, "must be a non-blank string");
        }
        if (receptionHandlerClassName == null || receptionHandlerClassName.trim().length() == 0) {
            validationResult.setResult("receptionHandlerClassName", ValidationStatus.INVALID,
                    "must be a non-blank string containing full class name of the reception handler");
        } else {
            validateReceptionHandlerClass(validationResult);
        }
        if (pluginHandlerParameters == null) {
            validationResult.setResult("pluginHandlerParameters", ValidationStatus.INVALID,
                    "must have a plugin handler");
        } else {
            validationResult.setResult("pluginHandlerParameters", pluginHandlerParameters.validate());
        }
        return validationResult;
    }

    private void validateReceptionHandlerClass(final GroupValidationResult validationResult) {
        try {
            Class.forName(receptionHandlerClassName);
        } catch (final ClassNotFoundException exp) {
            LOGGER.error("reception handler class not found in classpath", exp);
            validationResult.setResult("receptionHandlerClassName", ValidationStatus.INVALID,
                    "reception handler class not found in classpath");
        }
    }

    /**
     * Set the name of this group.
     *
     * @param name the name to set
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }
}
