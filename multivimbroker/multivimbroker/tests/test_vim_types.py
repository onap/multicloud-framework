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

from rest_framework.views import status

from multivimbroker.forwarder.views import VIMTypes


class TestUrls(unittest.TestCase):
    def setUp(self):
        self.view = VIMTypes()

    def test_vim_types_success(self):
        resp = self.view.get(mock.Mock())
        self.assertEqual(status.HTTP_200_OK, resp.status_code)
        self.assertEqual(3, len(resp.data))
