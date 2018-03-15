# Copyright (c) 2017 Wind River Systems, Inc.
# Copyright (c) 2017-2018 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import inspect
import json
import os
import re

import multivimbroker.pub.exceptions as exceptions
from multivimbroker.pub.msapi.extsys import get_vim_by_id


def fun_name():
    return inspect.stack()[1][3]


# Which headers are hop-by-hop headers by default
HOP_BY_HOP = ['connection', 'keep-alive', 'proxy-authenticate',
              'proxy-authorization', 'te', 'trailers',
              'transfer-encoding', 'upgrade']


def getHeadersKeys(response):
    hopbyhop = HOP_BY_HOP
    hopbyhop.extend([x.strip()
                     for x in response.get('connection', '').split(',')])
    return [header for header in response.keys() if header not in hopbyhop]


# trim out 'HTTP_' prefix part and replace "_" wiht "-".
def originHeaders(request):
    regex = re.compile('^HTTP_')
    return dict((regex.sub('', header).replace("_", "-"), value)
                for (header, value) in request.META.items()
                if header.startswith('HTTP_'))


def findMultivimDriver(vim=None):
    json_file = os.path.join(os.path.dirname(__file__),
                             '../config/provider-plugin.json')
    with open(json_file, "r") as f:
        plugins = json.load(f)
    if not vim or vim.get("type") not in plugins.keys():
        raise exceptions.NotFound("Not support VIM type")
    plugin = plugins[vim["type"]]
    if vim.get("version") in plugin["versions"].keys():
        return plugin["versions"][vim["version"]]["provider_plugin"]
    return plugin["provider_plugin"]


def getMultivimDriver(vimid, full_path=""):
    multcloud = "multicloud"
    vim = get_vim_by_id(vimid)
    multclouddriver = findMultivimDriver(vim=vim)
    return re.sub(multcloud, multclouddriver, full_path)
