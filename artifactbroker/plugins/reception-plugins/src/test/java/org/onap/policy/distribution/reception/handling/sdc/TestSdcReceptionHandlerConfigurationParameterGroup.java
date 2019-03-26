/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.sdc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

/**
 * Class to perform unit test of {@link SdcConfiguration}.
 *
 */
public class TestSdcReceptionHandlerConfigurationParameterGroup {

    @Test
    public void testSdcConfiguration() throws IOException {
        SdcReceptionHandlerConfigurationParameterGroup configParameters = null;
        try {
            final Gson gson = new GsonBuilder().create();
            configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
                    SdcReceptionHandlerConfigurationParameterGroup.class);
        } catch (final Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
        final GroupValidationResult validationResult = configParameters.validate();
        assertTrue(validationResult.isValid());
        final SdcConfiguration config = new SdcConfiguration(configParameters);
        assertEquals(Arrays.asList("a.com", "b.com", "c.com"), config.getMsgBusAddress());
        assertEquals(Arrays.asList("TOSCA_CSAR", "HEAT"), config.getRelevantArtifactTypes());
        assertEquals("localhost", config.getAsdcAddress());
        assertEquals("policy", config.getUser());
        assertEquals("policy", config.getPassword());
        assertEquals(20, config.getPollingInterval());
        assertEquals(30, config.getPollingTimeout());
        assertEquals("policy-id", config.getConsumerID());
        assertEquals("policy-group", config.getConsumerGroup());
        assertEquals("TEST", config.getEnvironmentName());
        assertEquals("null", config.getKeyStorePath());
        assertEquals("null", config.getKeyStorePassword());
        assertEquals(false, config.activateServerTLSAuth());
        assertEquals(true, config.isFilterInEmptyResources());
        assertEquals(false, config.isUseHttpsWithDmaap());
    }

    @Test
    public void testInvalidSdcConfiguration() throws IOException {
        SdcReceptionHandlerConfigurationParameterGroup configParameters = null;
        try {
            final Gson gson = new GsonBuilder().create();
            configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdcInvalid.json"),
                    SdcReceptionHandlerConfigurationParameterGroup.class);
        } catch (final Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
        final GroupValidationResult validationResult = configParameters.validate();
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testSdcConfigurationBuilder() {

        final SdcReceptionHandlerConfigurationParameterBuilder builder =
                new SdcReceptionHandlerConfigurationParameterBuilder().setAsdcAddress("localhost")
                        .setConsumerGroup("policy-group").setConsumerId("policy-id").setEnvironmentName("TEST")
                        .setKeystorePassword("password").setKeystorePath("dummyPath").setPassword("policy")
                        .setPollingInterval(10).setPollingTimeout(20).setRetryDelay(30).setUser("policy")
                        .setUseHttpsWithDmaap(false).setActiveserverTlsAuth(false).setFilterinEmptyResources(true)
                        .setArtifactTypes(Arrays.asList("TOSCA_CSAR")).setMessageBusAddress(Arrays.asList("localhost"));
        final SdcReceptionHandlerConfigurationParameterGroup configParameters =
                new SdcReceptionHandlerConfigurationParameterGroup(builder);
        configParameters.setName("SDCConfiguration");

        assertEquals(Arrays.asList("localhost"), configParameters.getMessageBusAddress());
        assertEquals(Arrays.asList("TOSCA_CSAR"), configParameters.getArtifactTypes());
        assertEquals("localhost", configParameters.getAsdcAddress());
        assertEquals("policy", configParameters.getUser());
        assertEquals("policy", configParameters.getPassword());
        assertEquals(10, configParameters.getPollingInterval());
        assertEquals(20, configParameters.getPollingTimeout());
        assertEquals(30, configParameters.getRetryDelay());
        assertEquals("policy-id", configParameters.getConsumerId());
        assertEquals("policy-group", configParameters.getConsumerGroup());
        assertEquals("TEST", configParameters.getEnvironmentName());
        assertEquals("dummyPath", configParameters.getKeyStorePath());
        assertEquals("password", configParameters.getKeyStorePassword());
        assertEquals(false, configParameters.isActiveServerTlsAuth());
        assertEquals(true, configParameters.isFilterInEmptyResources());
        assertEquals(false, configParameters.isUseHttpsWithDmaap());
    }

    @Test
    public void testSdcConfigurationWithNullList() {

        final SdcReceptionHandlerConfigurationParameterBuilder builder =
                new SdcReceptionHandlerConfigurationParameterBuilder().setAsdcAddress("localhost")
                        .setConsumerGroup("policy-group").setConsumerId("policy-id").setEnvironmentName("TEST")
                        .setKeystorePassword("password").setKeystorePath("dummyPath").setPassword("policy")
                        .setPollingInterval(10).setPollingTimeout(20).setUser("policy").setUseHttpsWithDmaap(false)
                        .setActiveserverTlsAuth(false).setFilterinEmptyResources(true)
                        .setArtifactTypes(Arrays.asList("TOSCA_CSAR")).setMessageBusAddress(null);
        final SdcReceptionHandlerConfigurationParameterGroup configParameters =
                new SdcReceptionHandlerConfigurationParameterGroup(builder);
        configParameters.setName("SDCConfiguration");

        try {
            configParameters.validate();
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("collection parameter \"messageBusAddress\" is null"));
        }
    }

    @Test
    public void testSdcConfigurationWithEmptyStringList() {

        final SdcReceptionHandlerConfigurationParameterBuilder builder =
                new SdcReceptionHandlerConfigurationParameterBuilder().setAsdcAddress("localhost")
                        .setConsumerGroup("policy-group").setConsumerId("policy-id").setEnvironmentName("TEST")
                        .setKeystorePassword("password").setKeystorePath("dummyPath").setPassword("policy")
                        .setPollingInterval(10).setPollingTimeout(20).setUser("policy").setUseHttpsWithDmaap(false)
                        .setActiveserverTlsAuth(false).setFilterinEmptyResources(true)
                        .setArtifactTypes(Arrays.asList("")).setMessageBusAddress(Arrays.asList("localhost"));
        final SdcReceptionHandlerConfigurationParameterGroup configParameters =
                new SdcReceptionHandlerConfigurationParameterGroup(builder);
        configParameters.setName("SDCConfiguration");

        final GroupValidationResult validationResult = configParameters.validate();
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains("must be a non-blank string"));
    }
}
