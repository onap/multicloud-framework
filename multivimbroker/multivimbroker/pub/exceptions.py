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


class BaseException(Exception):

    message = "Exception"

    def __init__(self, message=None, status_code="", content=""):
        super(BaseException, self).__init__(message)
        self.message = message or self.message
        self.status_code = status_code
        self.content = content


class VimBrokerException(BaseException):

    message = "vim error"


class NotFound(BaseException):

    message = "not found error"
