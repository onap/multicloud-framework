#    Licensed under the Apache License, Version 2.0 (the "License"); you may
#    not use this file except in compliance with the License. You may obtain
#    a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#    License for the specific language governing permissions and limitations
#    under the License.

from oslo_concurrency import processutils
from oslo_config import cfg
from oslo_service import service
from oslo_service import wsgi

from multivimbroker.api_v2 import app


CONF = cfg.CONF


class WSGIService(service.ServiceBase):
    """Provides ability to launch API from wsgi app."""

    def __init__(self):
        self.app = app.setup_app()

        self.workers = processutils.get_worker_count()

        self.server = wsgi.Server(
            CONF,
            "multivimbroker",
            self.app,
            # TODO(xiaohhui): these should be configurable.
            host="0.0.0.0",
            port="9002",
            use_ssl=False
        )

    def start(self):
        self.server.start()

    def stop(self):
        self.server.stop()

    def wait(self):
        self.server.wait()

    def reset(self):
        self.server.reset()
