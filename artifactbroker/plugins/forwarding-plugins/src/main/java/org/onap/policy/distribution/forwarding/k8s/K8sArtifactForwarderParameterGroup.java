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

package org.onap.policy.distribution.forwarding.k8s;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.main.parameters.ArtifactForwarderConfigurationParameterGroup;

/**
 * Holds the parameters for the{@link K8sArtifactForwarder}.
 */
public class K8sArtifactForwarderParameterGroup extends ArtifactForwarderConfigurationParameterGroup {
    public static final String POLICY_FORWARDER_PLUGIN_CLASS = K8sArtifactForwarder.class.getCanonicalName();

    private boolean useHttps;
    private String hostname;
    private int port;
    private String userName;
    private String password;
    private String clientAuth;
    private boolean isManaged;
    private String pdpGroup;

    /**
     * Construct an instance.
     *
     * @param builder the builder create the instance from
     */
    private K8sArtifactForwarderParameterGroup(final K8sArtifactForwarderParameterGroupBuilder builder) {
        this.useHttps = builder.useHttps;
        this.hostname = builder.hostname;
        this.port = builder.port;
        this.userName = builder.userName;
        this.password = builder.password;
        this.clientAuth = builder.clientAuth;
        this.isManaged = builder.isManaged;
        this.pdpGroup = builder.pdpGroup;
    }

    public boolean isUseHttps() {
        return useHttps;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public boolean isManaged() {
        return isManaged;
    }

    public String getPdpGroup() {
        return pdpGroup;
    }

    /**
     * Builder for K8sArtifactForwarderParameterGroup.
     */
    public static class K8sArtifactForwarderParameterGroupBuilder {
        private boolean useHttps = false;
        private String hostname;
        private int port;
        private String userName;
        private String password;
        private String clientAuth;
        private boolean isManaged = true;
        private String pdpGroup;

        public K8sArtifactForwarderParameterGroupBuilder setUseHttps(final boolean useHttps) {
            this.useHttps = useHttps;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setHostname(final String hostname) {
            this.hostname = hostname;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setPort(final int port) {
            this.port = port;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setClientAuth(final String clientAuth) {
            this.clientAuth = clientAuth;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setIsManaged(final boolean isManaged) {
            this.isManaged = isManaged;
            return this;
        }

        public K8sArtifactForwarderParameterGroupBuilder setPdpGroup(final String pdpGroup) {
            this.pdpGroup = pdpGroup;
            return this;
        }

        /**
         * Creates a new XacmlPapServletArtifactForwarderParameterGroup instance.
         */
        public K8sArtifactForwarderParameterGroup build() {
            return new K8sArtifactForwarderParameterGroup(this);
        }
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (!ParameterValidationUtils.validateStringParameter(hostname)) {
            validationResult.setResult("hostname", ValidationStatus.INVALID,
                    "must be a non-blank string containing hostname/ipaddress");
        }
        if (!ParameterValidationUtils.validateIntParameter(port)) {
            validationResult.setResult("port", ValidationStatus.INVALID, "must be a positive integer containing port");
        }
        if (!ParameterValidationUtils.validateStringParameter(userName)) {
            validationResult.setResult("userName", ValidationStatus.INVALID,
                    "must be a non-blank string containing userName");
        }
        if (!ParameterValidationUtils.validateStringParameter(password)) {
            validationResult.setResult("password", ValidationStatus.INVALID,
                    "must be a non-blank string containing password");
        }
        if (!ParameterValidationUtils.validateStringParameter(clientAuth)) {
            validationResult.setResult("clientAuth", ValidationStatus.INVALID,
                    "must be a non-blank string containing clientAuth");
        }
        if (!ParameterValidationUtils.validateStringParameter(pdpGroup)) {
            validationResult.setResult("pdpGroup", ValidationStatus.INVALID,
                    "must be a non-blank string containing pdpGroup");
        }
        return validationResult;
    }

}
