/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2026 Deutsche Telekom AG. All rights reserved.
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

import static org.junit.Assert.assertEquals;

import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.DistributionActionResultEnum;

public class TestSdcReceptionHandler {

    private static final String DISTRIBUTION_ID = "dist-1";

    private final List<String> sentStatuses = new ArrayList<>();
    private final Map<String, IDistributionClientDownloadResult> downloads = new HashMap<>();
    private SdcReceptionHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new SdcReceptionHandler() {
            @Override
            protected void inputReceived(final PolicyInput policyInput) throws PolicyDecodingException {
                // no artifact forwarders are configured in this test
            }
        };
        final SdcReceptionHandlerConfigurationParameterGroup parameters = new GsonBuilder().create().fromJson(
                new FileReader("src/test/resources/handling-sdc.json"),
                SdcReceptionHandlerConfigurationParameterGroup.class);
        setField("sdcConfig", new SdcConfiguration(parameters));
        setField("distributionClient", recordingClient());
    }

    @Test
    public void distributionWithoutVfIsReportedAsDone() {
        handler.activateCallback(notification(resource("PNF")));

        assertEquals(Collections.singletonList("COMPONENT_DONE_OK"), sentStatuses);
    }

    @Test
    public void distributionWithSeveralVfsIsReportedAsDoneOnceAfterDeployStatus() {
        final IArtifactInfo vfModulesMetadata = artifact("vf-modules-metadata.json", "VF_MODULES_METADATA");
        downloads.put(vfModulesMetadata.getArtifactName(), downloadResult(DistributionActionResultEnum.SUCCESS));

        handler.activateCallback(notification(resource("VF", vfModulesMetadata), resource("VF")));

        assertEquals(Arrays.asList("DOWNLOAD_OK vf-modules-metadata.json", "DEPLOY_OK vf-modules-metadata.json",
                "COMPONENT_DONE_OK"), sentStatuses);
    }

    @Test
    public void distributionWithOneFailedVfIsReportedAsErrorOnce() {
        final IArtifactInfo vfModulesMetadata = artifact("vf-modules-metadata.json", "VF_MODULES_METADATA");
        downloads.put(vfModulesMetadata.getArtifactName(), downloadResult(DistributionActionResultEnum.FAIL));

        handler.activateCallback(notification(resource("VF", vfModulesMetadata), resource("VF")));

        assertEquals(Arrays.asList("DOWNLOAD_ERROR vf-modules-metadata.json", "DEPLOY_ERROR vf-modules-metadata.json",
                "COMPONENT_DONE_ERROR"), sentStatuses);
    }

    private void setField(final String name, final Object value) throws ReflectiveOperationException {
        final Field field = SdcReceptionHandler.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(handler, value);
    }

    private IDistributionClient recordingClient() {
        final IDistributionClientResult success = proxy(IDistributionClientResult.class, (method, args) ->
                "getDistributionActionResult".equals(method) ? DistributionActionResultEnum.SUCCESS : null);
        return proxy(IDistributionClient.class, (method, args) -> {
            switch (method) {
                case "download":
                    return downloads.get(((IArtifactInfo) args[0]).getArtifactName());
                case "sendDownloadStatus":
                case "sendDeploymentStatus":
                    final IDistributionStatusMessage status = (IDistributionStatusMessage) args[0];
                    sentStatuses.add(status.getStatus() + " " + status.getArtifactURL());
                    return success;
                case "sendComponentDoneStatus":
                    sentStatuses.add(((IComponentDoneStatusMessage) args[0]).getStatus().name());
                    return success;
                default:
                    return null;
            }
        });
    }

    private static IDistributionClientDownloadResult downloadResult(final DistributionActionResultEnum result) {
        return proxy(IDistributionClientDownloadResult.class, (method, args) -> {
            switch (method) {
                case "getDistributionActionResult":
                    return result;
                case "getArtifactPayload":
                    return "[]".getBytes(StandardCharsets.UTF_8);
                default:
                    return null;
            }
        });
    }

    private static IArtifactInfo artifact(final String name, final String type) {
        return proxy(IArtifactInfo.class, (method, args) -> {
            switch (method) {
                case "getArtifactName":
                case "getArtifactURL":
                case "getArtifactUUID":
                    return name;
                case "getArtifactType":
                    return type;
                default:
                    return null;
            }
        });
    }

    private static IResourceInstance resource(final String type, final IArtifactInfo... artifacts) {
        return proxy(IResourceInstance.class, (method, args) -> {
            switch (method) {
                case "getResourceType":
                    return type;
                case "getArtifacts":
                    return Arrays.asList(artifacts);
                default:
                    return null;
            }
        });
    }

    private static INotificationData notification(final IResourceInstance... resources) {
        return proxy(INotificationData.class, (method, args) -> {
            switch (method) {
                case "getDistributionID":
                    return DISTRIBUTION_ID;
                case "getResources":
                    return Arrays.asList(resources);
                default:
                    return null;
            }
        });
    }

    private interface Answer {
        Object answer(String method, Object[] args);
    }

    private static <T> T proxy(final Class<T> type, final Answer answer) {
        final InvocationHandler handler = (instance, method, args) -> {
            switch (method.getName()) {
                case "hashCode":
                    return System.identityHashCode(instance);
                case "equals":
                    return instance == args[0];
                case "toString":
                    return type.getSimpleName();
                default:
                    return answer.answer(method.getName(), args);
            }
        };
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler));
    }
}
