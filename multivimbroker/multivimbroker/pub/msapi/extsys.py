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

import json
import logging
import re

from multivimbroker.pub.exceptions import VimBrokerException
from multivimbroker.pub.utils import restcall

logger = logging.getLogger(__name__)


def encode_vim_id(cloud_owner, cloud_region_id):
    '''
    compose vim_id by cloud_owner and cloud_region, make sure the vimid can be
    converted back when talking to AAI,etc.
    This is a backward compatibility design to reuse the existing
    implementation code
    :param cloud_owner:
    :param cloud_region:
    :return:
    '''

    # since the {cloud_owner}/{cloud_region_id"} is globally unique, the
    # concatenated one as below will be unique as well.

    vim_id = cloud_owner + "_" + cloud_region_id

    # other options:
    # 1, store it into cache so the decode and just look up the cache for
    # decoding
    # 2, use other delimiter in case that '_' was used by
    # cloud owner/cloud region id,
    # e.g. '.', '#', hence the decode need to try more than one time

    return vim_id


def decode_vim_id(vim_id):
    m = re.search(r'^([0-9a-zA-Z-]+)_([0-9a-zA-Z_-]+)$', vim_id)
    cloud_owner, cloud_region_id = m.group(1), m.group(2)
    return cloud_owner, cloud_region_id

def split_vim_to_owner_region(vim_id):
    split_vim = vim_id.split('_')
    cloud_owner = split_vim[0]
    cloud_region = "".join(split_vim[1:])
    return cloud_owner, cloud_region


def get_vim_by_id(vim_id):
    if vim_id == "vmware_fake":
        return {
            "type": "vmware",
            "version": "4.0",
            "vimId": vim_id
        }
    cloud_owner, cloud_region = decode_vim_id(vim_id)
    ret = restcall.get_res_from_aai("/cloud-infrastructure/cloud-regions/"
                                    "cloud-region/%s/%s" % (
                                        cloud_owner, cloud_region))
    if ret[0] != 0:
        logger.error("Status code is %s, detail is %s." % (ret[2], ret[1]))
        raise VimBrokerException(
            status_code=404,
            content="Failed to query VIM with id (%s) from extsys." % vim_id)
    ret = json.JSONDecoder().decode(ret[1])
    ret['type'] = ret['cloud-type']
    ret['version'] = ret['cloud-region-version']
    ret['vimId'] = vim_id
    return ret
