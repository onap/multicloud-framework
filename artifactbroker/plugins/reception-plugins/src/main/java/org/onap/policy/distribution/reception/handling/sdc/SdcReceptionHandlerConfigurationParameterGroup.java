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

import java.util.List;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json
 * format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
 */
public class SdcReceptionHandlerConfigurationParameterGroup extends ReceptionHandlerConfigurationParameterGroup {

    private String asdcAddress;
    private List<String> messageBusAddress;
    private String user;
    private String password;
    private int pollingInterval;
    private int pollingTimeout;
    private int retryDelay;
    private String consumerId;
    private List<String> artifactTypes;
    private String consumerGroup;
    private String environmentName;
    private String keyStorePath;
    private String keyStorePassword;
    private boolean activeServerTlsAuth;
    private boolean isFilterInEmptyResources;
    private boolean isUseHttpsWithDmaap;

    /**
     * The constructor for instantiating {@link SdcReceptionHandlerConfigurationParameterGroup} class.
     *
     * @param builder the SDC configuration builder
     */
    public SdcReceptionHandlerConfigurationParameterGroup(
            final SdcReceptionHandlerConfigurationParameterBuilder builder) {
        asdcAddress = builder.getAsdcAddress();
        messageBusAddress = builder.getMessageBusAddress();
        user = builder.getUser();
        password = builder.getPassword();
        pollingInterval = builder.getPollingInterval();
        pollingTimeout = builder.getPollingTimeout();
        retryDelay = builder.getRetryDelay();
        consumerId = builder.getConsumerId();
        artifactTypes = builder.getArtifactTypes();
        consumerGroup = builder.getConsumerGroup();
        environmentName = builder.getEnvironmentName();
        keyStorePath = builder.getKeystorePath();
        keyStorePassword = builder.getKeystorePassword();
        activeServerTlsAuth = builder.isActiveserverTlsAuth();
        isFilterInEmptyResources = builder.isFilterinEmptyResources();
        isUseHttpsWithDmaap = builder.getIsUseHttpsWithDmaap();

    }

    public String getAsdcAddress() {
        return asdcAddress;
    }

    public List<String> getMessageBusAddress() {
        return messageBusAddress;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public int getPollingTimeout() {
        return pollingTimeout;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public List<String> getArtifactTypes() {
        return artifactTypes;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public boolean isActiveServerTlsAuth() {
        return activeServerTlsAuth;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public boolean isFilterInEmptyResources() {
        return isFilterInEmptyResources;
    }

    public boolean isUseHttpsWithDmaap() {
        return isUseHttpsWithDmaap;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        validateStringElement(validationResult, asdcAddress, "asdcAddress");
        validateStringElement(validationResult, user, "user");
        validateStringElement(validationResult, consumerId, "consumerId");
        validateStringElement(validationResult, consumerGroup, "consumerGroup");
        validateStringElement(validationResult, keyStorePath, "keyStorePath");
        validateStringElement(validationResult, keyStorePassword, "keyStorePassword");
        validateIntElement(validationResult, pollingInterval, "pollingInterval");
        validateIntElement(validationResult, pollingTimeout, "pollingTimeout");
        validateIntElement(validationResult, retryDelay, "retryDelay");
        validateStringListElement(validationResult, messageBusAddress, "messageBusAddress");
        validateStringListElement(validationResult, artifactTypes, "artifactTypes");
        return validationResult;
    }

    /**
     * Validate the integer Element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateIntElement(final GroupValidationResult validationResult, final int element,
            final String elementName) {
        if (!ParameterValidationUtils.validateIntParameter(element)) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a positive integer");
        }
    }

    /**
     * Validate the String List Element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateStringListElement(final GroupValidationResult validationResult, final List<String> element,
            final String elementName) {
        for (final String temp : element) {
            if (!ParameterValidationUtils.validateStringParameter(temp)) {
                validationResult.setResult(elementName, ValidationStatus.INVALID,
                        "the string of " + elementName + "must be a non-blank string");
            }
        }
    }

    /**
     * Validate the string element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateStringElement(final GroupValidationResult validationResult, final String element,
            final String elementName) {
        if (!ParameterValidationUtils.validateStringParameter(element)) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a non-blank string");
        }
    }
}

