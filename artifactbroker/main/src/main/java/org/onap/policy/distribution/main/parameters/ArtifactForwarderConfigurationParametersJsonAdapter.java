/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019-2020 Intel. All rights reserved.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.distribution.main.parameters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * This class deserialises policy forwarder parameters from JSON.
 */
public class ArtifactForwarderConfigurationParametersJsonAdapter
        implements JsonDeserializer<ArtifactForwarderConfigurationParameterGroup> {
    private static final Logger LOGGER = FlexLogger.getLogger(ArtifactForwarderConfigurationParametersJsonAdapter.class);

    private static final String PARAMETER_CLASS_NAME = "parameterClassName";
    private static final String Artifact_FORWARDER_PARAMETERS = "parameters";

    @Override
    public ArtifactForwarderConfigurationParameterGroup deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        final JsonObject jsonObject = json.getAsJsonObject();

        final String artifactForwarderParameterClassName = getParameterGroupClassName(jsonObject);
        Class<?> artifactForwarderParameterClass = getParameterGroupClass(artifactForwarderParameterClassName);

        return context.deserialize(jsonObject.get(Artifact_FORWARDER_PARAMETERS), artifactForwarderParameterClass);
    }

    private String getParameterGroupClassName(final JsonObject jsonObject) {
        final JsonPrimitive classNameJsonPrimitive = ((JsonPrimitive) jsonObject.get(PARAMETER_CLASS_NAME));

        if (classNameJsonPrimitive == null || classNameJsonPrimitive.getAsString().length() == 0) {
            final String errorMessage = "parameter \"" + PARAMETER_CLASS_NAME + "\" value \""
                    + (classNameJsonPrimitive != null ? classNameJsonPrimitive.getAsString() : "null")
                    + "\" invalid in JSON file";
            LOGGER.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return classNameJsonPrimitive.getAsString().replaceAll("\\s+", "");
    }

    private Class<?> getParameterGroupClass(final String artifactForwarderParameterClassName) {
        Class<?> artifactForwarderParameterClass = null;
        try {
            artifactForwarderParameterClass = Class.forName(artifactForwarderParameterClassName);
        } catch (final ClassNotFoundException e) {
            final String errorMessage = "parameter \"" + PARAMETER_CLASS_NAME + "\" value \""
                    + artifactForwarderParameterClassName + "\", could not find class";
            LOGGER.warn(errorMessage, e);
            throw new IllegalArgumentException(errorMessage, e);
        }
        return artifactForwarderParameterClass;
    }

}
