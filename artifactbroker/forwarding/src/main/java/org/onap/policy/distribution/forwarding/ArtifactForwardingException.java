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

package org.onap.policy.distribution.forwarding;

/**
 * An error has occured when forwarding a policy.
 */
public class ArtifactForwardingException extends Exception {

    private static final long serialVersionUID = 3866850096319435806L;

    /**
     * Construct an instance with the given message.
     * 
     * @param message the error message
     */
    public ArtifactForwardingException(String message) {
        super(message);
    }

    /**
     * Construct an instance with the given message and cause.
     * 
     * @param message the error message
     * @param cause the cause
     */
    public ArtifactForwardingException(String message, Throwable cause) {
        super(message, cause);
    }

}
