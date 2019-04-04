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

import org.onap.policy.api.PolicyConfigType;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.distribution.model.OptimizationPolicy;

/**
 * Adapts {@link OptimizationPolicy} objects to objects compatible with the XACML PDP API.
 */
public class XacmlPdpOptimizationPolicyAdapter extends AbstractXacmlPdpPolicyAdapter<OptimizationPolicy> {

    /**
     * Create an instance to adapt the given {@link OptimizationPolicy}.
     * 
     * @param optimizationPolicy the {@link OptimizationPolicy} to be adapted
     */
    public XacmlPdpOptimizationPolicyAdapter(final OptimizationPolicy optimizationPolicy) {
        super(optimizationPolicy);
    }

    @Override
    public PolicyParameters getAsPolicyParameters() {
        PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyName(getPolicy().getPolicyName());
        policyParameters.setPolicyDescription(getPolicy().getPolicyDescription());
        policyParameters.setPolicyConfigType(PolicyConfigType.valueOf(getPolicy().getPolicyConfigType()));
        policyParameters.setOnapName(getPolicy().getOnapName());
        policyParameters.setRiskLevel(getPolicy().getRiskLevel());
        policyParameters.setConfigBody(getPolicy().getConfigBody());
        policyParameters.setRiskType(getPolicy().getRiskType());
        return policyParameters;
    }

}
