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

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.onap.multicloud.artifactbroker.config.ArtifactBrokerProperties;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.results.DistributionActionResultEnum;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "artifactbroker.sdc.enabled", havingValue = "true", matchIfMissing = true)
public class SdcConnection implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(SdcConnection.class);

    private final IDistributionClient client;
    private final IConfiguration configuration;
    private final INotificationCallback callback;
    private final Duration retryDelay;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "sdc-connection");
        thread.setDaemon(true);
        return thread;
    });

    private boolean initialized;
    private volatile boolean connected;
    private volatile String lastError = "not connected yet";

    public SdcConnection(IDistributionClient client, IConfiguration configuration, INotificationCallback callback,
            ArtifactBrokerProperties properties) {
        this.client = client;
        this.configuration = configuration;
        this.callback = callback;
        this.retryDelay = properties.sdc().retryDelay();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connect() {
        executor.scheduleWithFixedDelay(this::tryConnect, 0, retryDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    synchronized void tryConnect() {
        try {
            if (!initialized) {
                if (!succeeded("initialize", client.init(configuration, callback))) {
                    return;
                }
                initialized = true;
            }
            if (succeeded("start", client.start())) {
                connected = true;
                lastError = null;
                LOG.info("Connected to SDC at {}", configuration.getSdcAddress());
                executor.shutdown();
            }
        } catch (RuntimeException | LinkageError e) {
            lastError = "SDC distribution client failed: " + e.getMessage();
            LOG.error("{}, retrying in {}", lastError, retryDelay, e);
        }
    }

    private boolean succeeded(String action, IDistributionClientResult result) {
        if (result.getDistributionActionResult() == DistributionActionResultEnum.SUCCESS) {
            return true;
        }
        lastError = "Failed to " + action + " the SDC distribution client: " + result.getDistributionMessageResult();
        LOG.error("{}, retrying in {}", lastError, retryDelay);
        return false;
    }

    @PreDestroy
    public synchronized void disconnect() {
        executor.shutdownNow();
        if (initialized) {
            client.stop();
        }
    }

    @Override
    public Health health() {
        return connected ? Health.up().build() : Health.down().withDetail("reason", lastError).build();
    }
}
