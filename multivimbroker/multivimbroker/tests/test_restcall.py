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

from multivimbroker.pub.utils import restcall


class TestRestCall(unittest.TestCase):

    def test_combine_url(self):
        url = ["http://a.com/test/", "http://a.com/test/",
               "http://a.com/test"]
        res = ["/resource", "resource", "/resource"]
        expected = "http://a.com/test/resource"
        for i in range(len(url)):
            self.assertEqual(expected, restcall.combine_url(url[i], res[i]))

    @mock.patch.object(restcall, "call_req")
    def test_get_res_from_aai(self, mock_call):
        res = "cloud-regions"
        content = ""
        expect_url = "https://aai.api.simpledemo.openecomp.org:8443/aai/v13"
        expect_user = "AAI"
        expect_pass = "AAI"
        expect_headers = {
            'X-FromAppId': 'MultiCloud',
            'X-TransactionId': '9001',
            'content-type': 'application/json',
            'accept': 'application/json'
        }
        restcall.get_res_from_aai(res, content=content)
        mock_call.assert_called_once_with(
            expect_url, expect_user, expect_pass, restcall.rest_no_auth,
            res, "GET", content, expect_headers)
