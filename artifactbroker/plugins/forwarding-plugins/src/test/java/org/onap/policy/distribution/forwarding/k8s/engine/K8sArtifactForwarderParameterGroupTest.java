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

package org.onap.policy.distribution.forwarding.k8s.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup.K8sArtifactForwarderParameterGroupBuilder;

public class K8sArtifactForwarderParameterGroupTest {

    @Test
    public void testBuilderAndGetters() {
        K8sArtifactForwarderParameterGroupBuilder builder =
                new K8sArtifactForwarderParameterGroupBuilder();
        K8sArtifactForwarderParameterGroup configurationParameters =
                builder.setUseHttps(true).setHostname("10.10.10.10").setPort(1234).setUserName("myUser")
                        .setPassword("myPassword").setClientAuth("myClientAuth").setIsManaged(false).build();

        assertTrue(configurationParameters.isUseHttps());
        assertEquals("10.10.10.10", configurationParameters.getHostname());
        assertEquals(1234, configurationParameters.getPort());
        assertEquals("myUser", configurationParameters.getUserName());
        assertEquals("myPassword", configurationParameters.getPassword());
        assertEquals("myClientAuth", configurationParameters.getClientAuth());
        assertFalse(configurationParameters.isManaged());
    }

    @Test
    public void testInvalidHostName() {
        K8sArtifactForwarderParameterGroupBuilder builder =
                new K8sArtifactForwarderParameterGroupBuilder();
        K8sArtifactForwarderParameterGroup configurationParameters = builder.setUseHttps(true).setHostname("")
                .setPort(1234).setUserName("myUser").setPassword("myPassword").setIsManaged(false).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPort() {
        K8sArtifactForwarderParameterGroupBuilder builder =
                new K8sArtifactForwarderParameterGroupBuilder();
        K8sArtifactForwarderParameterGroup configurationParameters =
                builder.setUseHttps(true).setHostname("10.10.10.10").setPort(-1234).setUserName("myUser")
                        .setPassword("myPassword").setIsManaged(false).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidUserName() {
        K8sArtifactForwarderParameterGroupBuilder builder =
                new K8sArtifactForwarderParameterGroupBuilder();
        K8sArtifactForwarderParameterGroup configurationParameters =
                builder.setUseHttps(true).setHostname("10.10.10.10").setPort(1234).setUserName("")
                        .setPassword("myPassword").setIsManaged(false).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPassword() {
        K8sArtifactForwarderParameterGroupBuilder builder =
                new K8sArtifactForwarderParameterGroupBuilder();
        K8sArtifactForwarderParameterGroup configurationParameters =
                builder.setUseHttps(true).setHostname("10.10.10.10").setPort(1234).setUserName("myUser").setPassword("")
                        .setIsManaged(false).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

}
