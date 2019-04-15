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

package org.onap.policy.distribution.main.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.policy.distribution.forwarding.parameters.ArtifactForwarderParameters;
import org.onap.policy.distribution.main.testclasses.DummyArtifactForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyArtifactForwarderParameterGroup.DummyArtifactForwarderParameterGroupBuilder;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup.DummyReceptionHandlerParameterGroupBuilder;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class CommonTestData {

    private static final String REST_SERVER_PASSWORD = "zb!XztG34";
    private static final String REST_SERVER_USER = "healthcheck";
    private static final int REST_SERVER_PORT = 6969;
    private static final String REST_SERVER_HOST = "0.0.0.0";
    private static final boolean REST_SERVER_HTTPS = false;
    public static final String DISTRIBUTION_GROUP_NAME = "SDCDistributionGroup";
    public static final String FORWARDER_TYPE = "DummyForwarder";
    public static final String FORWARDER_CLASS_NAME =
            "org.onap.policy.distribution.main.testclasses.DummyArtifactForwarder";
    public static final String FORWARDER_CONFIGURATION_PARAMETERS = "dummyConfiguration";
    public static final String FORWARDER_HOST = "192.168.99.100";
    public static final String RECEPTION_HANDLER_TYPE = "DummyReceptionHandler";
    public static final String RECEPTION_HANDLER_CLASS_NAME =
            "org.onap.policy.distribution.main.testclasses.DummyReceptionHandler";
    public static final String RECEPTION_CONFIGURATION_PARAMETERS = "dummyReceptionHandlerConfiguration";
    public static final String MY_STRING_PARAMETER_VALUE = "aStringValue";
    public static final Boolean MY_BOOLEAN_PARAMETER_VALUE = true;
    public static final int MY_INTEGER_PARAMETER_VALUE = 1234;

    public static final String DUMMY_RECEPTION_HANDLER_KEY = "DummyReceptionHandler";
    public static final String DUMMY_ENGINE_FORWARDER_KEY = "DummyForwarder";

    public static final String POLICY_TYPE = "DUMMY";
    public static final String POLICY_NAME = "SamplePolicy";

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the restServerParameters object
     */
    public RestServerParameters getRestServerParameters(final boolean isEmpty) {
        final RestServerParameters restServerParameters;
        if (!isEmpty) {
            restServerParameters = new RestServerParameters(REST_SERVER_HOST, REST_SERVER_PORT, REST_SERVER_USER,
                    REST_SERVER_PASSWORD, REST_SERVER_HTTPS);
        } else {
            restServerParameters = new RestServerParameters(null, 0, null, null, REST_SERVER_HTTPS);
        }
        return restServerParameters;
    }

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the receptionHandlerParameters object
     */
    public Map<String, ReceptionHandlerParameters> getReceptionHandlerParameters(final boolean isEmpty) {
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                new HashMap<String, ReceptionHandlerParameters>();
        if (!isEmpty) {
            final Map<String, ArtifactForwarderParameters> policyForwarders = getArtifactForwarders(isEmpty);
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyForwarders);
            final ReceptionHandlerParameters rhParameters = new ReceptionHandlerParameters(RECEPTION_HANDLER_TYPE,
                    RECEPTION_HANDLER_CLASS_NAME, RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
            receptionHandlerParameters.put(DUMMY_RECEPTION_HANDLER_KEY, rhParameters);
        }
        return receptionHandlerParameters;
    }

    /**
     * Returns ReceptionHandlerConfigurationParameterGroups for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the ReceptionHandlerConfigurationParameterGroups
     */
    public Map<String, ReceptionHandlerConfigurationParameterGroup> getReceptionHandlerConfigurationParameters(
            final boolean isEmpty) {
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurationParameters =
                new HashMap<String, ReceptionHandlerConfigurationParameterGroup>();
        if (!isEmpty) {
            final List<String> messageBusAddress = new ArrayList<>();
            messageBusAddress.add("localhost");
            final List<String> artifactTypes = new ArrayList<>();
            artifactTypes.add("TOSCA_CSAR");
            final DummyReceptionHandlerParameterGroup dummyReceptionHandlerParameterGroup =
                    new DummyReceptionHandlerParameterGroupBuilder().setMyStringParameter(MY_STRING_PARAMETER_VALUE)
                            .setMyIntegerParameter(MY_INTEGER_PARAMETER_VALUE)
                            .setMyBooleanParameter(MY_BOOLEAN_PARAMETER_VALUE).build();
            receptionHandlerConfigurationParameters.put(RECEPTION_CONFIGURATION_PARAMETERS,
                    dummyReceptionHandlerParameterGroup);
        }
        return receptionHandlerConfigurationParameters;
    }

    /**
     * Returns an instance of PluginHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the pluginHandlerParameters object
     */
    public PluginHandlerParameters getPluginHandlerParameters(final boolean isEmpty) {
        final Map<String, ArtifactForwarderParameters> policyForwarders = getArtifactForwarders(isEmpty);
        final PluginHandlerParameters pluginHandlerParameters =
                new PluginHandlerParameters(policyForwarders);
        return pluginHandlerParameters;
    }

    /**
     * Returns an instance of ArtifactForwarderParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the policyForwarders object
     */
    public Map<String, ArtifactForwarderParameters> getArtifactForwarders(final boolean isEmpty) {
        final Map<String, ArtifactForwarderParameters> policyForwarders =
                new HashMap<String, ArtifactForwarderParameters>();
        if (!isEmpty) {
            final ArtifactForwarderParameters pFParameters = new ArtifactForwarderParameters(FORWARDER_TYPE,
                    FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
            policyForwarders.put(DUMMY_ENGINE_FORWARDER_KEY, pFParameters);
        }
        return policyForwarders;
    }

    /**
     * Returns ArtifactForwarderConfigurationParameterGroups for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the ArtifactForwarderConfigurationParameterGroups
     */
    public Map<String, ArtifactForwarderConfigurationParameterGroup> getArtifactForwarderConfigurationParameters(
            final boolean isEmpty) {
        final Map<String, ArtifactForwarderConfigurationParameterGroup> policyForwarderConfigurationParameters =
                new HashMap<String, ArtifactForwarderConfigurationParameterGroup>();
        if (!isEmpty) {
            final DummyArtifactForwarderParameterGroup dummyArtifactForwarderParameterGroup =
                    new DummyArtifactForwarderParameterGroupBuilder().setUseHttps(true).setHostname(FORWARDER_HOST)
                            .setPort(1234).setUserName("myUser").setPassword("myPassword").setIsManaged(true).build();
            policyForwarderConfigurationParameters.put(FORWARDER_CONFIGURATION_PARAMETERS,
                    dummyArtifactForwarderParameterGroup);
        }
        return policyForwarderConfigurationParameters;
    }

}
