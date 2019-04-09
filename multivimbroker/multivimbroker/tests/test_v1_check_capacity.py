# Copyright (c) 2019 Wind River Systems, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import mock
import unittest

from rest_framework import status

from multivimbroker.forwarder.views import APIv1CheckCapacity


class CheckCapacityTest(unittest.TestCase):

    def setUp(self):
        self.view = APIv1CheckCapacity()
        super(CheckCapacityTest, self).setUp()

    def tearDown(self):
        pass

    def test_check_capacity_success(self):
        req = mock.Mock()
        req.body = """
        {
            "vCPU": 1,
            "Memory": 1,
            "Storage": 500,
            "VIMs": [{"cloud-owner": "openstack",
             "cloud-region-id": "RegionOne"}]
        }"""
        req.get_full_path.return_value = ("http://msb.onap.org/api/multicloud"
                                          "/v1/check_vim_capacity")
        req.META.items.return_value = [("Project", "projectname1")]
        with mock.patch.object(self.view, "send") as send:
            plugin_resp = mock.Mock()
            plugin_resp.content = """{
                "result": true
            }"""
            plugin_resp.status_code = str(status.HTTP_200_OK)
            send.return_value = plugin_resp

            resp = self.view.post(req)
            expect_body = {
                "VIMs": [{"cloud-owner":"openstack",
                          "cloud-region-id":"RegionOne",
                          "AZs": []}]
            }
            self.assertEqual(status.HTTP_200_OK, resp.status_code)
            self.assertDictEqual(expect_body, resp.data)

    def test_check_capacity_no_suitable_vim(self):
        req = mock.Mock()
        req.body = """
        {
            "vCPU": 1,
            "Memory": 1,
            "Storage": 500,
            "VIMs": [{"cloud-owner": "openstack",
             "cloud-region-id": "RegionOne"}]
        }"""
        req.get_full_path.return_value = ("http://msb.onap.org/api/multicloud"
                                          "/v1/check_vim_capacity")
        req.META.items.return_value = [("Project", "projectname1")]

        with mock.patch.object(self.view, "send") as send:
            plugin_resp = mock.Mock()
            plugin_resp.content = """{
                "result": false
            }"""
            plugin_resp.status_code = str(status.HTTP_200_OK)
            send.return_value = plugin_resp

            resp = self.view.post(req)
            expect_body = {
                "VIMs": []
            }
            self.assertEqual(status.HTTP_200_OK, resp.status_code)
            self.assertDictEqual(expect_body, resp.data)

    def test_check_capacity_invalid_input(self):
        req = mock.Mock()
        req.body = "hello world"
        req.get_full_path.return_value = ("http://msb.onap.org/api/multicloud"
                                          "/v1/check_vim_capacity")
        req.META.items.return_value = [("Project", "projectname1")]
        expect_body = {
            "error": ("Invalidate request body "
                      "No JSON object could be decoded.")
        }
        resp = self.view.post(req)
        self.assertEqual(status.HTTP_400_BAD_REQUEST, resp.status_code)
        self.assertDictEqual(expect_body, resp.data)
