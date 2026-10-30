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

package org.onap.multicloud.artifactbroker.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.sdc.api.IDistributionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "TRACING_ENABLED=true",
    "management.tracing.export.otlp.enabled=false",
    "artifactbroker.sdc.enabled=false"})
@Import(TracingConfigTest.RecordingHandlerConfig.class)
class TracingConfigTest {

    @LocalServerPort
    private int port;
    @Autowired
    private RecordingHandler recorded;
    @MockitoBean
    private IDistributionClient client;

    @BeforeEach
    void setUp() {
        recorded.paths.clear();
    }

    @Test
    void probesAndScrapesAreNotObserved() {
        get("/actuator/health/liveness");
        get("/actuator/health/readiness");
        get("/actuator/health");
        get("/actuator/prometheus");
        get("/not-an-actuator-path");

        assertThat(recorded.paths).containsExactly("/not-an-actuator-path");
    }

    private void get(String path) {
        RestClient.create("http://localhost:" + port).get().uri(path).exchange((request, response) -> null);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class RecordingHandlerConfig {

        @Bean
        RecordingHandler recordingHandler() {
            return new RecordingHandler();
        }
    }

    static class RecordingHandler implements ObservationHandler<ServerRequestObservationContext> {

        private final List<String> paths = new CopyOnWriteArrayList<>();

        @Override
        public void onStop(ServerRequestObservationContext context) {
            paths.add(context.getCarrier().getRequestURI());
        }

        @Override
        public boolean supportsContext(Observation.Context context) {
            return context instanceof ServerRequestObservationContext;
        }
    }
}
