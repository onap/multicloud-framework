/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.reception.parameters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * This class deserialises reception handler parameters from JSON.
 */
public class ReceptionHandlerConfigurationParametersJsonAdapter
        implements JsonDeserializer<ReceptionHandlerConfigurationParameterGroup> {
    private static final Logger LOGGER = FlexLogger.getLogger(ReceptionHandlerConfigurationParametersJsonAdapter.class);

    private static final String PARAMETER_CLASS_NAME = "parameterClassName";
    private static final String RECEPTION_HANDLER_PARAMETERS = "parameters";

    @Override
    public ReceptionHandlerConfigurationParameterGroup deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        final JsonObject jsonObject = json.getAsJsonObject();

        final String receptionHandlerParameterClassName = getParameterGroupClassName(jsonObject);
        Class<?> receptionHandlerParameterClass = getParameterGroupClass(receptionHandlerParameterClassName);

        return context.deserialize(jsonObject.get(RECEPTION_HANDLER_PARAMETERS), receptionHandlerParameterClass);
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

    private Class<?> getParameterGroupClass(final String receptionHAndlerParameterClassName) {
        Class<?> receptionHandlerParameterClass = null;
        try {
            receptionHandlerParameterClass = Class.forName(receptionHAndlerParameterClassName);
        } catch (final ClassNotFoundException e) {
            final String errorMessage = "parameter \"" + PARAMETER_CLASS_NAME + "\" value \""
                    + receptionHAndlerParameterClassName + "\", could not find class";
            LOGGER.warn(errorMessage, e);
            throw new IllegalArgumentException(errorMessage, e);
        }
        return receptionHandlerParameterClass;
    }

}
