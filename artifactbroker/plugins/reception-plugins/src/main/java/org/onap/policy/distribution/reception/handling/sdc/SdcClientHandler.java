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

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements TimerTask for calling life cycle methods of SdcClient iteratively after specified interval
 * until the operation is successful.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class SdcClientHandler extends TimerTask {

    private SdcReceptionHandler sdcReceptionHandler;
    private SdcClientOperationType operationType;
    private Timer timer;

    public enum SdcClientOperationType {
        START, STOP
    }

    /**
     * Constructs an instance of {@link SdcClientHandler} class.
     *
     * @param sdcReceptionHandler the sdcReceptionHandler
     */
    public SdcClientHandler(final SdcReceptionHandler sdcReceptionHandler, final SdcClientOperationType operationType,
            final long retryDelay) {
        this.sdcReceptionHandler = sdcReceptionHandler;
        this.operationType = operationType;
        timer = new Timer(false);
        timer.scheduleAtFixedRate(this, 0, retryDelay * 1000L);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        if (SdcClientOperationType.START.equals(operationType)) {
            sdcReceptionHandler.initializeSdcClient();
            sdcReceptionHandler.startSdcClient();
        } else {
            sdcReceptionHandler.stopSdcClient();
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean cancel() {
        timer.cancel();
        return true;
    }
}
