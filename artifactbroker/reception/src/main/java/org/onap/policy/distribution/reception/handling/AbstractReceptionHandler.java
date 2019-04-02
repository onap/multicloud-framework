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

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwarder;
import org.onap.policy.distribution.forwarding.ArtifactForwardingException;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

import org.onap.sdc.api.notification.IArtifactInfo;
/**
 * Base implementation of {@link ReceptionHandler}. All reception handlers should extend this base class by implementing
 * the {@link #initializeReception(String)} method to perform the specific initialization required to receive inputs and
 * by invoking {@link #inputReceived(PolicyInput)} when the reception handler receives input.
 */
public abstract class AbstractReceptionHandler implements ReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(AbstractReceptionHandler.class);

    private PluginHandler pluginHandler;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void initialize(final String parameterGroupName) throws PluginInitializationException {
        final ReceptionHandlerParameters receptionHandlerParameters = ParameterService.get(parameterGroupName);
        pluginHandler = new PluginHandler(receptionHandlerParameters.getPluginHandlerParameters().getName());
        initializeReception(receptionHandlerParameters.getReceptionHandlerConfigurationName());
    }

    /**
     * Sub classes must implement this method to perform the specific initialization required to receive inputs, for
     * example setting up subscriptions.
     *
     * @param parameterGroupName the parameter group name
     * @throws PluginInitializationException if initialization of reception handler fails
     */
    protected abstract void initializeReception(String parameterGroupName) throws PluginInitializationException;

    /**
     * Handle input that has been received. The given input shall be decoded using the s configured
     * for this reception handler and forwarded using the {@link ArtifactForwarder}s configured for this reception
     * handler.
     *
     * @param policyInput the input that has been received
     * @throws PolicyDecodingException if an error occurs in decoding a policy from the received input
     */
    protected void inputReceived(final PolicyInput policyInput) throws PolicyDecodingException {


        for (final ArtifactForwarder policyForwarder : pluginHandler.getArtifactForwarders()) {
            try {
                policyForwarder.forward(policyInput);
            } catch (final ArtifactForwardingException policyForwardingException) {
                LOGGER.error("Error when forwarding policies to " + policyForwarder, policyForwardingException);
            }
        }
    }


}
