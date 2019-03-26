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

package org.onap.policy.distribution.model;

/**
 * This class represents a policy .
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyAsString implements Policy {
    private String policyName;
    private String policyType;
    private String policy;

    /**
     * Constructor for creating instance of {@link PolicyAsString}.
     *
     * @param policyName the policy file name
     * @param policyType the policy type
     * @param policy the policy
     */
    public PolicyAsString(final String policyName, final String policyType, final String policy) {
        this.policyName = policyName;
        this.policyType = policyType;
        this.policy = policy;
    }

    /**
     * Returns the policy of this {@link PolicyAsString} instance.
     *
     * @return the policy
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getPolicyName() {
        return policyName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getPolicyType() {
        return policyType;
    }
}
