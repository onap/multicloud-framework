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

package org.onap.multicloud.artifactbroker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.onap.multicloud.artifactbroker.config.TestProperties;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient;
import org.onap.multicloud.artifactbroker.k8s.K8sPluginClient.Credentials;
import org.onap.multicloud.artifactbroker.sdc.NotificationHandler;
import org.onap.multicloud.artifactbroker.sdc.VfModuleModel;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.boot.http.client.HttpRedirects;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "artifactbroker.sdc.retry-delay=1h")
@ExtendWith(OutputCaptureExtension.class)
class ArtifactBrokerApplicationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ApplicationContext context;

    @Autowired
    private NotificationHandler notificationHandler;

    @Test
    void podStaysReadyWhileSdcIsUnreachable() {
        assertThat(get("/actuator/health/readiness").getStatusCode().value()).isEqualTo(200);
        assertThat(get("/actuator/health/liveness").getStatusCode().value()).isEqualTo(200);

        ResponseEntity<String> health = get("/actuator/health");
        assertThat(health.getStatusCode().value()).isEqualTo(503);
        assertThat(health.getBody()).contains("sdcConnection");
    }

    @Test
    void consoleLogIsJsonWithTheDistributionAndStatusAsFields(CapturedOutput output) {
        INotificationData notification = mock(INotificationData.class);
        when(notification.getDistributionID()).thenReturn("distribution-json");

        notificationHandler.activateCallback(notification);

        JsonNode sent = JsonMapper.builder().build().readTree(output.getOut().lines()
                .filter(line -> line.contains("Sent COMPONENT_DONE_OK status to SDC"))
                .reduce((first, second) -> second)
                .orElseThrow());
        assertThat(sent.get("message").asString()).isEqualTo("Sent COMPONENT_DONE_OK status to SDC");
        assertThat(sent.get("level").asString()).isEqualTo("INFO");
        assertThat(sent.get("logger_name").asString()).endsWith(".sdc.StatusReporter");
        assertThat(sent.get("distribution_id").asString()).isEqualTo("distribution-json");
        assertThat(sent.get("status").asString()).isEqualTo("COMPONENT_DONE_OK");
        assertThat(sent.get("duration_ms").isNumber()).isTrue();
        assertThat(sent.has("@timestamp")).isTrue();
    }

    @Test
    void metricsAreExposedForPrometheus() {
        assertThat(get("/actuator/prometheus").getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @CsvSource({"GET,302,true", "GET,302,false", "CREATE,307,true", "CREATE,307,false",
        "UPLOAD,307,true", "UPLOAD,307,false"})
    void pluginCredentialsAreNotForwardedToRedirectDestinations(String operation, int status,
            boolean authenticated, CapturedOutput output) throws Exception {
        HttpServer source = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        HttpServer destination = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        List<String> leakedHeaders = new CopyOnWriteArrayList<>();
        List<String> pluginHeaders = new CopyOnWriteArrayList<>();
        destination.createContext("/redirected", exchange -> {
            leakedHeaders.add(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        source.createContext("/v1/rb/definition", exchange -> {
            pluginHeaders.add(exchange.getRequestHeaders().getFirst("Authorization"));
            if (exchange.getRequestMethod().equals("GET") && !operation.equals("GET")) {
                exchange.sendResponseHeaders(operation.equals("CREATE") ? 404 : 200, -1);
            } else {
                exchange.getResponseHeaders().add("Location",
                        "http://127.0.0.1:" + destination.getAddress().getPort() + "/redirected");
                exchange.sendResponseHeaders(status, -1);
            }
            exchange.close();
        });
        source.start();
        destination.start();
        try {
            K8sPluginClient plugin = new K8sPluginClient(context.getBean("k8sPluginRestClient", RestClient.class),
                    TestProperties.bind(Map.of(
                            "artifactbroker.k8s-plugin.base-url", "http://127.0.0.1:" + source.getAddress().getPort())),
                    authenticated ? new Credentials("operator", "secret") : new Credentials(null, null));
            Throwable failure = catchThrowable(() -> plugin.registerChart(
                    new VfModuleModel("module", "invariant", "model", "customization", "test", List.of()),
                    "chart.tgz", new byte[] {31, -117, 8, 0}));

            assertThat(leakedHeaders).isEmpty();
            assertThat(failure).isInstanceOf(ArtifactProcessingException.class)
                    .hasMessageContaining(String.valueOf(status));
            assertThat(pluginHeaders).hasSize(operation.equals("GET") ? 1 : 2)
                    .allSatisfy(header -> assertThat(header)
                            .isEqualTo(authenticated ? "Basic b3BlcmF0b3I6c2VjcmV0" : null));
            assertThat(output.getOut()).doesNotContain("Registered chart.tgz");
        } finally {
            source.stop(0);
            destination.stop(0);
        }
    }

    @Test
    void sharedClientStillFollowsRedirectsWithoutPluginCredentials() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        List<String> headers = new CopyOnWriteArrayList<>();
        server.createContext("/source", exchange -> {
            exchange.getResponseHeaders().add("Location", "/destination");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
        });
        server.createContext("/destination", exchange -> {
            headers.add(exchange.getRequestHeaders().getFirst("Authorization"));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        try {
            context.getBean(RestClient.Builder.class).build().get()
                    .uri("http://127.0.0.1:" + server.getAddress().getPort() + "/source")
                    .retrieve().toBodilessEntity();
            assertThat(headers).containsExactly((String) null);
        } finally {
            server.stop(0);
        }
    }

    @Test
    void nothingIsExportedOverOtlpByDefault() {
        assertThat(context.getBeansOfType(SpanExporter.class)).isEmpty();
        assertThat(context.getBeansOfType(OtlpMeterRegistry.class)).isEmpty();
    }

    private ResponseEntity<String> get(String path) {
        return RestClient.create("http://localhost:" + port).get().uri(path)
                .exchange((request, response) ->
                        ResponseEntity.status(response.getStatusCode()).body(response.bodyTo(String.class)));
    }

    @TestConfiguration
    static class UnreachableSdc {

        @Bean
        ClientHttpRequestFactoryBuilder<?> redirectFollowingFactoryBuilder() {
            return ClientHttpRequestFactoryBuilder.jdk();
        }

        @Bean
        HttpClientSettings redirectFollowingSettings() {
            return HttpClientSettings.defaults().withRedirects(HttpRedirects.FOLLOW);
        }

        @Bean
        @Primary
        IDistributionClient unreachableSdcClient() {
            IDistributionClientResult failure = mock(IDistributionClientResult.class);
            when(failure.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.GENERAL_ERROR);
            when(failure.getDistributionMessageResult()).thenReturn("SDC unreachable");
            IDistributionClient client = mock(IDistributionClient.class);
            when(client.init(any(), any())).thenReturn(failure);
            IDistributionClientResult success = mock(IDistributionClientResult.class);
            when(success.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.SUCCESS);
            when(client.sendComponentDoneStatus(any())).thenReturn(success);
            return client;
        }
    }
}
