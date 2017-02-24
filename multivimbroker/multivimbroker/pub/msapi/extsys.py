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

import json
import logging

from multivimbroker.pub.exceptions import VimBrokerException
from multivimbroker.pub.utils.restcall import req_by_msb
from multivimbroker.pub.config.config import ESR_GET_VIM_URI

logger = logging.getLogger(__name__)


def get_vims():
    ret = req_by_msb(ESR_GET_VIM_URI, "GET")
    if ret[0] != 0:
        logger.error("Status code is %s, detail is %s.", ret[2], ret[1])
        raise VimBrokerException("Failed to query VIMs from extsys.")
    return json.JSONDecoder().decode(ret[1])


def get_vim_by_id(vim_id):
    ret = req_by_msb("%s/%s" % (ESR_GET_VIM_URI, vim_id), "GET")
    if ret[0] != 0:
        logger.error("Status code is %s, detail is %s.", ret[2], ret[1])
        raise VimBrokerException(
            "Failed to query VIM with id (%s) from extsys." % vim_id)
    return json.JSONDecoder().decode(ret[1])
