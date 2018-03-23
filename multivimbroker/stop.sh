#!/bin/bash
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

# ps auxww | grep 'manage.py runserver 0.0.0.0:9001' | awk '{print $2}' | xargs kill -9
ps auxww |grep 'uwsgi --http :9001 --module multivimbroker.wsgi --master' |awk '{print $2}' |xargs kill -9
