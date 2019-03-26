/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel Corp. All rights reserved.
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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;

/**
 * Handles reception of inputs from File System which can be used to decode policies.
 */
public class FileSystemReceptionHandler extends AbstractReceptionHandler {
    private boolean running = false;
    private static final Logger LOGGER = FlexLogger.getLogger(FileSystemReceptionHandler.class);

    @Override
    protected void initializeReception(final String parameterGroupName) {
        LOGGER.debug("FileSystemReceptionHandler init...");
        try {
            final FileSystemReceptionHandlerConfigurationParameterGroup handlerParameters =
                                    ParameterService.get(parameterGroupName);
            main(handlerParameters.getWatchPath());
        } catch (final Exception ex) {
            LOGGER.error(ex);
        }
        running = false;
        LOGGER.debug("FileSystemReceptionHandler main loop exited...");
    }

    @Override
    public void destroy() {
        // Tear down subscription etc
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Main entry point.
     * 
     * @param watchPath Path to watch
     */
    public void main(String watchPath) throws IOException {
        try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {
            final Path dir = Paths.get(watchPath);
            dir.register(watcher, ENTRY_CREATE);
            LOGGER.debug("Watch Service registered for dir: " + dir.getFileName());
            startMainLoop(watcher, dir);
        } catch (final InterruptedException ex) {
            LOGGER.debug(ex);
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("unchecked")
    protected void startMainLoop(WatchService watcher, Path dir) throws InterruptedException {
        WatchKey key;
        running = true;
        while (running) {
            key = watcher.take();

            for (final WatchEvent<?> event : key.pollEvents()) {
                final WatchEvent<Path> ev = (WatchEvent<Path>) event;
                final Path fileName = ev.context();
                LOGGER.debug("new CSAR found: " + fileName);
                createPolicyInputAndCallHandler(dir.toString() + File.separator + fileName.toString());
                LOGGER.debug("CSAR complete: " + fileName);
            }
            final boolean valid = key.reset();
            if (!valid) {
                LOGGER.error("Watch key no longer valid!");
                break;
            }
        }
    }

    protected void createPolicyInputAndCallHandler(final String fileName) {
        try {
            final Csar csarObject = new Csar(fileName);
            inputReceived(csarObject);
        } catch (final PolicyDecodingException ex) {
            LOGGER.error(ex);
        }
    }
}
