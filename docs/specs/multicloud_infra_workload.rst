.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 Intel, Inc.

===============================
MultiCloud infra_workload API
===============================

To better shield VIM differences for SO .


Problem Description
===================

Currently HPA flavors are returned by OOF  to SO and SO copies these flavors in
the Heat template before sending the Heat template to Multicloud.  In Casablanca
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


::

  { 
     "generic-vnf-id":"<generic-vnf-id>",  
     "vf-module-id":"<vf-module-id>",  
     "oof_directives":{},
     "sdnc_directives":{},
     ************************************************************************************
     parameters below template type are valid for request with “template_type”:“heat”    
     ************************************************************************************
     "template_type":"<heat/tosca/etc.>",
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

oof_directives:
::

   "oof_directives":{ 
      "directives":[ 
         { 
            "vnfc_directives":[ 
               { 
                  "vnfc_id":"<ID of VNFC>",
                  "directives":[ 
                     { 
                        "directive_name":"<Name of directive,example flavor_directive>",
                        "attributes":[ 
                           { 
                              "attribute_name":"<name of attribute, such as flavor label>",
                              "attribute_value":"<value such as cloud specific flavor>"
                           }
                        ]
                     },
                     { 
                        "directive_name":"<Name of directive,example vnic-info>",
                        "attributes":[ 
                           { 
                              "attribute_name":"<name of attribute, such as vnic-type>",
                              "attribute_value":"<value such as direct/normal>"
                           },
                           { 
                              "attribute_name":"<name of attribute, such as provider netweork>",
                              "attribute_value":"<value such as physnet>"
                           }
                        ]
                     }
                  ]
               }
            ]
         },
         { 
            "vnf_directives":{ 
               "directives":[ 
                  { 
                     "directive_name":"<Name of directive>",
                     "attributes":[ 
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value>"
                        }
                     ]
                  },
                  { 
                     "directive_name":"<Name of directive>",
                     "attributes":[ 
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value >"
                        },
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value >"
                        }
                     ]
                  }
               ]
            }
         }
      ]
   },

sdc_directives:

::

  "sdnc_directives":{ 
      "directives":[ 
         { 
            "vnfc_directives":[ 
               { 
                  "vnfc_id":"<ID of VNFC>",
                  "directives":[ 
                     { 
                        "directive_name":"<Name of directive,example flavor_directive>",
                        "attributes":[ 
                           { 
                              "attribute_name":"<name of attribute, such as flavor label>",
                              "attribute_value":"<value such as cloud specific flavor>"
                           }
                        ]
                     },
                     { 
                        "directive_name":"<Name of directive,example vnic-info>",
                        "attributes":[ 
                           { 
                              "attribute_name":"<name of attribute, such as vnic-type>",
                              "attribute_value":"<value such as direct/normal>"
                           },
                           { 
                              "attribute_name":"<name of attribute, such as provider netweork>",
                              "attribute_value":"<value such as physnet>"
                           }
                        ]
                     }
                  ]
               }
            ]
         },
         { 
            "vnf_directives":{ 
               "directives":[ 
                  { 
                     "directive_name":"<Name of directive>",
                     "attributes":[ 
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value>"
                        }
                     ]
                  },
                  { 
                     "directive_name":"<Name of directive>",
                     "attributes":[ 
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value >"
                        },
                        { 
                           "attribute_name":"<name of attribute>",
                           "attribute_value":"<value >"
                        }
                     ]

                  }
               ]
            }
         }
      ]
   },


Work Items
==========

#. Work with OOF team for oof_directive form.
#. Work with SDNC team for sdc_directive form.

Tests
=====

#. Unit Tests with tox
#. CSIT Tests, the input/ouput of broker and each plugin see API design above.

