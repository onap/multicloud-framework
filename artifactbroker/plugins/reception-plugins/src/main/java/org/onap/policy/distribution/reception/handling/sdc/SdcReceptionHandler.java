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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.handling.sdc.SdcClientHandler.SdcClientOperationType;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.ArtifactDownloadException;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientFactory;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionStatusEnum;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler implements INotificationCallback {

    private static final Logger LOGGER = FlexLogger.getLogger(SdcReceptionHandler.class);
    private static final String SECONDS = "Seconds";

    private SdcReceptionHandlerStatus sdcReceptionHandlerStatus = SdcReceptionHandlerStatus.STOPPED;
    private IDistributionClient distributionClient;
    private SdcConfiguration sdcConfig;
    private volatile int nbOfNotificationsOngoing = 0;
    private int retryDelay;
    private SdcClientHandler sdcClientHandler;

    private enum DistributionStatusType {
        DOWNLOAD, DEPLOY
    }

    @Override
    protected void initializeReception(final String parameterGroupName) {
        final SdcReceptionHandlerConfigurationParameterGroup handlerParameters =
                ParameterService.get(parameterGroupName);
        retryDelay = handlerParameters.getRetryDelay() < 30 ? 30 : handlerParameters.getRetryDelay();
        sdcConfig = new SdcConfiguration(handlerParameters);
        distributionClient = createSdcDistributionClient();
        sdcClientHandler = new SdcClientHandler(this, SdcClientOperationType.START, retryDelay);
    }

    @Override
    public void destroy() {
        if (distributionClient != null) {
            sdcClientHandler = new SdcClientHandler(this, SdcClientOperationType.STOP, retryDelay);
        }
    }

    @Override
    public void activateCallback(final INotificationData notificationData) {
        LOGGER.debug("Receieved the notification from SDC with ID: " + notificationData.getDistributionID());
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.BUSY);
        processCsarServiceArtifacts(notificationData);
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
        LOGGER.debug("Processed the notification from SDC with ID: " + notificationData.getDistributionID());
    }

    /**
     * Method to change the status of this reception handler instance.
     *
     * @param newStatus the new status
     */
    private final synchronized void changeSdcReceptionHandlerStatus(final SdcReceptionHandlerStatus newStatus) {
        switch (newStatus) {
            case INIT:
            case STOPPED:
                sdcReceptionHandlerStatus = newStatus;
                break;
            case IDLE:
                handleIdleStatusChange(newStatus);
                break;
            case BUSY:
                ++nbOfNotificationsOngoing;
                sdcReceptionHandlerStatus = newStatus;
                break;
            default:
                break;
        }
    }

    /**
     * Creates an instance of {@link IDistributionClient} from {@link DistributionClientFactory}.
     *
     * @return the {@link IDistributionClient} instance
     */
    protected IDistributionClient createSdcDistributionClient() {
        return DistributionClientFactory.createDistributionClient();
    }

    /**
     * Method to initialize the SDC client.
     *
     */
    protected void initializeSdcClient() {

        LOGGER.debug("Initializing the SDC Client...");
        if (sdcReceptionHandlerStatus != SdcReceptionHandlerStatus.STOPPED) {
            LOGGER.error("The SDC Client is already initialized");
            return;
        }
        final IDistributionClientResult clientResult = distributionClient.init(sdcConfig, this);
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            LOGGER.error("SDC client initialization failed with reason:" + clientResult.getDistributionMessageResult()
                    + ". Initialization will be retried after " + retryDelay + SECONDS);
            return;
        }
        LOGGER.debug("SDC Client is initialized successfully");
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.INIT);
    }

    /**
     * Method to start the SDC client.
     *
     */
    protected void startSdcClient() {

        LOGGER.debug("Going to start the SDC Client...");
        if (sdcReceptionHandlerStatus != SdcReceptionHandlerStatus.INIT) {
            LOGGER.error("The SDC Client is not initialized");
            return;
        }
        final IDistributionClientResult clientResult = distributionClient.start();
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            LOGGER.error("SDC client start failed with reason:" + clientResult.getDistributionMessageResult()
                    + ". Start will be retried after " + retryDelay + SECONDS);
            return;
        }
        LOGGER.debug("SDC Client is started successfully");
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
        sdcClientHandler.cancel();
    }

    /**
     * Method to stop the SDC client.
     *
     */
    protected void stopSdcClient() {
        LOGGER.debug("Going to stop the SDC Client...");
        final IDistributionClientResult clientResult = distributionClient.stop();
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            LOGGER.error("SDC client stop failed with reason:" + clientResult.getDistributionMessageResult()
                    + ". Stop will be retried after " + retryDelay + SECONDS);
            return;
        }
        LOGGER.debug("SDC Client is stopped successfully");
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.STOPPED);
        sdcClientHandler.cancel();
    }

    /**
     * Method to process csar service artifacts from incoming SDC notification.
     *
     * @param notificationData the notification from SDC
     */
    public void processCsarServiceArtifacts(final INotificationData notificationData) {
        boolean artifactsProcessedSuccessfully = true;
        DistributionStatisticsManager.updateTotalDistributionCount();
        for (final IArtifactInfo artifact : notificationData.getServiceArtifacts()) {
            try {
                final IDistributionClientDownloadResult resultArtifact =
                        downloadTheArtifact(artifact, notificationData);
                final Path filePath = writeArtifactToFile(artifact, resultArtifact);
                final Csar csarObject = new Csar(filePath.toString());
                inputReceived(csarObject);
                sendDistributionStatus(DistributionStatusType.DEPLOY, artifact.getArtifactURL(),
                        notificationData.getDistributionID(), DistributionStatusEnum.DEPLOY_OK, null);
                deleteArtifactFile(filePath);
            } catch (final ArtifactDownloadException | PolicyDecodingException exp) {
                LOGGER.error("Failed to process csar service artifacts ", exp);
                artifactsProcessedSuccessfully = false;
                sendDistributionStatus(DistributionStatusType.DEPLOY, artifact.getArtifactURL(),
                        notificationData.getDistributionID(), DistributionStatusEnum.DEPLOY_ERROR,
                        "Failed to deploy the artifact due to: " + exp.getMessage());
            }
        }
        if (artifactsProcessedSuccessfully) {
            DistributionStatisticsManager.updateDistributionSuccessCount();
            sendComponentDoneStatus(notificationData.getDistributionID(), DistributionStatusEnum.COMPONENT_DONE_OK,
                    null);
        } else {
            DistributionStatisticsManager.updateDistributionFailureCount();
            sendComponentDoneStatus(notificationData.getDistributionID(), DistributionStatusEnum.COMPONENT_DONE_ERROR,
                    "Failed to process the artifact");
        }
    }

    /**
     * Method to download the distribution artifact.
     *
     * @param artifact the artifact
     * @return the download result
     * @throws ArtifactDownloadException if download fails
     */
    private IDistributionClientDownloadResult downloadTheArtifact(final IArtifactInfo artifact,
            final INotificationData notificationData) throws ArtifactDownloadException {

        DistributionStatisticsManager.updateTotalDownloadCount();
        final IDistributionClientDownloadResult downloadResult = distributionClient.download(artifact);
        if (!downloadResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            DistributionStatisticsManager.updateDownloadFailureCount();
            final String message = "Failed to download artifact with name: " + artifact.getArtifactName() + " due to: "
                    + downloadResult.getDistributionMessageResult();
            LOGGER.error(message);
            sendDistributionStatus(DistributionStatusType.DOWNLOAD, artifact.getArtifactURL(),
                    notificationData.getDistributionID(), DistributionStatusEnum.DOWNLOAD_ERROR, message);
            throw new ArtifactDownloadException(message);
        }
        DistributionStatisticsManager.updateDownloadSuccessCount();
        sendDistributionStatus(DistributionStatusType.DOWNLOAD, artifact.getArtifactURL(),
                notificationData.getDistributionID(), DistributionStatusEnum.DOWNLOAD_OK, null);
        return downloadResult;
    }

    /**
     * Method to write the downloaded distribution artifact to local file system.
     *
     * @param artifact the notification artifact
     * @param resultArtifact the download result artifact
     * @return the local path of written file
     * @throws ArtifactDownloadException if error occurs while writing the artifact
     */
    private Path writeArtifactToFile(final IArtifactInfo artifact,
            final IDistributionClientDownloadResult resultArtifact) throws ArtifactDownloadException {
        try {
            final byte[] payloadBytes = resultArtifact.getArtifactPayload();
            final File tempArtifactFile = File.createTempFile(artifact.getArtifactName(), ".csar");
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempArtifactFile)) {
                fileOutputStream.write(payloadBytes, 0, payloadBytes.length);
                return tempArtifactFile.toPath();
            }
        } catch (final Exception exp) {
            final String message = "Failed to write artifact to local repository";
            LOGGER.error(message, exp);
            throw new ArtifactDownloadException(message, exp);
        }
    }

    /**
     * Method to delete the downloaded notification artifact from local file system.
     *
     * @param filePath the path of file
     */
    private void deleteArtifactFile(final Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (final IOException exp) {
            LOGGER.error("Failed to delete the downloaded artifact file", exp);
        }
    }

    /**
     * Sends the distribution status to SDC using the input values.
     *
     * @param statusType the status type
     * @param artifactUrl the artifact url
     * @param distributionId the distribution id
     * @param status the status
     * @param errorReason the error reason
     */
    private void sendDistributionStatus(final DistributionStatusType statusType, final String artifactUrl,
            final String distributionId, final DistributionStatusEnum status, final String errorReason) {

        IDistributionClientResult clientResult;
        final DistributionStatusMessageBuilder messageBuilder = new DistributionStatusMessageBuilder()
                .setArtifactUrl(artifactUrl).setConsumerId(sdcConfig.getConsumerID()).setDistributionId(distributionId)
                .setDistributionStatus(status).setTimestamp(System.currentTimeMillis());
        final IDistributionStatusMessage message = new DistributionStatusMessage(messageBuilder);
        if (DistributionStatusType.DOWNLOAD.equals(statusType)) {
            if (errorReason != null) {
                clientResult = distributionClient.sendDownloadStatus(message, errorReason);
            } else {
                clientResult = distributionClient.sendDownloadStatus(message);
            }
        } else {
            if (errorReason != null) {
                clientResult = distributionClient.sendDeploymentStatus(message, errorReason);
            } else {
                clientResult = distributionClient.sendDeploymentStatus(message);
            }
        }
        final StringBuilder loggerMessage = new StringBuilder();
        loggerMessage.append("distribution status to SDC with values - ").append("DistributionId")
                .append(distributionId).append(" Artifact: ").append(artifactUrl).append(" StatusType: ")
                .append(statusType.name()).append(" Status: ").append(status.name());
        if (errorReason != null) {
            loggerMessage.append(" ErrorReason: ").append(errorReason);
        }
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            loggerMessage.insert(0, "Failed sending ");
            LOGGER.debug(loggerMessage);
        } else {
            loggerMessage.insert(0, "Successfully Sent ");
            LOGGER.debug(loggerMessage);
        }
    }

    /**
     * Sends the component done status to SDC using the input values.
     *
     * @param distributionId the distribution Id
     * @param status the distribution status
     * @param errorReason the error reason
     */
    private void sendComponentDoneStatus(final String distributionId, final DistributionStatusEnum status,
            final String errorReason) {
        IDistributionClientResult clientResult;
        final ComponentDoneStatusMessageBuilder messageBuilder = new ComponentDoneStatusMessageBuilder()
                .setConsumerId(sdcConfig.getConsumerID()).setDistributionId(distributionId)
                .setDistributionStatus(status).setTimestamp(System.currentTimeMillis());
        final IComponentDoneStatusMessage message = new ComponentDoneStatusMessage(messageBuilder);
        if (errorReason == null) {
            clientResult = distributionClient.sendComponentDoneStatus(message);
        } else {
            clientResult = distributionClient.sendComponentDoneStatus(message, errorReason);
        }

        final StringBuilder loggerMessage = new StringBuilder();
        loggerMessage.append("component done status to SDC with values - ").append("DistributionId")
                .append(distributionId).append(" Status: ").append(status.name());
        if (errorReason != null) {
            loggerMessage.append(" ErrorReason: ").append(errorReason);
        }
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            loggerMessage.insert(0, "Failed sending ");
            LOGGER.debug(loggerMessage);
        } else {
            loggerMessage.insert(0, "Successfully Sent ");
            LOGGER.debug(loggerMessage);
        }
    }

    /**
     * Handle the status change of {@link SdcReceptionHandler} to Idle.
     *
     * @param newStatus the new status
     */
    private void handleIdleStatusChange(final SdcReceptionHandlerStatus newStatus) {
        if (nbOfNotificationsOngoing > 1) {
            --nbOfNotificationsOngoing;
        } else {
            nbOfNotificationsOngoing = 0;
            sdcReceptionHandlerStatus = newStatus;
        }
    }
}
