#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.


import pecan
from pecan.rest import RestController

class TestsController(RestController):

    @pecan.expose('json')
    def get(self, test_id):
        return {'test': 123}

    @pecan.expose('json')
    def get_all(self):
        return [{'test': 123}]


class V0_Controller(RestController):
    tests = TestsController()


class MultiCloudController(RestController):
    v0 = V0_Controller()


class APIController(RestController):
    multicloud = MultiCloudController()


class RootController(object):
    api = APIController()
