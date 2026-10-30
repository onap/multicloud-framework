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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;
import org.slf4j.event.KeyValuePair;

public final class LogCapture extends AppenderBase<ILoggingEvent> implements BeforeEachCallback, AfterEachCallback {

    private final Logger logger;
    private final List<ILoggingEvent> events = new CopyOnWriteArrayList<>();

    private LogCapture(Class<?> type) {
        this.logger = (Logger) LoggerFactory.getLogger(type);
    }

    public static LogCapture of(Class<?> type) {
        return new LogCapture(type);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        events.clear();
        start();
        logger.addAppender(this);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        logger.detachAppender(this);
        stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        event.prepareForDeferredProcessing();
        events.add(event);
    }

    public ILoggingEvent event(String message) {
        List<ILoggingEvent> matches = events.stream()
                .filter(event -> event.getFormattedMessage().startsWith(message))
                .toList();
        assertThat(matches).as("log events starting with '%s' in %s", message, messages()).hasSize(1);
        return matches.get(0);
    }

    public List<String> messages() {
        return events.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    public static Map<String, Object> fields(ILoggingEvent event) {
        Map<String, Object> fields = new LinkedHashMap<>(event.getMDCPropertyMap());
        List<KeyValuePair> pairs = event.getKeyValuePairs();
        if (pairs != null) {
            pairs.forEach(pair -> fields.put(pair.key, pair.value));
        }
        return fields;
    }
}
