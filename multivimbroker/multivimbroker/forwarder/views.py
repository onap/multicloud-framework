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
import logging
import re

from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt

from rest_framework import status

from multivimbroker.pub.utils.restcall import req_by_msb
from multivimbroker.pub.msapi.extsys import get_vim_by_id

logger = logging.getLogger(__name__)


@csrf_exempt
def route(request, vimid=''):
    """ get vim info from vimid from local cache first
        and then to ESR if cache miss
    """
    content = ""
    status_code = status.HTTP_200_OK
    try:
        vim = get_vim_by_id(vimid)

        # if vim type is openstack, use latest "newton" version as default
        if vim["type"] == "openstack":
            vim["type"] = "multivim-newton"

        route_uri = re.sub('multivim', vim["type"], request.get_full_path())
        retcode, content, status_code = \
            req_by_msb(route_uri, request.method, request.body)
        if retcode != 0:
            # Execptions are handled within req_by_msb
            logger.error("Status code is %s, detail is %s.",
                         status_code, content)
    except Exception as e:
        content = e
        status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
    return HttpResponse(content, status_code)
