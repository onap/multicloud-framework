.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 Intel, Inc.

===============================
MultiCloud infra_workload API
===============================

We have two purposes for this API:

#. Integrate SO and Multicloud.
#. Generic API for SO to talk to different Multicloud plugins.


Problem Description
===================

Currently HPA flavors are returned by OOF to SO and SO copies these flavors in
the Heat template before sending the Heat template to Multicloud. In Casablanca
instead of SO making changes in the Heat template the flavor information will be
provided to Multicloud and Multicloud will pass this as parameters to HEAT
command line. In Dublin, instead of SO transferring the content of HEAT and HEAT_ENV
to Multicloud, Mutlicloud will download these information/artifacts.
For further detailed design, please refer to https://wiki.onap.org/display/DW/SO+Casablanca+HPA+Design


Propose Change
==============

Multi-Tenant Support
--------------------

Request Headers:
>>>>>>>>>>>>>>>>

To support multi-tenants over the same cloud region, all APIs defined below
should support to accept the following optional headers which are used to
specify a tenant other than the default one associated with the cloud region.

::

  "Project"   : Tenant/Project ID or Name specified by API consumer, Optional


Example 1:
::

  "Project: tenant1"

Example 2:
::

  "Project: fcca3cc49d5e42caae15459e27103efc"



Add infrastructure workload
---------------------------

API URL: POST http://{msb IP}:{msb port}/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload

Request Body:
>>>>>>>>>>>>>
::

  {
     "generic-vnf-id":"<generic-vnf-id>",
     "vf-module-id":"<vf-module-id>",
     "vf-module-model-invariant-id":"<uuid>",
     "vf-module-model-version-id":"<uuid>",
     "vf-module-model-customization-id":"<uuid>",
     "oof_directives":{},
     "sdnc_directives":{},
     "user_directives":{},
     "template_type":"<heat/tosca/etc.>",
     "template_data":{}
  }

 The 'vf-module-model-"*"-id’s will be used by the Multicloud plugin to retrieve the associated cloud artifacts.
 The 'oof_directives' is to convey oof’s input to multicloud.
 The 'sdnc_directives' for SDNC input to MultiCloud.
 The 'user_directives' is the parameter to convey input from VID portal to mutlicloud.
 They are all about how to populate data to template parameters.
 The precedence of the three directives are user_directives and oof_directives the sdnc_directives.

oof_directives:
:::::::::::::::
::

      "directives":[
         {
           "id":"<ID of VNFC>",
           "type": "vnfc",
           "directives":[
             {
               "type":"<flavor_directive>",
               "attributes":[
                 {
                   "attribute_name":"<name of attribute, such as flavor label>",
                   "attribute_value":"<value such as cloud specific flavor>"
                 }
               ]
             }
           ]
         },
         {
           "id":"<ID of VNF>",
           "type": "vnf",
           "directives":[
             {
               "type":"<Name of directive>",
               "attributes":[
                 {
                   "attribute_name":"<name of attribute>",
                   "attribute_value":"<value>"
                 }
               ]
             }
           ]
         }
      ]

Heat examples
:::::::::::::
::

  "template_type":"heat",
  "template_data":{
     "files":{  },
     "disable_rollback":true,
     "parameters":{
        "flavor":"m1.heat"
     },
     "stack_name":"teststack",
     "template":{
        "heat_template_version":"2013-05-23",
        "description":"Simple template to test heat commands",
        "parameters":
        {
           "flavor":{
              "default":"m1.tiny",
              "type":"string"
           }
        },
        "resources":{
           "hello_world":{
              "type":"OS::Nova::Server",
              "properties":{
                 "key_name":"heat_key",
                 "flavor":{
                    "get_param":"flavor"
                 },
                 "image":"40be8d1a-3eb9-40de-8abd-43237517384f",
                 "user_data":"#!/bin/bash -xv\necho \"hello world\" &gt; /root/hello-world.txt\n"
              }
           }
        }
     },
     "timeout_mins":60
  }

Response:
>>>>>>>>>

Response Codes
::::::::::::::
Success
.......

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 201 - Created      | Resource was created and is ready to use.                            |
+--------------------+----------------------------------------------------------------------+

Error
.....

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 400 - Bad Request  | Some content in the request was invalid.                             |
+--------------------+----------------------------------------------------------------------+
| 401 - Unauthorized | User must authenticate before making a request.                      |
+--------------------+----------------------------------------------------------------------+
| 409 - Conflict     | This operation conflicted with another operation on this resource.   |
+--------------------+----------------------------------------------------------------------+

Response Body
:::::::::::::
::

    {
        "template_type":"heat",
        "workload_id": "<The ID of infrastructure workload resource>"
        "template_response":
        {
            "stack": {
            "id": "<The UUID of stack>",
            "links": [
                {
                     "href": "<A list of URLs for the stack>",
                     "rel": "self"
                }
            ]
        }
    }

Delete infrastructure workload
------------------------------

API DELETE URL: http://{msb IP}:{msb port}/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload/{workload-id}

Response:
>>>>>>>>>

Response Codes
::::::::::::::
Success
.......

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 204 - No Content   | The server has fulfilled the request by deleting the resource.       |
+--------------------+----------------------------------------------------------------------+

Error
.....

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 400 - Bad Request  | Some content in the request was invalid.                             |
+--------------------+----------------------------------------------------------------------+
| 401 - Unauthorized | User must authenticate before making a request.                      |
+--------------------+----------------------------------------------------------------------+
| 404 - Not Found    | The requested resource could not be found.                           |
+--------------------+----------------------------------------------------------------------+
| 500 - Internal     | Something went wrong inside the service. This should not happen      |
|       Server Error | usually. If it does happen, it means the server has experienced      |
|                    | some serious problems.                                               |
+--------------------+----------------------------------------------------------------------+

Response Body
:::::::::::::
This request does not return anything in the response body.

Get infrastructure workload
----------------------------

API GET URL: http://{msb IP}:{msb port}/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload/{workload-id}

Response:
>>>>>>>>>

Response Codes
::::::::::::::
Success
.......

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 200 - OK           | Request was successful.                                              |
+--------------------+----------------------------------------------------------------------+

Error
.....

+--------------------+----------------------------------------------------------------------+
| Code               | Reason                                                               |
+====================+======================================================================+
| 400 - Bad Request  | Some content in the request was invalid.                             |
+--------------------+----------------------------------------------------------------------+
| 401 - Unauthorized | User must authenticate before making a request.                      |
+--------------------+----------------------------------------------------------------------+
| 404 - Not Found    | The requested resource could not be found.                           |
+--------------------+----------------------------------------------------------------------+
| 500 - Internal     | Something went wrong inside the service. This should not happen      |
|       Server Error | usually. If it does happen, it means the server has experienced      |
|                    | some serious problems.                                               |
+--------------------+----------------------------------------------------------------------+

Response Body
:::::::::::::
::

    {
        "template_type":"<heat/tosca/etc.>",
        "workload_id": "<The ID of infrastructure workload resource>",
        "workload_status":"CREATE_IN_PROCESS/CREATE_COMPLETE/DELETE_IN_PROCESS/etc"
    }

Work Items
==========

#. Work with SO.
#. Work with OOF team for oof_directive form.
#. Work with SDNC team for sdc_directive form.
#. Expose API by broker and each plugin.

Tests
=====

#. Unit Tests with tox.
#. Pairwise test with SO project.
#. Integration test with vCPE HPA test.
#. CSIT Tests, the input/ouput of broker and each plugin see API design above.
