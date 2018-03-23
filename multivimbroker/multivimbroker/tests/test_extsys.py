# Copyright (c) 2017-2018 VMware, Inc.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import mock
import unittest

from multivimbroker.pub.msapi import extsys
from multivimbroker.pub.utils import restcall


class TestExtsys(unittest.TestCase):

    def test_split_vim_to_owner_region(self):
        vim_id = "openstack_regionone"
        cloud_owner, cloud_region = extsys.split_vim_to_owner_region(vim_id)
        self.assertEqual("openstack", cloud_owner)
        self.assertEqual("regionone", cloud_region)

    @mock.patch.object(restcall, "get_res_from_aai")
    def test_get_vim_by_id_success(self, mock_get_res):
        resp_body = """{
            "cloud-type": "openstack",
            "cloud-region-version": "regionone"
        }"""
        mock_get_res.return_value = (0, resp_body, 200, mock.Mock())
        vim_id = "openstack_regionone"
        ret = extsys.get_vim_by_id(vim_id)
        expect_ret = {
            "cloud-type": "openstack",
            "cloud-region-version": "regionone",
            "type": "openstack",
            "version": "regionone",
            "vimId": vim_id
        }
        self.assertDictEqual(expect_ret, ret)
