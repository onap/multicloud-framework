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

package org.onap.policy.distribution.reception.handling.sdc;

import java.util.List;

/**
 * This class builds an instance of {@link SdcReceptionHandlerConfigurationParameterGroup} class.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class SdcReceptionHandlerConfigurationParameterBuilder {

    private boolean activeserverTlsAuth;
    private boolean filterinEmptyResources;
    private boolean useHttpsWithDmaap;
    private boolean useHttpsWithSDC;
    private int pollingTimeout;
    private int pollingInterval;
    private String user;
    private String password;
    private String consumerId;
    private String consumerGroup;
    private String asdcAddress;
    private String environmentName;
    private String keystorePath;
    private String keystorePassword;
    private int httpsproxyPort;
    private int httpproxyPort;
    private String httpsproxyHost;
    private String httpproxyHost;
    private List<String> messageBusAddress;
    private List<String> artifactTypes;
    private int retryDelay;

    /**
     * Set activeserverTlsAuth to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param activeserverTlsAuth the activeserverTlsAuth
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setActiveserverTlsAuth(final boolean activeserverTlsAuth) {
        this.activeserverTlsAuth = activeserverTlsAuth;
        return this;
    }

    /**
     * Set filterinEmptyResources to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param filterinEmptyResources the filterinEmptyResources
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setFilterinEmptyResources(
            final boolean filterinEmptyResources) {
        this.filterinEmptyResources = filterinEmptyResources;
        return this;
    }

    /**
     * Set useHttpsWithDmaap to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param useHttpsWithDmaap the useHttpsWithDmaap
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setUseHttpsWithDmaap(final Boolean useHttpsWithDmaap) {
        this.useHttpsWithDmaap = useHttpsWithDmaap;
        return this;
    }

    /**
     * Set pollingInterval to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param pollingInterval the pollingInterval
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setPollingInterval(final int pollingInterval) {
        this.pollingInterval = pollingInterval;
        return this;
    }

    /**
     * Set pollingTimeout to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param pollingTimeout the pollingTimeout
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setPollingTimeout(final int pollingTimeout) {
        this.pollingTimeout = pollingTimeout;
        return this;
    }

    /**
     * Set asdcAddress to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param asdcAddress the asdcAddress
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setAsdcAddress(final String asdcAddress) {
        this.asdcAddress = asdcAddress;
        return this;
    }

    /**
     * Set user to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param user the user
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setUser(final String user) {
        this.user = user;
        return this;
    }

    /**
     * Set password to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param password the password
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setPassword(final String password) {
        this.password = password;
        return this;
    }

    /**
     * Set consumerId to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param consumerId the consumerId
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setConsumerId(final String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    /**
     * Set consumerGroup to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param consumerGroup the consumerGroup
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setConsumerGroup(final String consumerGroup) {
        this.consumerGroup = consumerGroup;
        return this;
    }

    /**
     * Set environmentName to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param environmentName the environmentName
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setEnvironmentName(final String environmentName) {
        this.environmentName = environmentName;
        return this;
    }

    /**
     * Set keystorePath to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param keystorePath the keystorePath
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setKeystorePath(final String keystorePath) {
        this.keystorePath = keystorePath;
        return this;
    }

    /**
     * Set keystorePassword to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param keystorePassword the keystorePassword
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }

    /**
     * Set messageBusAddress to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param messageBusAddress the messageBusAddress
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setMessageBusAddress(final List<String> messageBusAddress) {
        this.messageBusAddress = messageBusAddress;
        return this;
    }

    /**
     * Set artifactTypes to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param artifactTypes the artifactTypes
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setArtifactTypes(final List<String> artifactTypes) {
        this.artifactTypes = artifactTypes;
        return this;
    }

    /**
     * Set retryDelay to this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param retryDelay the retryDelay
     */
    public SdcReceptionHandlerConfigurationParameterBuilder setRetryDelay(final int retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    /**
     * Returns the active server TlsAuth of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the activeserverTlsAuth
     */
    public boolean isActiveserverTlsAuth() {
        return activeserverTlsAuth;
    }

    /**
     * Returns the isFilterinEmptyResources flag of this {@link SdcReceptionHandlerConfigurationParameterBuilder}
     * instance.
     *
     * @return the isFilterinEmptyResources
     */
    public boolean isFilterinEmptyResources() {
        return filterinEmptyResources;
    }

    /**
     * Returns the isUseHttpsWithDmaap flag of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the isUseHttpsWithDmaap
     */
    public Boolean getIsUseHttpsWithDmaap() {
        return useHttpsWithDmaap;
    }

    /**
     * Returns the isUseHttpsWithSDC flag of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the isUseHttpsWithSDC
     */
    public Boolean getIsUseHttpsWithSDC() {
        return useHttpsWithSDC;
    }

    /**
     * Returns the polling interval of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the pollingInterval
     */
    public int getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Returns the polling timeout of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the pollingTimeout
     */
    public int getPollingTimeout() {
        return pollingTimeout;
    }

    /**
     * Returns the asdc address of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the asdcAddress
     */
    public String getAsdcAddress() {
        return asdcAddress;
    }

    /**
     * Returns the user of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the password of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the consumer id of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the consumerId
     */
    public String getConsumerId() {
        return consumerId;
    }

    /**
     * Returns the consumer group of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the consumerGroup
     */
    public String getConsumerGroup() {
        return consumerGroup;
    }

    /**
     * Returns the environment name of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the environmentName
     */
    public String getEnvironmentName() {
        return environmentName;
    }

    /**
     * Returns the keystore path of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the keystorePath
     */
    public String getKeystorePath() {
        return keystorePath;
    }

    /**
     * Returns the keystore password of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the keystorePassword
     */
    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * Returns the message bus address of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the messageBusAddress
     */
    public List<String> getMessageBusAddress() {
        return messageBusAddress;
    }

    /**
     * Returns the artifact types of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the artifactTypes
     */
    public List<String> getArtifactTypes() {
        return artifactTypes;
    }

    /**
     * Returns the retryDelay of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the retryDelay
     */
    public int getRetryDelay() {
        return retryDelay;
    }
    /**
     * Returns the https proxy port of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the httpsproxyPort
     */
    public int getHttpsProxyPort() {
        return httpsproxyPort;
    }
    /**
     * Returns the https proxy host of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the httpsproxyHost
     */
    public String getHttpsProxyHost() {
        return httpsproxyHost;
    }
        /**
     * Returns the http proxy port of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the httpproxyPort
     */
    public int getHttpProxyPort() {
        return httpproxyPort;
    }
    /**
     * Returns the http proxy host of this {@link SdcReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the httpsproxyHost
     */
    public String getHttpProxyHost() {
        return httpproxyHost;
    }
}


