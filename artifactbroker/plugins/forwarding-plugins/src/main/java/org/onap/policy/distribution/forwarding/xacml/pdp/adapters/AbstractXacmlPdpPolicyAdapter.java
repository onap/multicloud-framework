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

package org.onap.policy.distribution.forwarding.xacml.pdp.adapters;

import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyAdapter;
import org.onap.policy.distribution.model.Policy;

/**
 * Base class for {@link XacmlPdpPolicyAdapter} implementations.
 * 
 * @param <T> the type of policy the adapter handles
 */
public abstract class AbstractXacmlPdpPolicyAdapter<T extends Policy> implements XacmlPdpPolicyAdapter<T> {

    private T policy;

    protected AbstractXacmlPdpPolicyAdapter(T policy) {
        this.policy = policy;
    }

    @Override
    public T getPolicy() {
        return policy;
    }

    @Override
    public PushPolicyParameters getAsPushPolicyParameters(String pdpGroups) {
        PushPolicyParameters pushPolicyParameters = new PushPolicyParameters();
        pushPolicyParameters.setPolicyName(policy.getPolicyName());
        pushPolicyParameters.setPolicyType(policy.getPolicyType());
        pushPolicyParameters.setPdpGroup(pdpGroups);
        return pushPolicyParameters;
    }

}
