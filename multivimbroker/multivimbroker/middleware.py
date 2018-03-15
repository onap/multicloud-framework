# Copyright (c) 2017-2018 VMware, Inc.
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


import uuid
from pub.config.config import SERVICE_NAME
from onaplogging.mdcContext import MDC


class LogContextMiddleware(object):

    def process_request(self, request):
        # fetch propageted Id from other component. if do not fetch id,
        # generate one.
        ReqeustID = request.META.get("HTTP_X_TRANSACTIONID", None)
        if ReqeustID is None:
            ReqeustID = uuid.uuid3(uuid.NAMESPACE_URL, SERVICE_NAME)
        MDC.put("requestID", ReqeustID)
        # generate the reqeust id
        InovocationID = uuid.uuid3(uuid.NAMESPACE_DNS, SERVICE_NAME)
        MDC.put("invocationID", InovocationID)
        MDC.put("serviceName", SERVICE_NAME)
        # behind multiple proxies
        ip = request.META.get("HTTP_X_FORWARDED_HOST", "")
        if ip == "":
            ip = request.META.get("HTTP_HOST", "")
        MDC.put("serviceIP", ip.split(":")[0])
        return None

    def process_response(self, request, response):

        MDC.clear()
        return response
