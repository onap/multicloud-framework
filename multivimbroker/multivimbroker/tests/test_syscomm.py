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

from multivimbroker.pub import exceptions
from multivimbroker.pub.msapi import extsys
from multivimbroker.pub.utils import syscomm


class TestSyscomm(unittest.TestCase):

    def test_getVIMTypes(self):
        expected_body = {
            "openstack": ["titanium_cloud", "ocata"],
            "vmware": ["4.0"],
        }
        ret = syscomm.getVIMTypes()
        for item in ret:
            for v in item['versions']:
                self.assertIn(v, expected_body[item['vim_type']])

    @mock.patch.object(extsys, "get_vim_by_id")
    def test_getMultivimDriver(self, mock_get_vim):
        mock_get_vim.return_value = {
            "type": "openstack",
            "version": "ocata"
        }
        full_path = "multicloud/v0/openstack_regionone/identity"
        expect_path = "multicloud-ocata/v0/openstack_regionone/identity"
        ret_path = syscomm.getMultivimDriver("openstack_regionone", full_path)
        self.assertEqual(expect_path, ret_path)

    def test_findMultivimDriver_no_type(self):
        vim = {"type": "wrong type"}
        self.assertRaises(exceptions.NotFound, syscomm.findMultivimDriver, vim)
