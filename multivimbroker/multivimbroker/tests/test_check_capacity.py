# Copyright (c) 2017-2018 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import mock
import unittest

from rest_framework import status

from multivimbroker.forwarder.views import CheckCapacity


class CheckCapacityTest(unittest.TestCase):

    def setUp(self):
        self.view = CheckCapacity()
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
            "VIMs": ["openstack_RegionOne"]
        }"""
        req.get_full_path.return_value = ("http://msb.onap.org/api/multicloud"
                                          "/v0/check_vim_capacity")
        with mock.patch.object(self.view, "send") as send:
            plugin_resp = mock.Mock()
            plugin_resp.body = """{
                "result": true
            }"""
            plugin_resp.status_code = status.HTTP_200_OK
            send.return_value = plugin_resp

            resp = self.view.post(req)
            expect_body = {
                "VIMs": ["openstack_RegionOne"]
            }
            self.assertEqual(status.HTTP_200_OK, resp.status_code)
            self.assertDictEqual(expect_body, resp.data)
