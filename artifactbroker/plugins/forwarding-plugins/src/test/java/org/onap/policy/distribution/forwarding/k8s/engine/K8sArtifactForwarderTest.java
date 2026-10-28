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
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwardingException;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarder;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup.K8sArtifactForwarderParameterGroupBuilder;
import org.onap.policy.distribution.model.CloudArtifact;
import org.onap.policy.distribution.model.VfModuleModel;
import org.onap.sdc.api.notification.IArtifactInfo;

public class K8sArtifactForwarderTest {

    private static final String CONFIGURATION_NAME = "k8sConfiguration";
    private static final String DEFINITION_PATH = "/v1/rb/definition";
    private static final String MODULE_PATH = DEFINITION_PATH + "/invariant-1/cust-1";
    private static final String ONE_VF_MODULE = "[{\"vfModuleModelName\": \"vf..base..module-0\","
            + " \"vfModuleModelInvariantUUID\": \"invariant-1\", \"vfModuleModelCustomizationUUID\": \"cust-1\","
            + " \"artifacts\": [\"chart-uuid\"]}]";

    @Rule
    public TemporaryFolder dataDirectory = new TemporaryFolder();

    private final List<String> requests = new ArrayList<>();
    private final Map<String, Integer> statuses = new HashMap<>();
    private final Map<String, String> bodies = new HashMap<>();
    private HttpServer k8sPlugin;
    private K8sArtifactForwarderParameterGroup parameters;
    private K8sArtifactForwarder forwarder;

    /**
     * Starts a fake k8s plugin and configures the forwarder to use it.
     */
    @Before
    public void setUp() throws Exception {
        k8sPlugin = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        k8sPlugin.createContext("/", exchange -> {
            final String request = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();
            requests.add(request);
            try (InputStream body = exchange.getRequestBody()) {
                bodies.put(request, new String(body.readAllBytes(), StandardCharsets.UTF_8));
            }
            exchange.sendResponseHeaders(statuses.getOrDefault(request, 200), -1);
            exchange.close();
        });
        k8sPlugin.start();

        parameters = new K8sArtifactForwarderParameterGroupBuilder().setUseHttps(false).setHostname("localhost")
                .setPort(k8sPlugin.getAddress().getPort()).build();
        parameters.setName(CONFIGURATION_NAME);
        ParameterService.register(parameters);

        forwarder = new K8sArtifactForwarder();
        forwarder.configure(CONFIGURATION_NAME);
        final Field field = K8sArtifactForwarder.class.getDeclaredField("dataDirectory");
        field.setAccessible(true);
        field.set(forwarder, dataDirectory.getRoot().toPath());

        final Path moduleDirectory = dataDirectory.newFolder("cust-1").toPath();
        Files.write(moduleDirectory.resolve("chart.tgz"), "chart".getBytes(StandardCharsets.UTF_8));
    }

    @After
    public void tearDown() {
        k8sPlugin.stop(0);
        ParameterService.deregister(parameters.getName());
    }

    @Test
    public void newDefinitionIsCreatedAndItsChartUploaded() throws Exception {
        statuses.put("GET " + MODULE_PATH, 500);

        forwarder.forward(cloudArtifact(artifact("chart.tgz", "HELM")));

        assertEquals(Arrays.asList("GET " + MODULE_PATH, "POST " + DEFINITION_PATH, "POST " + MODULE_PATH + "/content"),
                requests);
        assertTrue(bodies.get("POST " + DEFINITION_PATH).contains("\"rb-name\":\"invariant-1\""));
        assertEquals("chart", bodies.get("POST " + MODULE_PATH + "/content"));
    }

    @Test
    public void existingDefinitionOnlyGetsItsChartUploaded() throws Exception {
        forwarder.forward(cloudArtifact(artifact("chart.tgz", "HELM")));

        assertEquals(Arrays.asList("GET " + MODULE_PATH, "POST " + MODULE_PATH + "/content"), requests);
    }

    @Test
    public void rejectedDefinitionFailsTheForwarding() {
        statuses.put("GET " + MODULE_PATH, 500);
        statuses.put("POST " + DEFINITION_PATH, 500);

        assertForwardingFails(cloudArtifact(artifact("chart.tgz", "HELM")));
        assertEquals(Arrays.asList("GET " + MODULE_PATH, "POST " + DEFINITION_PATH), requests);
    }

    @Test
    public void rejectedUploadFailsTheForwarding() {
        statuses.put("POST " + MODULE_PATH + "/content", 422);

        assertForwardingFails(cloudArtifact(artifact("chart.tgz", "HELM")));
    }

    @Test
    public void unreachableK8sPluginFailsTheForwarding() {
        k8sPlugin.stop(0);

        assertForwardingFails(cloudArtifact(artifact("chart.tgz", "HELM")));
    }

    @Test
    public void moduleWithoutChartFailsTheForwarding() {
        assertForwardingFails(cloudArtifact(artifact("base.yaml", "HEAT")));
        assertTrue(requests.isEmpty());
    }

    private void assertForwardingFails(final CloudArtifact cloudArtifact) {
        try {
            forwarder.forward(cloudArtifact);
            fail("forwarding must fail");
        } catch (final ArtifactForwardingException exp) {
            assertTrue(exp.getMessage().contains("vf..base..module-0"));
        }
    }

    private static CloudArtifact cloudArtifact(final IArtifactInfo chart) {
        final ArrayList<VfModuleModel> vfModules =
                new Gson().fromJson(ONE_VF_MODULE, new TypeToken<ArrayList<VfModuleModel>>() {}.getType());
        final HashMap<String, IArtifactInfo> artifacts = new HashMap<>();
        artifacts.put("chart-uuid", chart);
        return new CloudArtifact(vfModules, artifacts);
    }

    private static IArtifactInfo artifact(final String name, final String type) {
        return (IArtifactInfo) Proxy.newProxyInstance(IArtifactInfo.class.getClassLoader(),
                new Class<?>[] {IArtifactInfo.class}, (instance, method, args) -> {
                switch (method.getName()) {
                    case "getArtifactName":
                        return name;
                    case "getArtifactType":
                        return type;
                    case "toString":
                        return name;
                    default:
                        return null;
                }
            });
    }
}
