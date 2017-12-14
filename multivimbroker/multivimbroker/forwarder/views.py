# Copyright 2017 Wind River Systems, Inc.
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

import os
import json

from rest_framework.views import APIView
from rest_framework.views import Response
from rest_framework.views import status
from multivimbroker.forwarder.base import BaseHandler

#


class BaseServer(BaseHandler, APIView):

    def get(self, request, vimid):
        raise NotImplementedError()

    def post(self, request, vimid):
        raise NotImplementedError()

    def put(self, request, vimid):
        raise NotImplementedError()

    def delete(self, request, vimid):
        raise NotImplementedError()

    def head(self, request, vimid):
        raise NotImplementedError()

    def patch(self, request, vimid):
        raise NotImplementedError()


# proxy handler
class Identity(BaseServer):

    def get(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "GET")

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST")


class Registry(BaseServer):

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST")


class UnRegistry(BaseServer):

    def delete(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body,
                         "DELETE")


class Extension(BaseServer):

    def get(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "GET")


class VIMTypes(BaseServer):

    def get(self, request):
        # Fix here unless we have plugin registry
        json_file = os.path.join(os.path.dirname(__file__),
                                 '../pub/config/provider-plugin.json')
        with open(json_file, "r") as f:
            plugins = json.load(f)
        data = {"vim_types": plugins}
        return Response(data=data, status=status.HTTP_200_OK)


# forward  handler
class Forward(BaseServer):

    def get(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "GET")

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST",
                         headers=None)

    def patch(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "PATCH",
                         headers=None)

    def delete(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body,
                         "DELETE", headers=None)

    def head(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "HEAD")

    def put(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "PUT",
                         headers=None)
