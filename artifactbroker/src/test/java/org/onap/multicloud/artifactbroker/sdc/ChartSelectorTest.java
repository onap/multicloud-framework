/*
 * ============LICENSE_START=======================================================
 * Copyright (C) 2026 Deutsche Telekom AG
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
 * ============LICENSE_END=========================================================
 */

package org.onap.multicloud.artifactbroker.sdc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.onap.multicloud.artifactbroker.sdc.TestArtifacts.artifact;
import static org.onap.multicloud.artifactbroker.sdc.TestArtifacts.byUuid;
import static org.onap.multicloud.artifactbroker.sdc.TestArtifacts.module;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.multicloud.artifactbroker.ArtifactProcessingException;
import org.onap.sdc.api.notification.IArtifactInfo;

class ChartSelectorTest {

    private static final String MODULE_NAME = "vfw_k8s_demo_cnf..base_template..module-0";

    @Test
    void helmArtifactWinsOverCloudArtifacts() throws Exception {
        IArtifactInfo cloud = artifact("cloud", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "base_template_cloudtech.tgz");
        IArtifactInfo helm = artifact("helm", "HELM", "helm_base_template.tgz");

        assertThat(ChartSelector.select(module(MODULE_NAME, "cloud", "helm"), byUuid(cloud, helm))).containsSame(helm);
    }

    @Test
    void singleCloudArtifactIsChosenEvenIfItsNameDoesNotMatch() throws Exception {
        IArtifactInfo cloud = artifact("cloud", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "other_chart.tgz");

        assertThat(ChartSelector.select(module(MODULE_NAME, "cloud"), byUuid(cloud))).containsSame(cloud);
    }

    @Test
    void cloudArtifactMatchingTheModuleNameIsChosen() throws Exception {
        IArtifactInfo other = artifact("other", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "vpkg_cloudtech.tgz");
        IArtifactInfo matching = artifact("match", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "Base_Template_cloudtech.tgz");

        assertThat(ChartSelector.select(module(MODULE_NAME, "other", "match"), byUuid(other, matching)))
                .containsSame(matching);
    }

    @Test
    void firstCloudArtifactIsChosenWhenTheModuleNameHasNoPrefix() throws Exception {
        IArtifactInfo first = artifact("first", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "a.tgz");
        IArtifactInfo second = artifact("second", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "b.tgz");

        assertThat(ChartSelector.select(module("plain-name", "first", "second"), byUuid(first, second)))
                .containsSame(first);
    }

    @Test
    void severalCloudArtifactsWithoutAMatchFail() {
        IArtifactInfo first = artifact("first", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "a.tgz");
        IArtifactInfo second = artifact("second", "CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT", "b.tgz");

        assertThatThrownBy(() -> ChartSelector.select(module(MODULE_NAME, "first", "second"), byUuid(first, second)))
                .isInstanceOf(ArtifactProcessingException.class)
                .hasMessage("no CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT artifact matches base_template_");
    }

    @Test
    void moduleWithoutChartHasNone() throws Exception {
        IArtifactInfo heat = artifact("heat", "HEAT", "base_template_dummy_ignore.yaml");

        assertThat(ChartSelector.select(module("vf..base_template_dummy_ignore..module-0", "heat"), byUuid(heat)))
                .isEmpty();
    }

    @Test
    void artifactUuidsMissingFromTheNotificationAreIgnored() throws Exception {
        IArtifactInfo helm = artifact("helm", "HELM", "helm_base_template.tgz");

        assertThat(ChartSelector.select(module(MODULE_NAME, "filtered-out", "helm"), byUuid(helm))).containsSame(helm);
    }

    @Test
    void moduleWithoutArtifactListHasNoChart() throws Exception {
        VfModuleModel module = new VfModuleModel(MODULE_NAME, "i", "u", "c", "d", null);

        assertThat(ChartSelector.select(module, Map.of())).isEmpty();
        assertThat(module.artifacts()).isEqualTo(List.of());
    }
}
