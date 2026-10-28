/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2026 Deutsche Telekom AG. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwarder;
import org.onap.policy.distribution.forwarding.ArtifactForwardingException;
import org.onap.policy.distribution.forwarding.parameters.ArtifactForwarderParameters;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

public class TestAbstractReceptionHandler {

    private static final String HANDLER_NAME = "failingHandler";

    private PluginHandlerParameters pluginParameters;
    private ReceptionHandlerParameters handlerParameters;

    /**
     * Registers a reception handler whose only forwarder fails.
     */
    @Before
    public void setUp() {
        pluginParameters = new PluginHandlerParameters(Collections.singletonMap("failing",
                new ArtifactForwarderParameters("Failing", FailingArtifactForwarder.class.getName(), null)));
        pluginParameters.setName(HANDLER_NAME);
        handlerParameters = new ReceptionHandlerParameters("Dummy", DummyReceptionHandler.class.getName(), null,
                pluginParameters);
        handlerParameters.setName(HANDLER_NAME);
        ParameterService.register(pluginParameters);
        ParameterService.register(handlerParameters);
    }

    @After
    public void tearDown() {
        ParameterService.deregister(handlerParameters.getName());
        ParameterService.deregister(pluginParameters.getName());
    }

    @Test
    public void forwardingFailureIsPropagated() throws Exception {
        final DummyReceptionHandler handler = new DummyReceptionHandler();
        handler.initialize(handlerParameters.getName());

        try {
            handler.inputReceived(new PolicyInput() {});
            fail("forwarding failure must be propagated");
        } catch (final ArtifactForwardingException exp) {
            assertEquals("forwarding failed", exp.getMessage());
        }
    }

    public static class FailingArtifactForwarder implements ArtifactForwarder {

        @Override
        public void configure(final String parameterGroupName) {}

        @Override
        public void forward(final PolicyInput policyInput) throws ArtifactForwardingException {
            throw new ArtifactForwardingException("forwarding failed");
        }
    }
}
