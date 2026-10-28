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
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup.K8sArtifactForwarderParameterGroupBuilder;

public class K8sArtifactForwarderParameterGroupTest {

    @Test
    public void testBuilderAndGetters() {
        final K8sArtifactForwarderParameterGroup configurationParameters = new K8sArtifactForwarderParameterGroupBuilder()
                .setUseHttps(true).setHostname("10.10.10.10").setPort(1234).build();
        configurationParameters.setName("myConfiguration");

        assertTrue(configurationParameters.isUseHttps());
        assertEquals("10.10.10.10", configurationParameters.getHostname());
        assertEquals(1234, configurationParameters.getPort());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidHostName() {
        final K8sArtifactForwarderParameterGroup configurationParameters = new K8sArtifactForwarderParameterGroupBuilder()
                .setHostname("").setPort(1234).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPort() {
        final K8sArtifactForwarderParameterGroup configurationParameters = new K8sArtifactForwarderParameterGroupBuilder()
                .setHostname("10.10.10.10").setPort(-1234).build();
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testConfigurationWithPdpFieldsIsStillAccepted() {
        final K8sArtifactForwarderParameterGroup configurationParameters = new Gson().fromJson("{\"useHttps\": false,"
                + " \"hostname\": \"localhost\", \"port\": 9015, \"userName\": \"testpdp\", \"password\": \"alpha123\","
                + " \"clientAuth\": \"cHl0aG9uOnRlc3Q=\", \"isManaged\": true, \"pdpGroup\": \"default\"}",
                K8sArtifactForwarderParameterGroup.class);
        configurationParameters.setName("myConfiguration");

        assertEquals("localhost", configurationParameters.getHostname());
        assertEquals(9015, configurationParameters.getPort());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }
}
