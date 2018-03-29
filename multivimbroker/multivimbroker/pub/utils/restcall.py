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

import sys
import traceback
import logging
import urllib2
import uuid
import httplib2

from multivimbroker.pub.config.config import AAI_SCHEMA_VERSION
from multivimbroker.pub.config.config import AAI_SERVICE_URL
from multivimbroker.pub.config.config import AAI_USERNAME
from multivimbroker.pub.config.config import AAI_PASSWORD
from multivimbroker.pub.config.config import MSB_SERVICE_IP, MSB_SERVICE_PORT

rest_no_auth, rest_oneway_auth, rest_bothway_auth = 0, 1, 2
HTTP_200_OK, HTTP_201_CREATED = '200', '201'
HTTP_204_NO_CONTENT, HTTP_202_ACCEPTED = '204', '202'
status_ok_list = [HTTP_200_OK, HTTP_201_CREATED,
                  HTTP_204_NO_CONTENT, HTTP_202_ACCEPTED]
HTTP_404_NOTFOUND, HTTP_403_FORBIDDEN = '404', '403'
HTTP_401_UNAUTHORIZED, HTTP_400_BADREQUEST = '401', '400'

logger = logging.getLogger(__name__)


def call_multipart_req(base_url, user, passwd, auth_type, resource, method,
                       content, headers=None):
    callid = str(uuid.uuid1())
#    logger.debug("[%s]call_req('%s','%s','%s',%s,'%s','%s','%s')" % (
#    callid, base_url, user, passwd, auth_type, resource, method, content))
    ret = None
    resp = ""
    full_url = ""

    try:
        full_url = combine_url(base_url, resource)
        logger.debug("request=%s)" % full_url)
        requestObj = urllib2.Request(full_url, content,
                                     headers)
        resp = urllib2.urlopen(requestObj)
        if resp.code in status_ok_list:
            ret = [0, resp.read(), resp.code, resp]
        else:
            ret = [1, resp.read(), resp.code, resp]
    except urllib2.URLError as err:
        ret = [2, str(err), 500, resp]
    except Exception:
        logger.error(traceback.format_exc())
        logger.error("[%s]ret=%s" % (callid, str(sys.exc_info())))
        res_info = str(sys.exc_info())
        ret = [3, res_info, 500, resp]
    return ret


def call_req(base_url, user, passwd, auth_type, resource, method,
             content='', headers=None):
    callid = str(uuid.uuid1())
#    logger.debug("[%s]call_req('%s','%s','%s',%s,'%s','%s','%s')" % (
#    callid, base_url, user, passwd, auth_type, resource, method, content))
    ret = None
    resp_status = ''
    resp = ""
    full_url = ""

    try:
        full_url = combine_url(base_url, resource)
        if headers is None:
            headers = {}
            headers['content-type'] = 'application/json'

        if user:
            headers['Authorization'] = 'Basic ' + \
                ('%s:%s' % (user, passwd)).encode("base64")
        ca_certs = None
        for retry_times in range(3):
            http = httplib2.Http(
                ca_certs=ca_certs,
                disable_ssl_certificate_validation=(
                    auth_type == rest_no_auth))
            http.follow_all_redirects = True
            try:
                logger.debug("request=%s)" % full_url)
                resp, resp_content = http.request(
                    full_url, method=method.upper(),
                    body=content, headers=headers)
                resp_status, resp_body = resp['status'], resp_content.decode(
                    'UTF-8')

                if resp_status in status_ok_list:
                    ret = [0, resp_body, resp_status, resp]
                else:
                    ret = [1, resp_body, resp_status, resp]
                break
            except Exception as ex:
                if 'httplib.ResponseNotReady' in str(sys.exc_info()):
                    logger.error(traceback.format_exc())
                    ret = [1, "Unable to connect to %s" %
                           full_url, resp_status, resp]
                    continue
                raise ex
    except urllib2.URLError as err:
        ret = [2, str(err), resp_status, resp]
    except Exception:
        logger.error(traceback.format_exc())
        logger.error("[%s]ret=%s" % (callid, str(sys.exc_info())))
        res_info = str(sys.exc_info())
        if 'httplib.ResponseNotReady' in res_info:
            res_info = "The URL[%s] request \
            failed or is not responding." % full_url
        ret = [3, res_info, resp_status, resp]

#    logger.debug("[%s]ret=%s" % (callid, str(ret)))
    return ret


def req_by_msb(resource, method, content='', headers=None):
    base_url = "http://%s:%s/" % (MSB_SERVICE_IP, MSB_SERVICE_PORT)
    return call_req(base_url, "", "",
                    rest_no_auth, resource, method, content, headers)


def req_by_msb_multipart(resource, method, content, headers=None):
    base_url = "http://%s:%s/" % (MSB_SERVICE_IP, MSB_SERVICE_PORT)
    return call_multipart_req(base_url, "", "",
                              rest_no_auth, resource, method, content, headers)


def get_res_from_aai(resource, content=''):
    headers = {
        'X-FromAppId': 'MultiCloud',
        'X-TransactionId': '9001',
        'content-type': 'application/json',
        'accept': 'application/json'
    }
    base_url = "%s/%s" % (AAI_SERVICE_URL, AAI_SCHEMA_VERSION)
    return call_req(base_url, AAI_USERNAME, AAI_PASSWORD, rest_no_auth,
                    resource, "GET", content, headers)


def combine_url(base_url, resource):
    full_url = None
    if base_url.endswith('/') and resource.startswith('/'):
        full_url = base_url[:-1] + resource
    elif base_url.endswith('/') and not resource.startswith('/'):
        full_url = base_url + resource
    elif not base_url.endswith('/') and resource.startswith('/'):
        full_url = base_url + resource
    else:
        full_url = base_url + '/' + resource
    return full_url
