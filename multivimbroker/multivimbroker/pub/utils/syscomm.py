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
from multivimbroker.pub.msapi import extsys


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
    headers = {}
    for key, value in request.META.items():
        if key.startswith('HTTP_') and key != 'HTTP_HOST':
            headers[key[5:].replace('_', '-')] = value
        elif key in ('CONTENT_TYPE', 'CONTENT_LENGTH'):
            headers[key.replace('_', '-')] = value
        elif key.lower() in ('project', 'project_id', 'project_name',
                             'tenant', 'tenant_id', 'tenant_name'):
            # support API to specify project other than the default one
            headers[key] = value
        # elif key.lower() in ('x-auth-token',
        #                      'http_x_auth_token', 'x_auth_token'):
        #     # pass the token to plugins
        #     headers["X-Auth-Token"] = value
    return headers


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
    vim = extsys.get_vim_by_id(vimid)
    multclouddriver = findMultivimDriver(vim=vim)
    return re.sub(multcloud, multclouddriver, full_path)


def getVIMTypes():
    # Fix here unless we have plugin registry
    json_file = os.path.join(os.path.dirname(__file__),
                             '../config/provider-plugin.json')
    with open(json_file, "r") as f:
        plugins = json.load(f)
    ret = []
    for k, v in plugins.items():
        item = {}
        item["vim_type"] = v.get("vim_type")
        item["versions"] = [k for k in v.get('versions', {})]
        ret.append(item)

    return ret
