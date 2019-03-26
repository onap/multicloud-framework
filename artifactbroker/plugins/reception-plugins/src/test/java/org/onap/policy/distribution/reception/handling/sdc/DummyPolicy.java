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

import org.onap.policy.distribution.model.Policy;

/**
 * Class to create a dummy policy for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DummyPolicy implements Policy {

    private String policyName;
    private String policyType;

    /**
     * Constructor for instantiating {@link DummyPolicy} class.
     *
     * @param policyName the policy name
     * @param policyType the policy type
     */
    public DummyPolicy(final String policyName, final String policyType) {
        super();
        this.policyName = policyName;
        this.policyType = policyType;
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
