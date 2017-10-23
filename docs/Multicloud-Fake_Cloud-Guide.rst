================================
MultiCloud Fake_Cloud User Case
================================



multilcoud-vmware server not only provide vio plugin to access real openstack platform,but
also provide fake_cloud plugin which simulate thereal VIO function.The fake
cloud is suitable for testing openstack function if there is not real VIO platform.


Make sure you environment have msb,aai service and multicloud-vmware config file has the right msb_ip and
msb_port value,The config file path is vio/vio/pub/config/congfig.py



Register Fake Cloud to AAI
~~~~~~~~~~~~~~~~~~~~~~~~~~

Register vio information into AAI service with region name "vmware" and region id "fake"

.. code-block:: console

  $ curl -X PUT  -H "X-TransactionId":"get_aai_subr" -H "X-FromAppId":"AAI" -H "Accept":"application/json" \
    -H "Content-Type:"application/json"  -H "Authorization:Basic QUFJOkFBSQ==" \
    https://aai_ip:aai_port/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/vmware/fake \
      -d "{
            "cloud-owner": "vmware",
            "cloud-region-id": "fake",
            "cloud-type": "vmware",
            "cloud-region-version": "4.0",
            "identity-url": "http://MSB_IP:MSB_PORT/api/multicloud/v0/vmware_fake/identity/v3",
            "cloud-zone": "cloud zone",
            "complex-name": "complex name",
            "esr-system-info-list": {
                "esr-system-info": [
                    {
                        "system-name": "vmware-fake-cloud",
                        "type": "vim",
                        "service-url": "http://127.0.0.1:5000/v3",
                        "user-name": "admin",
                        "password": "vmware",
                        "system-type": "VIM",
                        "ssl-insecure": true,
                        "cloud-domain": "default",
                        "default-tenant": "admin",
                    }
                ]
            }
      }"

the identity url reprent the fake cloud identity url.



Test Examples
~~~~~~~~~~~~~

the ${fake_identiy_url}= "http://MSB_IP:MSB_PORT/api/multicloud/v0/vmware_fake/identity/v3"
the ${msb_address} =  "MSB_IP:MSB_PORT"

Get auth token
--------------

# send request to multicloud-framework(broker) service to get token of keystone V3

.. code-block:: console

  $ curl -X  POST   -d @test.json  -H 'Content-Type:application/json'   http://MSB_IP:MSB_PORT/api/multicloud/v0/vmware_fake/identity/v3/auth/tokens

test.json content example:

::

  {
    "auth": {
      "scope": {"project": {"id": “<project-id>”}},
      "identity":
	  {
		"password": {"user": {"domain": {"name": “<doman-name>”}, "password": “<user-password>”, "name": “<user-name>”}}, "methods": ["password"]
	  }
    }
  }


Response:
There are a large amounts of data including service endpoint, user information, etc.
For our testing  We  take nova and identity service endpoint address and auth token which is in response header named “X-Subject-Token”.

# you can find the endpoint url namespace is "api/multicloiud-vio/v0/vmware_fake", it represent the multicloud-vio service, So
requests sending to mulitcloud-vio will be forwarded to fake cloud.the ip and port reprenst ${msb_address}


Identity endpoint:
	http://$msb_address/api/multicloud-vio/v0/vmware_fake/identity

Nova endpoint:
	http://$msb_address/api/multicloud-vio/v0/vmware_fake/compute/<user-tenantid>


List projects
-------------

Use identity’s endpoint:  http://$msb_address/api/multicloud-vio/v0/vmware_fake/identity/

.. code-block:: console

  $ curl -X GET   -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/identity/projects


Get os Hypervisor
-----------------

Use nova’s endpoint:  http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<user-tenantid>


.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/os-hypervisors/detail


List instance of  user’s project
--------------------------------

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers


Show instance detail
--------------------

you need to input <server-id> in url path.

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/vimid/nova/tenantid/servers/<server-id>


Shutdown instance
-----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"os-stop":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Start instance
--------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"os-start":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Suspend instance
----------------

you need to input <server-id> in url path

.. code-block:: console

   $ curl -X POST -d '{"suspend":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Resume  instance
----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"resume":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Pause instance
--------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"pause":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Unpasue instance
----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"unpause":null}' -H 'X-Auth-Token:<token> -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


Reboot instance
---------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"reboot":{"type":"HARD"}}' -H 'X-Auth-Token:<token> -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/nova/<tenantid>/servers/<server-id>/action


list heat stacks
----------------

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/heat/<tenantid>/stacks


create preview stack
--------------------

.. code-block:: console

  $ curl -X POST -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/heat/<tenantid>/stacks/preview \
        -d "{
              "files": {},
              "disable_rollback": true,
              "parameters": {
                  "flavor": "m1.heat"
              },
              "stack_name": "teststack",
              "template": {
                  "heat_template_version": "2013-05-23",
                  "description": "Simple template to test heat commands",
                  "parameters": {
                      "flavor": {
                          "default": "m1.tiny",
                          "type": "string"
                      }
                  },
                  "resources": {
                      "hello_world": {
                          "type": "OS::Nova::Server",
                          "properties": {
                              "key_name": "heat_key",
                              "flavor": {
                                  "get_param": "flavor"
                              },
                              "image": "40be8d1a-3eb9-40de-8abd-43237517384f",
                              "user_data": "#!/bin/bash -xv\necho \"hello world\" &gt; /root/hello-world.txt\n"
                          }
                      }
                  }
              },
              "timeout_mins": 60
           }"


create  stack
-------------

.. code-block:: console

  $ curl -X POST -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/vmware_fake/heat/<tenantid>/stacks \
          -d  "{
                  "files": {},
                  "disable_rollback": true,
                  "parameters": {
                      "flavor": "m1.heat"
                  },
                  "stack_name": "teststack",
                  "template": {
                      "heat_template_version": "2013-05-23",
                      "description": "Simple template to test heat commands",
                      "parameters": {
                          "flavor": {
                              "default": "m1.tiny",
                              "type": "string"
                          }
                      },
                      "resources": {
                          "hello_world": {
                              "type": "OS::Nova::Server",
                              "properties": {
                                  "key_name": "heat_key",
                                  "flavor": {
                                      "get_param": "flavor"
                                  },
                                  "image": "40be8d1a-3eb9-40de-8abd-43237517384f",
                                  "user_data": "#!/bin/bash -xv\necho \"hello world\" &gt; /root/hello-world.txt\n"
                              }
                          }
                      }
                  },
                  "timeout_mins": 60
              }"


delete stack
------------

.. code-block:: console

  $ curl -X DELETE -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/vmware_fake/heat/<tenantid>/stacks/<stack_name>/<stack_id>

