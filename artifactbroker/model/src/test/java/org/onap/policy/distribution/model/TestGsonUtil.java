/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel. All rights reserved.
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

package org.onap.policy.distribution.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.junit.Test;

/**
 * Class to perform unit testing of all GsonUtil models.
 *
 * @author libo zhu (libo.zhu@intel.com)
 */
public class TestGsonUtil {

    @Test
    public void testGsonUtil() {
        try {
            // Read the parameters from JSON using Gson

            String data = new String(Files.readAllBytes(Paths.get("src/test/resource/modules.json")));
            List<VfModuleModel> vfModuleModels= GsonUtil.parseJsonArrayWithGson(data, VfModuleModel.class);
            assertEquals(4, vfModuleModels.size());
        } catch (final Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }


    }
}
