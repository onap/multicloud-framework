================================
ONAP MultiCloud Test Guide
================================

Test Examples
~~~~~~~~~~~~~

The env HOST_IP is msb-iag service cluster-ip value is "10.43.188.78"(see it above).
The vimid is "vmware_vio"  the registered name in aai.


Get V2 auth token
-----------------

# VIO openstack  support keystone V2 version, vio proxy-plugin will check the request body format, if request body format
# is keystone V2 format will access keystone V2  service, if not it will access keystone V3 service.
# this example show how to access keystone V2 service.
.. code-block:: console

  $ curl -X POST -d @testV2.json  -H 'Content-Type:application/json'  http://$msb_address/api/multicloud/v0/<vimid>/identity/v2.0/tokens

testV2.json content:

::

  {
        "auth": {
            "tenantName": "admin",
            "passwordCredentials": {
                "username": "admin",
                "password": "vmware"
            }
        }
  }


Response:
There are a large amounts of data including service endpoint, user information, etc.
For our testing  We  take nova and identity service endpoint address and auth token which is in response header named “X-Subject-Token”.

# you can find the endpoint url namespace is "api/multicloiud-vio/v0", it represent the multicloud-vio service, So
requests sending to mulitcloud-vio will be forwarded to backend  VIO openstack.


Identity endpoint:
	http://$msb_address/api/multicloud-vio/v0/<vimid>/identity

Nova endpoint:
	http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<user-tenantid>


Get V3 auth token
-----------------

# send request to multicloud-framework(broker) service to get token

.. code-block:: console

  $ curl -X  POST   -d @test.json  -H 'Content-Type:application/json'   http://$msb_address/api/multicloud/v0/<vimid>/identity/v3/auth/tokens

test.json content example:

::

  {
    "auth": sudo pip install virtualenv{
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

# you can find the endpoint url namespace is "api/multicloiud-vio/v0", it represent the multicloud-vio service, So
requests sending to mulitcloud-vio will be forwarded to backend  VIO openstack.


Identity endpoint:
	http://$msb_address/api/multicloud-vio/v0/<vimid>/identity

Nova endpoint:
	http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<user-tenantid>


List projects
-------------

Use identity’s endpoint:  http://$msb_address/api/multicloud-vio/v0/<vimid>/identity/

.. code-block:: console

  $ curl -X GET   -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/<vimid>/identity/projects


Get os Hypervisor
-----------------

Use nova’s endpoint:  http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<user-tenantid>


.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/os-hypervisors/detail


List instance of  user’s project
--------------------------------

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers


Show instance detail
--------------------

you need to input <server-id> in url path.

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/vimid/nova/tenantid/servers/<server-id>


Shutdown instance
-----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"os-stop":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Start instance
--------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"os-start":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Suspend instance
----------------

you need to input <server-id> in url path

.. code-block:: console

   $ curl -X POST -d '{"suspend":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Resume  instance
----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"resume":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Pause instance
--------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"pause":null}' -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Unpasue instance
----------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"unpause":null}' -H 'X-Auth-Token:<token> -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Reboot instance
---------------

you need to input <server-id> in url path

.. code-block:: console

  $ curl -X POST -d '{"reboot":{"type":"HARD"}}' -H 'X-Auth-Token:<token> -H 'Content-Type:application/json'  http://$msb_address/api/multicloud-vio/v0/<vimid>/nova/<tenantid>/servers/<server-id>/action


Upload Image Task
-----------------

create uploading image task by image url:

.. code-block:: console

   $ curl -X POST -d '{"input": {"image_properties":
     {"container_format": "bare", "name": "<image_name>"},
     "import_from_format": "<disk_format>",
     "import_from": "<image_url>"},
     "type": "import"}'
     -H 'X-Auth-Token:<token>' -H 'Content-Type:application/json' http://$msb_address/api/multicloud-vio/v0/<vimid>/glance/v2/tasks

get the taskid from response body,then query the task status by taskid.

.. code-block:: console

   $ curl -X GET -H 'X-Auth-Token:<token>'  http://$msb_address/api/multicloud-vio/v0/<vimid>/glance/v2/tasks/<taskid>

You can see the description and properties of task in response body,if 'status' is  success, it will show image_id in
result block.

query the image status by image_id

.. code-block:: console

  $ curl -X GET -H 'X-Auth-Token:<token>' http://$msb_address/api/multicloud-vio/v0/<vimid>/glance/v2/images/<image_id>
