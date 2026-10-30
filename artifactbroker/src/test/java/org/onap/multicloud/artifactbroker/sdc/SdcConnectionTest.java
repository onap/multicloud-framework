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

package org.onap.multicloud.artifactbroker.sdc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.onap.multicloud.artifactbroker.LogCapture;
import org.onap.multicloud.artifactbroker.config.TestProperties;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.springframework.boot.health.contributor.Status;

class SdcConnectionTest {

    @RegisterExtension
    final LogCapture logs = LogCapture.of(SdcConnection.class);

    private final IDistributionClient client = mock(IDistributionClient.class);
    private SdcConnection connection;

    @BeforeEach
    void setUp() {
        IConfiguration configuration = mock(IConfiguration.class);
        when(configuration.getSdcAddress()).thenReturn("sdc-be.onap:8443");
        connection = new SdcConnection(client, configuration, mock(INotificationCallback.class),
                TestProperties.bind(Map.of()));
    }

    @Test
    void isDownBeforeTheFirstAttempt() {
        assertThat(connection.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void connectsWhenInitAndStartSucceed() {
        when(client.init(any(), any())).thenReturn(result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.SUCCESS));

        connection.tryConnect();

        assertThat(connection.health().getStatus()).isEqualTo(Status.UP);
        assertThat(LogCapture.fields(logs.event("Calling SDC distribution client initialize")))
                .containsEntry("sdc_address", "sdc-be.onap:8443");
        assertThat(LogCapture.fields(logs.event("SDC distribution client initialize succeeded")))
                .containsKey("duration_ms");
        assertThat(logs.event("Calling SDC distribution client start").getLevel()).isEqualTo(Level.INFO);
        assertThat(LogCapture.fields(logs.event("SDC distribution client start succeeded")))
                .containsKey("duration_ms");
        assertThat(LogCapture.fields(logs.event("Connected to SDC"))).containsEntry("sdc_address", "sdc-be.onap:8443");
    }

    @Test
    void failedInitIsLoggedWithTheRetryDelay() {
        when(client.init(any(), any())).thenReturn(result(DistributionActionResultEnum.GENERAL_ERROR));

        connection.tryConnect();

        ILoggingEvent failure = logs.event("Failed to initialize the SDC distribution client: SDC says no");
        assertThat(failure.getLevel()).isEqualTo(Level.ERROR);
        assertThat(LogCapture.fields(failure)).containsEntry("retry_delay", "PT30S").containsKey("duration_ms");
        assertThat(logs.messages()).noneMatch(message -> message.contains("succeeded"));
    }

    @Test
    void failedInitIsRetriedAndReportedAsDown() {
        when(client.init(any(), any())).thenReturn(result(DistributionActionResultEnum.GENERAL_ERROR),
                result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.SUCCESS));

        connection.tryConnect();

        assertThat(connection.health().getStatus()).isEqualTo(Status.DOWN);
        assertThat(connection.health().getDetails().get("reason").toString()).contains("initialize", "SDC says no");

        connection.tryConnect();

        assertThat(connection.health().getStatus()).isEqualTo(Status.UP);
        verify(client, times(2)).init(any(), any());
    }

    @Test
    void failedStartDoesNotInitializeAgain() {
        when(client.init(any(), any())).thenReturn(result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.GENERAL_ERROR),
                result(DistributionActionResultEnum.SUCCESS));

        connection.tryConnect();
        connection.tryConnect();

        verify(client, times(1)).init(any(), any());
        verify(client, times(2)).start();
        assertThat(connection.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void exceptionFromTheClientDoesNotStopTheRetries() {
        when(client.init(any(), any())).thenThrow(new IllegalStateException("no SASL_JAAS_CONFIG"))
                .thenReturn(result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.SUCCESS));

        connection.tryConnect();

        assertThat(connection.health().getDetails().get("reason").toString()).contains("no SASL_JAAS_CONFIG");
        ILoggingEvent failure = logs.event("SDC distribution client failed: no SASL_JAAS_CONFIG, retrying in PT30S");
        assertThat(failure.getLevel()).isEqualTo(Level.ERROR);
        assertThat(failure.getThrowableProxy().getMessage()).isEqualTo("no SASL_JAAS_CONFIG");

        connection.tryConnect();

        assertThat(connection.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void linkageErrorFromTheClientDoesNotStopTheRetries() {
        when(client.init(any(), any())).thenThrow(new NoClassDefFoundError("org/apache/kafka/common/Foo"))
                .thenReturn(result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.SUCCESS));

        connection.tryConnect();

        assertThat(connection.health().getDetails().get("reason").toString()).contains("org/apache/kafka/common/Foo");

        connection.tryConnect();

        assertThat(connection.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void disconnectStopsAnInitializedClient() {
        when(client.init(any(), any())).thenReturn(result(DistributionActionResultEnum.SUCCESS));
        when(client.start()).thenReturn(result(DistributionActionResultEnum.GENERAL_ERROR));
        connection.tryConnect();

        connection.disconnect();

        verify(client).stop();
    }

    @Test
    void disconnectWithoutInitDoesNotStopTheClient() {
        connection.disconnect();

        verify(client, never()).stop();
    }

    private static IDistributionClientResult result(DistributionActionResultEnum value) {
        return new IDistributionClientResult() {
            @Override
            public DistributionActionResultEnum getDistributionActionResult() {
                return value;
            }

            @Override
            public String getDistributionMessageResult() {
                return "SDC says no";
            }
        };
    }
}
