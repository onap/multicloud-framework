# Copyright (c) 2017-2018 VMware, Inc.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

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
