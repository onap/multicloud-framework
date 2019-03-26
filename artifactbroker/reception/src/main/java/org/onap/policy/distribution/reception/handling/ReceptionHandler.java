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

package org.onap.policy.distribution.reception.handling;

import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PluginTerminationException;

/**
 * Handles input into Policy Distribution which may be decoded into a Policy.
 */
public interface ReceptionHandler {

    /**
     * Initialize the reception handler with the given parameters.
     *
     * @param parameterGroupName the name of the parameter group containing the configuration for the reception handler
     * @throws PluginInitializationException exception if it occurs
     */
    void initialize(String parameterGroupName) throws PluginInitializationException;

    /**
     * Destroy the reception handler, removing any subscriptions and releasing all resources.
     *
     * @throws PluginTerminationException if it occurs
     */
    void destroy() throws PluginTerminationException;

}
