/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.sdc;

import java.io.File;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json
 * format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
 */
public class FileSystemReceptionHandlerConfigurationParameterGroup extends ReceptionHandlerConfigurationParameterGroup {

    private String watchPath;

    /**
     * The constructor for instantiating {@link FileSystemReceptionHandlerConfigurationParameterGroup} class.
     *
     * @param builder the SDC configuration builder
     */
    public FileSystemReceptionHandlerConfigurationParameterGroup(
            final FileSystemReceptionHandlerConfigurationParameterBuilder builder) {
        watchPath = builder.getWatchPath();
    }

    public String getWatchPath() {
        return watchPath;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        validatePathElement(validationResult, watchPath, "watchPath");
        return validationResult;
    }


    /**
     * Validate the string element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validatePathElement(final GroupValidationResult validationResult, final String element,
            final String elementName) {
        boolean valid = false;
        if (element != null) {
            File file = new File(element);
            if (file.exists() && file.isDirectory()) {
                valid = true;
            }
        }
        if (!valid) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a valid directory");
        }
    }
}

