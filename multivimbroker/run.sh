#!/bin/bash
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

sed -i "s/MSB_SERVICE_IP =.*/MSB_SERVICE_IP = \"${MSB_ADDR}\"/g" multivimbroker/pub/config/config.py
sed -i "s/MSB_SERVICE_PORT =.*/MSB_SERVICE_PORT = \"${MSB_PORT}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_ADDR =.*/AAI_ADDR = \"${AAI_ADDR}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_PORT =.*/AAI_PORT = \"${AAI_PORT}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_SCHEMA_VERSION =.*/AAI_SCHEMA_VERSION = \"${AAI_SCHEMA_VERSION}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_USERNAME =.*/AAI_USERNAME = \"${AAI_USERNAME}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_PASSWORD =.*/AAI_PASSWORD = \"${AAI_PASSWORD}\"/g" multivimbroker/pub/config/config.py

nohup python manage.py runserver 0.0.0.0:9001 2>&1 &

while [ ! -f logs/runtime_multivimbroker.log ]; do
    sleep 1
done

tail -F logs/runtime_multivimbroker.log
