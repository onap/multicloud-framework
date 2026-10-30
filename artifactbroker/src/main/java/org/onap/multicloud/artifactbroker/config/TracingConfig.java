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

import io.micrometer.observation.ObservationPredicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;

@Configuration(proxyBeanMethods = false)
public class TracingConfig {

    private static final String ACTUATOR_PATHS = "/actuator/**";

    // Also drops the actuator's http_server_requests series: an observation feeds metrics and traces alike.
    @Bean
    ObservationPredicate actuatorRequestsAreNotObserved() {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return (name, context) -> {
            if (!(context instanceof ServerRequestObservationContext serverContext)) {
                return true;
            }
            HttpServletRequest request = serverContext.getCarrier();
            String path = request.getRequestURI().substring(request.getContextPath().length());
            return !pathMatcher.match(ACTUATOR_PATHS, path);
        };
    }
}
