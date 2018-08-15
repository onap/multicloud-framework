# Copyright 2017 Wind River Systems, Inc.
# Copyright (c) 2017-2018 VMware, Inc.
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
import re
import tempfile
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers

from rest_framework.views import APIView
from rest_framework.views import Response
from rest_framework.views import status
from multivimbroker.forwarder.base import BaseHandler
from multivimbroker.pub.utils.syscomm import originHeaders
from multivimbroker.pub.utils import syscomm
from rest_framework.parsers import MultiPartParser


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

        return self.send(vimid, request.get_full_path(), request.body, "GET",
                         headers=originHeaders(request))

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST",
                         headers=originHeaders(request))


class Registry(BaseServer):

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST",
                         headers=originHeaders(request))


class UnRegistry(BaseServer):

    def delete(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body,
                         "DELETE", headers=originHeaders(request))


class Extension(BaseServer):

    def get(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "GET",
                         headers=originHeaders(request))


class VIMTypes(BaseServer):

    def get(self, request):
        return Response(data=syscomm.getVIMTypes(), status=status.HTTP_200_OK)


class CheckCapacity(BaseServer):

    def post(self, request):
        try:
            body = json.loads(request.body)
        except ValueError as e:
            return Response(
                data={'error': 'Invalidate request body %s.' % e},
                status=status.HTTP_400_BAD_REQUEST)

        ret = {"VIMs": []}
        newbody = {
            "vCPU": body.get("vCPU", 0),
            "Memory": body.get("Memory", 0),
            "Storage": body.get("Storage", 0)
        }
        for vim in body.get("VIMs", []):
            url = request.get_full_path().replace(
                "check_vim_capacity", "%s/capacity_check" % vim)
            resp = self.send(vim, url, json.dumps(newbody), "POST")
            if int(resp.status_code) != status.HTTP_200_OK:
                continue
            try:
                resp_body = json.loads(resp.content)
            except ValueError:
                continue
            if not resp_body.get("result", False):
                continue
            ret['VIMs'].append(vim)
        return Response(data=ret, status=status.HTTP_200_OK)


# forward  handler
class Forward(BaseServer):

    def get(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "GET",
                         headers=originHeaders(request))

    def post(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "POST",
                         headers=originHeaders(request))

    def patch(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "PATCH",
                         headers=originHeaders(request))

    def delete(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body,
                         "DELETE", headers=originHeaders(request))

    def head(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "HEAD",
                         headers=originHeaders(request))

    def put(self, request, vimid):

        return self.send(vimid, request.get_full_path(), request.body, "PUT",
                         headers=originHeaders(request))


# Multipart view
class MultiPartView(BaseServer):

    parser_classes = (MultiPartParser, )

    def post(self, request, vimid):
        try:
            register_openers()
            fileDict = dict(request.FILES.iterlists())
            params = {}
            for key in fileDict.keys():
                fileObj = fileDict[key][0]
                f = tempfile.NamedTemporaryFile(prefix="django_",
                                                suffix=fileObj._name,
                                                delete=False)
                f.write(fileObj.file.read())
                f.seek(fileObj.file.tell(), 0)
                fileObj.file.close()
                params[key] = open(f.name, 'rb')
            datagen, headers = multipart_encode(params)
            regex = re.compile('^HTTP_')
            for key, value in request.META.iteritems():
                if key.startswith("HTTP_"):
                    headers[regex.sub('', key).replace('_', '-')] = value
            resp = self.send(vimid, request.path, datagen, "POST",
                             headers=headers)
        finally:
            for key in params:
                fileRef = params[key]
                if fileRef.closed is False:
                    fileRef.close()
                os.remove(fileRef.name)
        return resp



#API v1

from common.msapi import extsys

# proxy handler
class APIv1Identity(Identity):

    def get(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Identity,self).get(request, vimid)

    def post(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Identity,self).post(request, vimid)


class APIv1Registry(Registry):

    def post(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Registry,self).post(request, vimid)


class APIv1UnRegistry(UnRegistry):

    def delete(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1UnRegistry,self).delete(request, vimid)


class APIv1Extension(Extension):

    def get(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Extension,self).get(request, vimid)


class APIv1VIMTypes(VIMTypes):

    def get(self, request):
        return super(APIv1VIMTypes,self).get(request)


class APIv1CheckCapacity(CheckCapacity):

    def post(self, request):
        try:
            body = json.loads(request.body)
        except ValueError as e:
            return Response(
                data={'error': 'Invalidate request body %s.' % e},
                status=status.HTTP_400_BAD_REQUEST)

        ret = {"VIMs": []}
        newbody = {
            "vCPU": body.get("vCPU", 0),
            "Memory": body.get("Memory", 0),
            "Storage": body.get("Storage", 0)
        }
        for vim in body.get("VIMs", []):
            cloud_owner = vim["cloud-owner"]
            cloud_region_id = vim["cloud-region-id"]
            url = request.get_full_path().replace(
                "check_vim_capacity", "%s/%s/capacity_check" % (cloud_owner, cloud_region_id))
            vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
            resp = self.send(vimid, url, json.dumps(newbody), "POST")
            if int(resp.status_code) != status.HTTP_200_OK:
                continue
            try:
                resp_body = json.loads(resp.content)
            except ValueError:
                continue
            if not resp_body.get("result", False):
                continue
            ret['VIMs'].append(vim)
        return Response(data=ret, status=status.HTTP_200_OK)


# forward  handler
class APIv1Forward(Forward):

    def get(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).get(request, vimid)

    def post(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).post(request, vimid)

    def patch(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).patch(request, vimid)

    def delete(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).delete(request, vimid)

    def head(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).head(request, vimid)

    def put(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1Forward,self).put(request, vimid)



# Multipart view
class APIv1MultiPartView(MultiPartView):

    def post(self, request, cloud_owner, cloud_region_id):
        vimid = extsys.encode_vim_id(cloud_owner, cloud_region_id)
        return super(APIv1MultiPartView,self).post(request, vimid)
