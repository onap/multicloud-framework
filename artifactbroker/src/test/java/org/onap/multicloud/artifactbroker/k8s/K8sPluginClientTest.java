/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2026 Deutsche Telekom AG
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
 * ============LICENSE_END=========================================================
 */

package org.onap.multicloud.artifactbroker.k8s;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.LogCapture;
import org.onap.multicloud.artifactbroker.config.TestProperties;
import org.onap.multicloud.artifactbroker.sdc.VfModuleModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class K8sPluginClientTest {

    private static final String BASE_URL = "http://k8s-plugin:9015";
    private static final String DEFINITION_URL = BASE_URL + "/v1/rb/definition";
    private static final String MODULE_URL = DEFINITION_URL + "/invariant-uuid/customization-uuid";
    private static final byte[] CHART = {31, -117, 8, 0};
    private static final VfModuleModel MODULE = new VfModuleModel("vfw..base_template..module-0",
            "invariant-uuid", "model-uuid", "customization-uuid", "a module", List.of());

    @RegisterExtension
    final LogCapture logs = LogCapture.of(K8sPluginClient.class);

    private MockRestServiceServer server;
    private K8sPluginClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new K8sPluginClient(builder,
                TestProperties.bind(Map.of("artifactbroker.k8s-plugin.base-url", BASE_URL)));
    }

    @Test
    void newDefinitionIsCreatedAndItsChartUploaded() throws Exception {
        server.expect(requestTo(MODULE_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(DEFINITION_URL)).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$['rb-name']").value("invariant-uuid"))
                .andExpect(jsonPath("$['rb-version']").value("customization-uuid"))
                .andExpect(jsonPath("$.description").value("a module"))
                .andExpect(jsonPath("$.labels.vf_module_model_uuid").value("model-uuid"))
                .andExpect(jsonPath("$.labels.vf_module_model_name").value("vfw..base_template..module-0"))
                .andRespond(withStatus(HttpStatus.CREATED));
        server.expect(requestTo(MODULE_URL + "/content")).andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(CHART))
                .andRespond(withSuccess());

        client.registerChart(MODULE, "chart.tgz", CHART);

        server.verify();
        for (String operation : List.of("look up", "create", "upload the content of")) {
            String call = operation + " definition invariant-uuid/customization-uuid";
            ILoggingEvent calling = logs.event("Calling the k8s plugin to " + call);
            assertThat(calling.getLevel()).isEqualTo(Level.INFO);
            assertThat(LogCapture.fields(calling)).containsEntry("rb_name", "invariant-uuid")
                    .containsEntry("rb_version", "customization-uuid")
                    .containsEntry("artifact_name", "chart.tgz");
            ILoggingEvent succeeded = logs.event("k8s plugin call to " + call + " succeeded");
            assertThat(succeeded.getLevel()).isEqualTo(Level.INFO);
            assertThat(LogCapture.fields(succeeded)).containsEntry("rb_name", "invariant-uuid")
                    .containsKey("duration_ms");
        }
    }

    @Test
    void existingDefinitionOnlyGetsItsChartUploadedAgain() throws Exception {
        server.expect(requestTo(MODULE_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MODULE_URL + "/content")).andExpect(method(HttpMethod.POST))
                .andExpect(content().bytes(CHART))
                .andRespond(withSuccess());

        client.registerChart(MODULE, "chart.tgz", CHART);

        server.verify();
    }

    @Test
    void rejectedDefinitionFailsTheRegistration() {
        server.expect(requestTo(MODULE_URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(DEFINITION_URL)).andRespond(withServerError());

        assertThatThrownBy(() -> client.registerChart(MODULE, "chart.tgz", CHART))
                .isInstanceOf(ArtifactProcessingException.class)
                .hasMessageContaining("creating definition invariant-uuid/customization-uuid failed");
    }

    @Test
    void rejectedUploadFailsTheRegistration() {
        server.expect(requestTo(MODULE_URL)).andRespond(withSuccess());
        server.expect(requestTo(MODULE_URL + "/content")).andRespond(withServerError());

        assertThatThrownBy(() -> client.registerChart(MODULE, "chart.tgz", CHART))
                .isInstanceOf(ArtifactProcessingException.class)
                .hasMessageContaining("uploading chart.tgz failed");
        ILoggingEvent failed = logs.event(
                "k8s plugin call to upload the content of definition invariant-uuid/customization-uuid failed");
        assertThat(failed.getLevel()).isEqualTo(Level.ERROR);
        assertThat(LogCapture.fields(failed)).containsEntry("status_code", 500)
                .containsEntry("rb_version", "customization-uuid")
                .containsKey("duration_ms");
    }

    @Test
    void unreachableK8sPluginFailsTheRegistration() {
        server.expect(requestTo(MODULE_URL)).andRespond(withException(new IOException("Connection refused")));

        assertThatThrownBy(() -> client.registerChart(MODULE, "chart.tgz", CHART))
                .isInstanceOf(ArtifactProcessingException.class)
                .hasMessageContaining("Connection refused");
        ILoggingEvent failed = logs.event(
                "k8s plugin call to look up definition invariant-uuid/customization-uuid failed");
        assertThat(failed.getFormattedMessage()).contains("Connection refused");
        assertThat(LogCapture.fields(failed)).doesNotContainKey("status_code");
    }
}
