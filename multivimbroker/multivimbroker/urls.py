# Copyright (c) 2017 Wind River Systems, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

from django.conf.urls import include, url
import json

from multivimbroker.pub.config import config


urlpatterns = [
    url(r'^', include('multivimbroker.swagger.urls')),
    url(r'^', include('multivimbroker.forwarder.urls')),
]


def req_msb(request_when_start):
    # regist to MSB when startup
    if request_when_start:
        from multivimbroker.pub.utils.restcall import req_by_msb
        req_by_msb(config.REG_TO_MSB_REG_URL, "POST",
                   json.JSONEncoder().encode(config.REG_TO_MSB_REG_PARAM))


req_msb(config.REG_TO_MSB_WHEN_START)
