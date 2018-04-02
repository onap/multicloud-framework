#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

import logging
import pecan

from multivimbroker.pub import exceptions
from multivimbroker.pub.utils import restcall
from multivimbroker.pub.utils import syscomm
from multivimbroker.swagger import utils


logger = logging.getLogger(__name__)

# TODO: Move to a constant file.
REGISTRY_URI = "registry"
UNREGISTRY_URI = ""
IDENTITY_URI = "identity/v3"
IDENTITY_AUTH_URI = "identity/v3/auth/tokens"


class V0_Controller(object):

    @pecan.expose('json')
    def vim_types(self):
        return syscomm.getVIMTypes()

    @pecan.expose('json', route="swagger.json")
    def swagger_json(self):
        return utils.get_swagger_json_data()

    def _filter_illegal_uri(self, uri, method):
        """
        Filter unsupported actions, so they can be stopped at begginning.
        """

        if uri == REGISTRY_URI and method != "POST":
            pecan.abort(405)

        if uri == UNREGISTRY_URI and method != "DELETE":
            pecan.abort(405)

        if (uri in (IDENTITY_URI, IDENTITY_AUTH_URI) and
                method not in ("POST", "GET")):
            pecan.abort(405)

    @pecan.expose()
    def _route(self, remainder, request):
        uri = "/".join(remainder[1:])
        method = request.method
        self._filter_illegal_uri(uri, method)

        return self.forwarder, remainder

    @pecan.expose('json')
    def forwarder(self, *remainder, **kwargs):
        """ Forward any requests that don't have a specific match """

        # TODO(xiaohhui): Add validator for vim_id.
        vim_id = remainder[0]
        request = pecan.request
        try:
            vim_url = syscomm.getMultivimDriver(vim_id,
                                                full_path=request.path)

            # NOTE: Not sure headers should be set here. According to original
            # code, headers are discarded.
            retcode, content, status_code, resp = restcall.req_by_msb(
                vim_url, request.method, content=request.body)
        except exceptions.NotFound as e:
            pecan.abort(404, detail=str(e))
        except Exception as e:
            pecan.abort(500, detail=str(e))

        if retcode:
            # Execptions are handled within req_by_msb
            logger.error("Status code is %s, detail is %s.",
                         status_code, content)
        response = pecan.Response(body=content, status=status_code)
        if retcode == 0:
            for k in syscomm.getHeadersKeys(resp):
                response.headers[k] = resp[k]

        return response
