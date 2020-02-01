#!/bin/sh
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

sed -i "s/MSB_SERVICE_PROTOCOL =.*/MSB_SERVICE_PROTOCOL = \"${MSB_PROTO}\"/g" multivimbroker/pub/config/config.py
sed -i "s/MSB_SERVICE_IP =.*/MSB_SERVICE_IP = \"${MSB_ADDR}\"/g" multivimbroker/pub/config/config.py
sed -i "s/MSB_SERVICE_PORT =.*/MSB_SERVICE_PORT = \"${MSB_PORT}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_ADDR =.*/AAI_ADDR = \"${AAI_ADDR}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_PORT =.*/AAI_PORT = \"${AAI_PORT}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_SCHEMA_VERSION =.*/AAI_SCHEMA_VERSION = \"${AAI_SCHEMA_VERSION}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_USERNAME =.*/AAI_USERNAME = \"${AAI_USERNAME}\"/g" multivimbroker/pub/config/config.py
sed -i "s/AAI_PASSWORD =.*/AAI_PASSWORD = \"${AAI_PASSWORD}\"/g" multivimbroker/pub/config/config.py

logDir="/var/log/onap/multicloud/multivimbroker"
if [ ! -x  $logDir  ]; then
       mkdir -p $logDir
fi

if [ "$WEB_FRAMEWORK" == "pecan" ]
then
    python multivimbroker/scripts/api.py
else
    # nohup python manage.py runserver 0.0.0.0:9001 2>&1 &
    if [ "${SSL_ENABLED}" = "true" ]; then
        nohup uwsgi --https :9001,multivimbroker/pub/ssl/cert/cert.crt,multivimbroker/pub/ssl/cert/cert.key,HIGH -t 120 --module multivimbroker.wsgi --master --processes 4 &
    else
        nohup uwsgi --http :9001 -t 120 --module multivimbroker.wsgi --master --processes 4 &
    fi

    while [ ! -f $logDir/multivimbroker.log ]; do
        sleep 1
    done

    tail -F  $logDir/multivimbroker.log
fi
