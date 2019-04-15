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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.main.testclasses.DummyArtifactForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to perform unit test of DistributionParameterGroup.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionParameterGroup {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testDistributionParameterGroup() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals(restServerParameters.getHost(), distributionParameters.getRestServerParameters().getHost());
        assertEquals(restServerParameters.getPort(), distributionParameters.getRestServerParameters().getPort());
        assertEquals(restServerParameters.getUserName(),
                distributionParameters.getRestServerParameters().getUserName());
        assertEquals(restServerParameters.getPassword(),
                distributionParameters.getRestServerParameters().getPassword());
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertEquals(CommonTestData.MY_STRING_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).getMyStringParameter());
        assertEquals(CommonTestData.MY_INTEGER_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).getMyIntegerParameter());
        assertEquals(CommonTestData.MY_BOOLEAN_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).isMyBooleanParameter());
        assertEquals(CommonTestData.FORWARDER_HOST,
                ((DummyArtifactForwarderParameterGroup) distributionParameters.getArtifactForwarderConfigurationParameters()
                        .get(CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS)).getHostname());
    }

    @Test
    public void testDistributionParameterGroup_NullName() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup(null, restServerParameters, receptionHandlerParameters,
                        receptionHandlerConfigurations, forwarderConfigurations);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals(null, distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertTrue(validationResult.getResult().contains(
                "field \"name\" type \"java.lang.String\" value \"null\" INVALID, " + "must be a non-blank string"));
    }

    @Test
    public void testDistributionParameterGroup_EmptyName() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup("", restServerParameters, receptionHandlerParameters,
                        receptionHandlerConfigurations, forwarderConfigurations);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals("", distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertTrue(validationResult.getResult().contains(
                "field \"name\" type \"java.lang.String\" value \"\" INVALID, " + "must be a non-blank string"));
    }

    @Test
    public void testDistributionParameterGroup_NullReceptionHandlerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);
        try {
            final DistributionParameterGroup distributionParameters =
                    new DistributionParameterGroup(CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, null,
                            receptionHandlerConfigurations, forwarderConfigurations);
            distributionParameters.validate();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"receptionHandlerParameters\" is null"));
        }

    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandlerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(true);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);
        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations);
        distributionParameters.validate();
        final GroupValidationResult result = distributionParameters.validate();
        assertFalse(result.isValid());
        assertTrue(result.getResult().endsWith("must have at least one reception handler\n"));
    }

    @Test
    public void testDistributionParameterGroup_EmptyRestServerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(true);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, ArtifactForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getArtifactForwarderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("\"org.onap.policy.distribution.main.parameters.RestServerParameters\" INVALID, "
                        + "parameter group has status INVALID"));
    }
}
