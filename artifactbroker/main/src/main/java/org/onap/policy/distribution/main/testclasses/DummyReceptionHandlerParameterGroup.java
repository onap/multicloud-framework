/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Intel. All rights reserved.
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

package org.onap.policy.distribution.main.testclasses;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

public class DummyReceptionHandlerParameterGroup extends ReceptionHandlerConfigurationParameterGroup {

    private String myStringParameter;
    private int myIntegerParameter;
    private boolean myBooleanParameter;

    /**
     * Inner static class is to used as a Builder.
     *
     */
    public static class DummyReceptionHandlerParameterGroupBuilder {
        private String myStringParameter;
        private int myIntegerParameter;
        private boolean myBooleanParameter;

        public DummyReceptionHandlerParameterGroupBuilder setMyStringParameter(final String val) {
            myStringParameter = val;
            return this;
        }

        public DummyReceptionHandlerParameterGroupBuilder setMyIntegerParameter(final int val) {
            myIntegerParameter = val;
            return this;
        }

        public DummyReceptionHandlerParameterGroupBuilder setMyBooleanParameter(final boolean val) {
            myBooleanParameter = val;
            return this;
        }

        /**
         * Creates a new DummyReceptionHandlerConfigurationParameterGroup instance.
         */
        public DummyReceptionHandlerParameterGroup build() {
            return new DummyReceptionHandlerParameterGroup(this);
        }
    }

    /**
     * The constructor for instantiating PssdConfigurationParameterGroup. It is kept private so that
     * it could only be called by PssdConfigurationBuilder.
     *
     * @param builder stores all the values used by PssdConfigurationParametersGroup
     */
    private DummyReceptionHandlerParameterGroup(final DummyReceptionHandlerParameterGroupBuilder builder) {
        myStringParameter = builder.myStringParameter;
        myIntegerParameter = builder.myIntegerParameter;
        myBooleanParameter = builder.myBooleanParameter;
    }

    public String getMyStringParameter() {
        return myStringParameter;
    }

    public int getMyIntegerParameter() {
        return myIntegerParameter;
    }

    public boolean isMyBooleanParameter() {
        return myBooleanParameter;
    }


    /**
     * {@inheritDoc}.
     */
    @Override
    public GroupValidationResult validate() {
        return new GroupValidationResult(this);
    }

}

