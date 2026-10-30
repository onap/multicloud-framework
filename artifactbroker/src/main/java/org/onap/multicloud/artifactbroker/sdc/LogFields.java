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

import java.util.concurrent.TimeUnit;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.slf4j.spi.LoggingEventBuilder;

final class LogFields {

    static final String DISTRIBUTION_ID = "distribution_id";

    private LogFields() {
    }

    static LoggingEventBuilder artifact(LoggingEventBuilder builder, IArtifactInfo artifact) {
        return builder.addKeyValue("artifact_name", artifact.getArtifactName())
                .addKeyValue("artifact_uuid", artifact.getArtifactUUID())
                .addKeyValue("artifact_type", artifact.getArtifactType());
    }

    static long millisSince(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
