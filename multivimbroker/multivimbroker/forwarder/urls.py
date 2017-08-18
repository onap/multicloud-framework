# Copyright 2017 Wind River Systems, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns

from multivimbroker.forwarder.views import Forward
from multivimbroker.forwarder.views import Identity


urlpatterns = [

    url(r'^openoapi/multivim/v1/(?P<vimid>[0-9a-zA-Z_-]+)/identity/v3$',Identity.as_view()),
    url(r'^openoapi/multivim/v1/(?P<vimid>[0-9a-zA-Z_-]+)', Forward.as_view()),

]

urlpatterns = format_suffix_patterns(urlpatterns)
