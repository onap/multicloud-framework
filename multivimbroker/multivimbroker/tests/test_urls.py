# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import json
import mock
import unittest

from multivimbroker.pub.config import config
from multivimbroker import urls


class TestUrls(unittest.TestCase):

    def test_request_msb(self):
        with mock.patch("multivimbroker.pub.utils.restcall."
                        "req_by_msb") as req_by_msb:
            urls.req_msb(True)
            req_by_msb.assert_called_once_with(
                config.REG_TO_MSB_REG_URL, "POST",
                json.JSONEncoder().encode(config.REG_TO_MSB_REG_PARAM))
