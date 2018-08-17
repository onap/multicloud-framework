.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 Intel, Inc.

===============================
MultiCloud infra_workload API
===============================

we have two purposes for this API:

#. Intergrate SO and Multicloud.
#. Generic API for SO to talk to different Multicloud plugins.


Problem Description
===================

Currently HPA flavors are returned by OOF  to SO and SO copies these flavors in
the Heat template before sending the Heat template to Multicloud.  In Casablanca
instead of SO making changes in the Heat template the flavor information will be
provided to Multicloud and Multicloud will pass these as parameters to HEAT
command line.
The detail design refer to https://wiki.onap.org/display/DW/SO+Casablanca+HPA+Design


Propose Change
==============

Add infrastructure workload API
-------------------------------

API URL: http://{msb IP}:{msb port}/api/multicloud /v1/{cloud-owner}/{cloud-region-id}/infra_workload

Request Body:

.. code-block:: json

  {
     "generic-vnf-id":"<generic-vnf-id>",
     "vf-module-id":"<vf-module-id>",
     "oof_directives":{},
     "sdnc_directives":{},
     "template_type":"<heat/tosca/etc.>",
     "template_data":{}
  }

oof_directives:

.. code-block:: json

  {
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
   }

Heat examples
=============

.. code-block:: json

  {
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
  }

Work Items
==========

#. Work with SO.
#. Work with OOF team for oof_directive form.
#. Work with SDNC team for sdc_directive form.

Tests
=====

#. Unit Tests with tox
#. CSIT Tests, the input/ouput of broker and each plugin see API design above.
