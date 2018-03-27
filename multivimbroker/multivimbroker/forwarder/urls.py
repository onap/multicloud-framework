# Copyright 2017 Wind River Systems, Inc.
# Copyright (c) 2017-2018 VMware, Inc.
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

from multivimbroker.forwarder.views import CheckCapacity
from multivimbroker.forwarder.views import Extension
from multivimbroker.forwarder.views import Forward
from multivimbroker.forwarder.views import Identity
from multivimbroker.forwarder.views import Registry
from multivimbroker.forwarder.views import UnRegistry
from multivimbroker.forwarder.views import VIMTypes
from multivimbroker.forwarder.views import MultiPartView


urlpatterns = [
    url(r'^api/multicloud/v0/vim_types$',
        VIMTypes.as_view()),
    url(r'^api/multicloud/v0/check_vim_capacity$',
        CheckCapacity.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)/identity/v3$',
        Identity.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)/identity/v3'
        r'/auth/tokens$', Identity.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)/registry$',
        Registry.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)$',
        UnRegistry.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)/extensions$',
        Extension.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)/multipart',
        MultiPartView.as_view()),
    url(r'^api/multicloud/v0/(?P<vimid>[0-9a-zA-Z_-]+)',
        Forward.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
