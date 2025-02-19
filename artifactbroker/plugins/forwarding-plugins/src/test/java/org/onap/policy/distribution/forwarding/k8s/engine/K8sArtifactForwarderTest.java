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

package org.onap.policy.distribution.forwarding.k8s.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.BeforeClass;
import org.junit.Test;

import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarder;
import org.onap.policy.distribution.forwarding.k8s.K8sArtifactForwarderParameterGroup.K8sArtifactForwarderParameterGroupBuilder;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.model.CloudArtifact;
import org.onap.policy.distribution.model.GsonUtil;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.VfModuleModel;

import org.onap.sdc.api.notification.IArtifactInfo;

public class K8sArtifactForwarderTest {

    private static final BusTopicParams BUS_TOPIC_PARAMS = BusTopicParams.builder().useHttps(false).hostname("myHost")
            .port(1234).userName("myUser").password("myPassword").managed(true).build();
    private static final String CLIENT_AUTH = "ClientAuth";
    private static final String CLIENT_AUTH_VALUE = "myClientAuth";
    private static final String PDP_GROUP_VALUE = "myPdpGroup";
    private HashMap<String, Object> headers = new HashMap<>();
    private BusTopicParamsMatcher matcher = new BusTopicParamsMatcher(BUS_TOPIC_PARAMS);

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        ParameterGroup parameterGroup = new K8sArtifactForwarderParameterGroupBuilder()
                .setUseHttps(BUS_TOPIC_PARAMS.isUseHttps()).setHostname(BUS_TOPIC_PARAMS.getHostname())
                .setPort(BUS_TOPIC_PARAMS.getPort()).setUserName(BUS_TOPIC_PARAMS.getUserName())
                .setPassword(BUS_TOPIC_PARAMS.getPassword()).setClientAuth(CLIENT_AUTH_VALUE)
                .setIsManaged(BUS_TOPIC_PARAMS.isManaged()).setPdpGroup(PDP_GROUP_VALUE).build();
        parameterGroup.setName("xacmlPdpConfiguration");
        ParameterService.register(parameterGroup);
    }

    @Test
    public void testForwardPolicy()
            throws KeyManagementException, NoSuchAlgorithmException, NoSuchFieldException,SecurityException,
            IllegalArgumentException, IllegalAccessException, IOException, ClassNotFoundException {

        CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        //when(httpClientMock.execute(any(HttpPost.class))).thenReturn(Response.ok().build());
        //when(httpClientMock.execute(anyObject())).thenReturn(CloseableHttpResponse.ok().build());
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        //when(entity.getContent()).thenReturn(getClass().getClassLoader().getResourceAsStream("result.txt"));
        //when(entity.getContent()).thenReturn("result of content");
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "{foo : 'bar'}".getBytes()));
        when(response.getEntity()).thenReturn(entity);

        when(httpClientMock.execute(anyObject())).thenReturn(response);

        K8sArtifactForwarder forwarder = new K8sArtifactForwarder();
        forwarder.configure("xacmlPdpConfiguration");


        ArrayList<VfModuleModel> vfModuleModels = new ArrayList<VfModuleModel>();
        try {
            // Read the parameters from JSON using Gson

            String data = new String(Files.readAllBytes(Paths.get("src/test/resource/modules.json")));
            Type type = new TypeToken<ArrayList<VfModuleModel>>() {}.getType();
            Gson gson = new Gson();
            vfModuleModels = gson.fromJson(data, type);

            //vfModuleModels= GsonUtil.parseJsonArrayWithGson(data, VfModuleModel.class);
            assertEquals(4, vfModuleModels.size());
        } catch (final Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }

        HashMap<String, IArtifactInfo> artifactHashMap = new HashMap<>();
        artifactHashMap.put("4d4a37ef-6a1f-4cb2-b3c9-b380a5940431",new ArtifactInfoImpl());
        artifactHashMap.put("0a38b7ef-93b9-4d48-856d-efb56d53aab8",new ArtifactInfoImpl());

        CloudArtifact cloudArtifact = new CloudArtifact(vfModuleModels,artifactHashMap);

        forwarder.forward(cloudArtifact);

    }

    class BusTopicParamsMatcher extends BaseMatcher<BusTopicParams> {

        private BusTopicParams busTopicParams;

        BusTopicParamsMatcher(final BusTopicParams busTopicParams) {
            this.busTopicParams = busTopicParams;
        }

        @Override
        public boolean matches(Object arg0) {
            if (arg0 instanceof BusTopicParams) {
                BusTopicParams toCompareTo = (BusTopicParams) arg0;
                return toCompareTo.isUseHttps() == busTopicParams.isUseHttps()
                        && toCompareTo.getHostname().equals(busTopicParams.getHostname())
                        && toCompareTo.getPort() == busTopicParams.getPort()
                        && toCompareTo.getUserName().equals(busTopicParams.getUserName())
                        && toCompareTo.getPassword().equals(busTopicParams.getPassword())
                        && toCompareTo.isManaged() == busTopicParams.isManaged();
            }
            return false;
        }

        @Override
        public void describeTo(Description arg0) {}
    }

    class ArtifactInfoImpl implements IArtifactInfo {

        private String artifactName;
        private String artifactType;
        private String artifactURL;
        private String artifactChecksum;
        private String artifactDescription;
        private Integer artifactTimeout;
        private String artifactVersion;
        private String artifactUUID;
        private String generatedFromUUID;
        private IArtifactInfo generatedArtifact;
        private List<String> relatedArtifacts;
        private List<IArtifactInfo> relatedArtifactsInfo;

        ArtifactInfoImpl(){}

        private ArtifactInfoImpl(IArtifactInfo iArtifactInfo){
            artifactName = iArtifactInfo.getArtifactName();
            artifactType = iArtifactInfo.getArtifactType();
            artifactURL = iArtifactInfo.getArtifactURL();
            artifactChecksum = iArtifactInfo.getArtifactChecksum();
            artifactDescription = iArtifactInfo.getArtifactDescription();
            artifactTimeout = iArtifactInfo.getArtifactTimeout();
            artifactVersion = iArtifactInfo.getArtifactVersion();
            artifactUUID = iArtifactInfo.getArtifactUUID();
            generatedArtifact = iArtifactInfo.getGeneratedArtifact();
            relatedArtifactsInfo = iArtifactInfo.getRelatedArtifacts();
            relatedArtifacts = fillRelatedArtifactsUUID(relatedArtifactsInfo);

        }


        private List<String> fillRelatedArtifactsUUID(List<IArtifactInfo> relatedArtifactsInfo) {
            List<String> relatedArtifactsUUID = null;
            if ( relatedArtifactsInfo != null && !relatedArtifactsInfo.isEmpty()) {
                relatedArtifactsUUID = new ArrayList<>();
                for (IArtifactInfo curr: relatedArtifactsInfo) {
                    relatedArtifactsUUID.add(curr.getArtifactUUID());
                }
            }
            return relatedArtifactsUUID;
        }

        public String getArtifactName() {
            return artifactName;
        }

        public void setArtifactName(String artifactName) {
            this.artifactName = artifactName;
        }

        public String getArtifactType() {
            return artifactType;
        }

        public void setArtifactType(String artifactType) {
            this.artifactType = artifactType;
        }

        public String getArtifactURL() {
            return artifactURL;
        }

        public void setArtifactURL(String artifactURL) {
            this.artifactURL = artifactURL;
        }

        public String getArtifactChecksum() {
            return artifactChecksum;
        }

        public void setArtifactChecksum(String artifactChecksum) {
            this.artifactChecksum = artifactChecksum;
        }

        public String getArtifactDescription() {
            return artifactDescription;
        }

        public void setArtifactDescription(String artifactDescription) {
            this.artifactDescription = artifactDescription;
        }

        public Integer getArtifactTimeout() {
            return artifactTimeout;
        }

        public void setArtifactTimeout(Integer artifactTimeout) {
            this.artifactTimeout = artifactTimeout;
        }

        @Override
        public String toString() {
            return "BaseArtifactInfoImpl [artifactName=" + artifactName
                    + ", artifactType=" + artifactType + ", artifactURL="
                    + artifactURL + ", artifactChecksum=" + artifactChecksum
                    + ", artifactDescription=" + artifactDescription
                    + ", artifactVersion=" + artifactVersion
                    + ", artifactUUID=" + artifactUUID
                    + ", artifactTimeout=" + artifactTimeout + "]";
        }

        public String getArtifactVersion() {
            return artifactVersion;
        }

        public void setArtifactVersion(String artifactVersion) {
            this.artifactVersion = artifactVersion;
        }

        public String getArtifactUUID() {
            return artifactUUID;
        }

        public void setArtifactUUID(String artifactUUID) {
            this.artifactUUID = artifactUUID;
        }

        public String getGeneratedFromUUID() {
            return generatedFromUUID;
        }

        public void setGeneratedFromUUID(String generatedFromUUID) {
            this.generatedFromUUID = generatedFromUUID;
        }

        public IArtifactInfo getGeneratedArtifact() {
            return generatedArtifact;
        }

        public void setGeneratedArtifact(IArtifactInfo generatedArtifact) {
            this.generatedArtifact = generatedArtifact;
        }

        public List<IArtifactInfo> getRelatedArtifacts(){
            List<IArtifactInfo> temp = new ArrayList<IArtifactInfo>();
            if( relatedArtifactsInfo != null ){
                temp.addAll(relatedArtifactsInfo);
            }
            return temp;
        }

        public void setRelatedArtifacts(List<String> relatedArtifacts) {
            this.relatedArtifacts = relatedArtifacts;
        }

        public void setRelatedArtifactsInfo(List<IArtifactInfo> relatedArtifactsInfo) {
            this.relatedArtifactsInfo = relatedArtifactsInfo;
        }

        public List<String> getRelatedArtifactsUUID(){
            return relatedArtifacts;
        }

    }


}
