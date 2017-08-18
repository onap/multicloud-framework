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

import inspect
import re

import multivimbroker.pub.exceptions as  exceptions
from multivimbroker.pub.msapi.extsys import get_vim_by_id
def fun_name():
    return inspect.stack()[1][3]




# Which headers are hop-by-hop headers by default
HOP_BY_HOP = ['connection', 'keep-alive', 'proxy-authenticate', 'proxy-authorization', 'te', 'trailers', 'transfer-encoding', 'upgrade']
def get_headers(response):
    hopbyhop = list(HOP_BY_HOP)
    hopbyhop.extend([x.strip() for x in response.get('connection', '').split(',')])
    return [header for header in response.keys() if header not in hopbyhop]



def findMultivimDriver(vim=None):

    if vim and vim["type"] == "openstack":
        if vim["version"] == "kilo":
            multivimdriver = "multivim-kilo"
        elif vim["version"] == "newton":
            multivimdriver = "multivim-newton"
        else:
            # if vim type is openstack, use latest "newton" version as default
            multivimdriver = "multivim-newton"
    elif vim and vim["type"] == "vmware":
        multivimdriver = "multivim-vio"
    else:
        raise  exceptions.NotFound("Not support VIM type")
    return  multivimdriver



def  getMultivimDriver(vimid,full_path=""):

    multivim = "multivim"
    vim = get_vim_by_id(vimid)
    if vim["type"] and vim["version"]:
        pass

    multivimdriver = findMultivimDriver(vim=vim)
    return re.sub(multivim, multivimdriver, full_path)
