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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "artifactbroker.sdc.retry-delay=1h")
class ArtifactBrokerApplicationTest {

    @LocalServerPort
    private int port;

    @Test
    void podStaysReadyWhileSdcIsUnreachable() {
        assertThat(get("/actuator/health/readiness").getStatusCode().value()).isEqualTo(200);
        assertThat(get("/actuator/health/liveness").getStatusCode().value()).isEqualTo(200);

        ResponseEntity<String> health = get("/actuator/health");
        assertThat(health.getStatusCode().value()).isEqualTo(503);
        assertThat(health.getBody()).contains("sdcConnection");
    }

    @Test
    void metricsAreExposedForPrometheus() {
        assertThat(get("/actuator/prometheus").getStatusCode().value()).isEqualTo(200);
    }

    private ResponseEntity<String> get(String path) {
        return RestClient.create("http://localhost:" + port).get().uri(path)
                .exchange((request, response) ->
                        ResponseEntity.status(response.getStatusCode()).body(response.bodyTo(String.class)));
    }

    @TestConfiguration
    static class UnreachableSdc {

        @Bean
        @Primary
        IDistributionClient unreachableSdcClient() {
            IDistributionClientResult failure = mock(IDistributionClientResult.class);
            when(failure.getDistributionActionResult()).thenReturn(DistributionActionResultEnum.GENERAL_ERROR);
            when(failure.getDistributionMessageResult()).thenReturn("SDC unreachable");
            IDistributionClient client = mock(IDistributionClient.class);
            when(client.init(any(), any())).thenReturn(failure);
            return client;
        }
    }
}
