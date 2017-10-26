# Copyright (c) 2017 VMware, Inc.
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

import logging

from django.http import HttpResponse
from rest_framework import status

import multivimbroker.pub.exceptions as exceptions
from multivimbroker.pub.utils.syscomm import getHeadersKeys
from multivimbroker.pub.utils.syscomm import getMultivimDriver
from multivimbroker.pub.utils.restcall import req_by_msb


logger = logging.getLogger(__name__)


class BaseHandler(object):

    HEADER_MAPPING = {
        'HTTP_X_AUTH_TOKEN': 'X-Auth-Token',
        'HTTP_X_SUBJECT_TOKEN': 'X-Subject-Token',
        'CONTENT_TYPE': 'Content-Type',
        'HTTP_ACCEPT': 'Accept',
        'HTTP_USER_AGENT': 'User-Agent',
        'HTTP_ACCEPT_LANGUAGE': 'Accept-Language',
        'HTTP_ACCEPT_ENCODING': 'Accept-Encoding'
    }

    def _request(self, route_uri, method, body="", headers=None):

        try:
            retcode, content, status_code, resp = \
                req_by_msb(route_uri, method, body, headers)
            if retcode != 0:
                # Execptions are handled within req_by_msb
                logger.error("Status code is %s, detail is %s.",
                             status_code, content)

        except exceptions.NotFound as e:
            return HttpResponse(str(e), status=status.HTTP_404_NOT_FOUND)

        except Exception as e:
            content = e
            status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
            logger.exception("exception: %s" % e)

        response = HttpResponse(content, status=status_code)
        for k in getHeadersKeys(resp):
            response[k] = resp[k]
        return response

    def send(self, vimid, full_path, body, method, headers=None):
        new_headers = None
        if headers:
            new_headers = {}
            for k,v in self.HEADER_MAPPING.items():
                if k in headers:
                    new_headers[v] = headers[k]

        try:
            url = getMultivimDriver(vimid, full_path=full_path)
        except exceptions.VimBrokerException as e:
            logging.exception("vimbroker exception: %s" % e)
            return HttpResponse(e.content, status=e.status_code)
        except Exception as e:
            logging.exception("unkown exception: %s" % e)
            return HttpResponse(str(e),
                                status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return self._request(url, method, body=body, headers=new_headers)
