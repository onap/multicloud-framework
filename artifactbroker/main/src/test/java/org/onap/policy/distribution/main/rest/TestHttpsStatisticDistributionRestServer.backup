/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.main.rest;

import static org.junit.Assert.assertEquals;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.Main;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
public class TestHttpsStatisticDistributionRestServer {

    private static final Logger LOGGER = FlexLogger.getLogger(TestHttpsStatisticDistributionRestServer.class);
    private static String KEYSTORE = System.getProperty("user.dir") + "/src/test/resources/ssl/policy-keystore";

    @Test
    public void testDistributionStatistic()
        throws PolicyDistributionException, InterruptedException, KeyManagementException, NoSuchAlgorithmException {
        final String reportString = "StatisticsReport [code=200, totalDistributionCount=0, distributionSuccessCount=0, "
            + "distributionFailureCount=0, totalDownloadCount=0, " + "downloadSuccessCount=0, downloadFailureCount=0]";
        final Main main = startDistributionService();
        final StatisticsReport report = performStatisticCheck();
        validateReport(200, 0, 0, 0, 0, 0, 0, reportString, report);
        stopDistributionService(main);
    }

    private Main startDistributionService() {
        Properties systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.keyStore", KEYSTORE);
        systemProps.put("javax.net.ssl.keyStorePassword", "Pol1cy_0nap");
        System.setProperties(systemProps);

        final String[] distributionConfigParameters = { "-c", "parameters/DistributionConfigParameters_Https.json" };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private StatisticsReport performStatisticCheck()
        throws InterruptedException, KeyManagementException, NoSuchAlgorithmException {
        StatisticsReport response = null;

        TrustManager[] noopTrustManager = new TrustManager[] { new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, noopTrustManager, new SecureRandom());
        final ClientBuilder clientBuilder = ClientBuilder.newBuilder().sslContext(sc)
            .hostnameVerifier((host, session) -> true);
        final Client client = clientBuilder.build();
        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        client.register(feature);

        final WebTarget webTarget = client.target("https://localhost:6969/statistics");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        while (response == null) {
            try {
                response = invocationBuilder.get(StatisticsReport.class);
            } catch (final Exception exp) {
                LOGGER.error("the server is not started yet. We will retry again", exp);
            }
        }
        return response;
    }

    private void validateReport(final int code, final int total, final int successCount, final int failureCount,
        final int download, final int downloadSuccess, final int downloadFailure, final String reportString,
        final StatisticsReport report) {
        assertEquals(code, report.getCode());
        assertEquals(total, report.getTotalDistributionCount());
        assertEquals(successCount, report.getDistributionSuccessCount());
        assertEquals(failureCount, report.getDistributionFailureCount());
        assertEquals(download, report.getTotalDownloadCount());
        assertEquals(downloadSuccess, report.getDownloadSuccessCount());
        assertEquals(downloadFailure, report.getDownloadFailureCount());
        assertEquals(reportString, report.toString());
    }
}
