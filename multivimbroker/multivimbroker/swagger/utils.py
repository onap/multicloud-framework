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
import os


def get_swagger_json_data():
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.flavor.swagger.json')
    f = open(json_file)
    json_data = json.JSONDecoder().decode(f.read())
    f.close()
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.image.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.network.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.subnet.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.server.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.volume.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.vport.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.tenant.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.host.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.limit.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])
    json_file = os.path.join(os.path.dirname(__file__),
                             'multivim.identity.swagger.json')
    f = open(json_file)
    json_data_temp = json.JSONDecoder().decode(f.read())
    f.close()
    json_data["paths"].update(json_data_temp["paths"])
    json_data["definitions"].update(json_data_temp["definitions"])

    return json_data
