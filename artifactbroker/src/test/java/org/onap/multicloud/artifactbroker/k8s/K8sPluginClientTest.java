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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.multicloud.artifactbroker.LogCapture;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.multicloud.artifactbroker.config.TestProperties;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient.CredentialConfiguration;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient.Credentials;
import org.onap.multicloud.artifactbroker.sdc.VfModuleModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.http.HttpHeaders;
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
                TestProperties.bind(Map.of("artifactbroker.k8s-plugin.base-url", BASE_URL)),
                new Credentials(null, null));
    }

    @Test
    void newDefinitionIsCreatedAndItsChartUploaded() throws Exception {
        server.expect(requestTo(MODULE_URL)).andExpect(method(HttpMethod.GET))
                                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(DEFINITION_URL)).andExpect(method(HttpMethod.POST))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andExpect(jsonPath("$['rb-name']").value("invariant-uuid"))
                .andExpect(jsonPath("$['rb-version']").value("customization-uuid"))
                .andExpect(jsonPath("$.description").value("a module"))
                .andExpect(jsonPath("$.labels.vf_module_model_uuid").value("model-uuid"))
                .andExpect(jsonPath("$.labels.vf_module_model_name").value("vfw..base_template..module-0"))
                .andRespond(withStatus(HttpStatus.CREATED));
        server.expect(requestTo(MODULE_URL + "/content")).andExpect(method(HttpMethod.POST))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
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
    void configuredCredentialsAuthenticateLookupJsonAndUploadWithoutChangingTheSharedBuilder() throws Exception {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new K8sPluginClient(builder,
                TestProperties.bind(Map.of("artifactbroker.k8s-plugin.base-url", BASE_URL)),
                new Credentials("operator", "p\u00e4ss:word"));
        String basic = "Basic " + Base64.getEncoder().encodeToString(
                "operator:p\u00e4ss:word".getBytes(StandardCharsets.UTF_8));
        server.expect(requestTo(MODULE_URL)).andExpect(header(HttpHeaders.AUTHORIZATION, basic))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        server.expect(requestTo(DEFINITION_URL)).andExpect(header(HttpHeaders.AUTHORIZATION, basic))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withStatus(HttpStatus.CREATED));
        server.expect(requestTo(MODULE_URL + "/content")).andExpect(header(HttpHeaders.AUTHORIZATION, basic))
                .andExpect(content().bytes(CHART)).andRespond(withSuccess());
        server.expect(requestTo("http://unrelated/service"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION)).andRespond(withSuccess());

        client.registerChart(MODULE, "chart.tgz", CHART);
        builder.build().get().uri("http://unrelated/service").retrieve().toBodilessEntity();

        server.verify();
        assertThat(logs.messages()).allSatisfy(message -> assertThat(message)
                .doesNotContain("p\u00e4ss:word", basic));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "false|literal${spring.application.name}#{2*3}",
        "false|${unresolved}",
        "true|literal${spring.application.name}#{2*3}",
        "true|${unresolved}"
    })
    void realEnvironmentCredentialsRemainLiteral(boolean environmentVariables, String password) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer credentialServer = MockRestServiceServer.bindTo(builder).build();
        String username = "operator${spring.application.name}#{2*3}";
        String expected = "Basic " + Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(CredentialBindingConfiguration.class)
                .withBean(RestClient.Builder.class, () -> builder)
                .withPropertyValues("spring.application.name=expanded",
                        "artifactbroker.k8s-plugin.base-url=" + BASE_URL);
        if (environmentVariables) {
            runner = runner.withInitializer(context -> context.getEnvironment().getPropertySources().addFirst(
                    new SystemEnvironmentPropertySource("plugin-systemEnvironment", Map.of(
                            "ARTIFACTBROKER_K8SPLUGIN_USERNAME", username,
                            "ARTIFACTBROKER_K8SPLUGIN_PASSWORD", password))));
        } else {
            runner = runner.withPropertyValues("artifactbroker.k8s-plugin.username=" + username,
                    "artifactbroker.k8s-plugin.password=" + password);
        }
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            credentialServer.expect(requestTo(MODULE_URL)).andExpect(request ->
                    assertThat(expected.equals(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)))
                            .as("outbound credentials remain literal").isTrue())
                    .andRespond(withSuccess());
            credentialServer.expect(requestTo(MODULE_URL + "/content")).andExpect(request ->
                    assertThat(expected.equals(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)))
                            .as("outbound credentials remain literal").isTrue())
                    .andRespond(withSuccess());
            context.getBean(K8sPluginClient.class).registerChart(MODULE, "chart.tgz", CHART);
            credentialServer.verify();
        });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ArtifactBrokerProperties.class)
        @Import({K8sPluginClient.class, CredentialConfiguration.class})
    static class CredentialBindingConfiguration {
    }

    @Test
    void incompleteOrInvalidCredentialsFailBindingWithoutEchoingSecrets() {
        for (Map<String, String> credentials : List.of(
                Map.of("artifactbroker.k8s-plugin.username", "secret-username"),
                Map.of("artifactbroker.k8s-plugin.password", "secret-password"),
                Map.of("artifactbroker.k8s-plugin.username", " ",
                        "artifactbroker.k8s-plugin.password", "secret-password"),
                Map.of("artifactbroker.k8s-plugin.username", "secret-username",
                        "artifactbroker.k8s-plugin.password", " "),
                Map.of("artifactbroker.k8s-plugin.username", "secret:username",
                        "artifactbroker.k8s-plugin.password", "secret-password"))) {
            assertThatThrownBy(() -> new Binder(new MapConfigurationPropertySource(credentials))
                    .bindOrCreate("artifactbroker.k8s-plugin", Credentials.class))
                    .hasRootCauseInstanceOf(IllegalArgumentException.class)
                    .rootCause().hasMessageNotContaining("secret-password")
                    .hasMessageNotContaining("secret-username").hasMessageNotContaining("secret:username");
        }
    }

    @Test
    void pluginPropertiesDoNotExposeCredentialsInTheirStringRepresentation() {
        Credentials credentials = new Credentials("operator", "secret-password");
        assertThat(credentials.toString()).doesNotContain("operator", "secret-password");
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
