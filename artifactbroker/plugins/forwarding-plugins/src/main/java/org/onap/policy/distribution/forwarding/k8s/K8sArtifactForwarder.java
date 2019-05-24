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

package org.onap.policy.distribution.forwarding.k8s;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.ArtifactForwarder;
import org.onap.policy.distribution.model.CloudArtifact;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.model.VfModuleModel;
import org.onap.sdc.api.notification.IArtifactInfo;


/**
 * Forwards policies to the XACML PDP.
 */
public class K8sArtifactForwarder implements ArtifactForwarder {

    private static final Logger LOGGER = FlexLogger.getLogger(K8sArtifactForwarder.class);
    private static final String BASE_PATH = "http://localhost:9015/v1/rb/definition";
    private static final String CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT = "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT";
    private Map<String, IArtifactInfo> artifactMap;

    private K8sArtifactForwarderParameterGroup configurationParameters = null;
    


    @Override
    public void forward(PolicyInput  policyInput) {
        if (policyInput instanceof CloudArtifact) {
            System.out.println("get a CloudArtifact !");  
            CloudArtifact cloudArtifact = (CloudArtifact) policyInput;
            artifactMap = cloudArtifact.getArtifactTypeMap();
            System.out.println("the artifactMap = " + artifactMap);  
            ArrayList<VfModuleModel> vfModuleModels = cloudArtifact.getVfModulePayload();
            System.out.println("the size of vfModule = " + vfModuleModels.size());  
                   
            for (VfModuleModel vfModule : vfModuleModels) {
                forwardAndUpload(vfModule);
            }
        } else {
            System.out.println("NOT a CloudArtifact type !");
            return;
        }
    }

    private void forwardAndUpload(VfModuleModel vfModule) {
        
        System.out.println("before create type !");
        boolean definitionCreated = createDefinition(vfModule);
        System.out.println(" after create type !");
        if (definitionCreated) {
            uploadArtifact(vfModule);
        }
    }

    private boolean createDefinition(VfModuleModel vfModule) {
        try {
            HttpPost httpPost = new HttpPost(BASE_PATH);
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");

            Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .create();

            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("rb-name", vfModule.getVfModuleModelName());
            map.put("rb-version", vfModule.getVfModuleModelVersion());
            map.put("descritpion",vfModule.getVfModuleModelDescription());
            Map<String, String> labelMap = new LinkedHashMap<String, String>();
            labelMap.put("vnf_customization_uuid",vfModule.getVfModuleModelCustomizationUUID());
            map.put("labels", labelMap);
            String json = gson.toJson(map);

            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            return invokeHttpPost("definition", httpPost);
        } catch (Exception e) {
            System.out.println("create definition error");
            return false;
        }

    }

    private boolean uploadArtifact(VfModuleModel vfModule) {
        String url = BASE_PATH + "/" + vfModule.getVfModuleModelName() + "/" 
            + vfModule.getVfModuleModelVersion() + "/content"; 
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8");

        List<String> artifacts = vfModule.getArtifacts();
        System.out.println("artifacts = " + artifacts);

        String cloudUuid = null;
        IArtifactInfo cloudArtifact = null;
        boolean found = false;

        for (String artifact: artifacts) {
            if ( artifactMap.get(artifact) != null 
                && artifactMap.get(artifact).getArtifactType().equals("CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT")) {
                cloudArtifact = artifactMap.get(artifact);
                cloudUuid = cloudArtifact.getArtifactUUID();
                found = true;
                break;
            }
        }
        
        if ( found == false ) {
            System.out.println(" meets error , no CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT type found ");
            return false;
        }
        String cloudArtifactPath = "/data/" + vfModule.getVfModuleModelCustomizationUUID() 
            + "/" + cloudArtifact.getArtifactName();
        File file = new File(cloudArtifactPath);
        FileEntity entity = new FileEntity(file);
        httpPost.setEntity(entity);
        
        return invokeHttpPost("uploading", httpPost);
    }


    @Override
    public void configure(String parameterGroupName) {
        configurationParameters = ParameterService.get(parameterGroupName);
    }

    protected static boolean invokeHttpPost(String action, HttpPost httpPost)  {
        System.out.println("httpPost begin!");
        boolean ret = false;

        String errorMsg;
        label1: {
            try ( CloseableHttpClient httpClient = HttpClients.createDefault() ) {
                System.out.println("result1") ;
                CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpPost);
                System.out.println("result2") ;
                String result = EntityUtils.toString(closeableHttpResponse.getEntity());
                System.out.println("result = {}" + result);
                System.out.println("status  = {}" + closeableHttpResponse.getStatusLine().getStatusCode());
                int status = closeableHttpResponse.getStatusLine().getStatusCode();
                // [200, 300] means pass
                if ( (status >=200) && (status <= 300) ) {
                    ret = true;
                } else {
                    System.out.println("exception: ret= " + status);
                }

                closeableHttpResponse.close();
                break label1;
            } catch (IOException var) {
                errorMsg = action + ":httpPostWithJSON connect faild";
                System.out.println("exception: POST_CONNECT_FAILD : {}" + errorMsg);
            }
        }

        System.out.println("httpPost end!");
        return ret;
    }

}
