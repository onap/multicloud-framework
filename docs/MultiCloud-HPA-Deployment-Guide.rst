..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

====================================
ONAP MultiCloud HPA Deployment Guide
====================================

The guide for MultiCloud HPA Deployment.

Architecture & Policies & Mappings
==================================

Please refer to the link for more architecture details:

https://wiki.onap.org/pages/viewpage.action?pageId=20874679

Please refer to the link for more Policies&Mappings details:

https://wiki.onap.org/display/DW/HPA+Policies+and+Mappings

Prerequisites
=============
configure openstack with proper flavors (with name prefixed by "onap." to carry HPA information to ONAP), example flavor:

.. code-block:: console

    nova flavor-create onap.hpa.medium 110 4096 0 6
    #cpu pining
    nova flavor-key onap.hpa.medium set hw:cpu_policy=dedicated
    nova flavor-key onap.hpa.medium set hw:cpu_thread_policy=prefer
    #cpu topology
    nova flavor-key onap.hpa.medium set hw:cpu_sockets=2
    nova flavor-key onap.hpa.medium set hw:cpu_cores=4
    nova flavor-key onap.hpa.medium set hw:cpu_threads=8
    #hugepage
    nova flavor-key onap.hpa.medium set hw:mem_page_size=large
    #numa
    nova flavor-key onap.hpa.medium set hw:numa_nodes=2    
    nova flavor-key onap.hpa.medium set hw:numa_cpus.0=0,1 hw:numa_cpus.1=2,3,4,5 hw:numa_mem.0=2048 hw:numa_mem.1=2048

collect following information for on-boarding this Cloud instance to ONAP:

.. code-block:: console

    your openstack project name
    your openstack user
    your openstack password
    your openstack keystone endpoint
    your openstack Region ID: e.g. RegionOne
    your openstack owner name/ID (any string without underscore character '_'), e.g. CloudOwner

Note: along with the Region ID, the owner name/ID comprises unique ID of a cloud region within ONAP

With Heat based ONAP:

.. code-block:: console

    export ONAP_AAI_IP=<floating IP of VM with name "onap-aai-inst1">
    export ONAP_AAI_PORT=8443
    export ONAP_MSB_IP=<floating IP of VM with name "onap-multi-service">
    export ONAP_MSB_PORT=80

With OOM based ONAP:

.. code-block:: console
 
    export ONAP_AAI_IP=<floating IP of VM with name "k8s_1">
    export ONAP_AAI_PORT=30233
    export ONAP_MSB_IP=<floating IP of VM with name "k8s_1">
    export ONAP_MSB_PORT=30280

The geographic location of the cloud region
===========================================
make sure there is complex object to represent the geographic location of the cloud region
in case you need create a complex object "clli1":

.. code-block:: console

    curl -X PUT \
    https://$ONAP_AAI_IP:$ONAP_AAI_PORT/aai/v13/cloud-infrastructure/complexes/complex/clli1 \
    -H 'Accept: application/json' \
    -H 'Authorization: Basic QUFJOkFBSQ==' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 2b272126-aa65-41e6-aa5d-46bc70b9eb4f' \
    -H 'Real-Time: true' \
    -H 'X-FromAppId: jimmy-postman' \
    -H 'X-TransactionId: 9999' \
    -d '{
        "physical-location-id": "clli1",
        "data-center-code": "example-data-center-code-val-5556",
        "complex-name": "clli1",
        "identity-url": "example-identity-url-val-56898",
        "physical-location-type": "example-physical-location-type-val-7608",
        "street1": "example-street1-val-34205",
        "street2": "example-street2-val-99210",
        "city": "Beijing",
        "state": "example-state-val-59487",
        "postal-code": "100000",
        "country": "example-country-val-94173",
        "region": "example-region-val-13893",
        "latitude": "39.9042",
        "longitude": "106.4074",
        "elevation": "example-elevation-val-30253",
        "lata": "example-lata-val-46073"
        }'

Register a cloud region
=======================
register a cloud region to represent the VIM with the specific tenant

note: please update the parameters enclosed with "<>"

.. code-block:: console

    curl -X PUT \
    https://$ONAP_AAI_IP:$ONAP_AAI_PORT/aai/v13/cloud-infrastructure/cloud-regions/cloud-region/CloudOwner/RegionOne \
    -H 'Accept: application/json' \
    -H 'Authorization: Basic QUFJOkFBSQ==' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 8b9b95ae-91d6-4436-90fa-69cb4d2db99c' \
    -H 'Real-Time: true' \
    -H 'X-FromAppId: jimmy-postman' \
    -H 'X-TransactionId: 9999' \
    -d '{
        "cloud-owner": "CloudOwner",
        "cloud-region-id": "RegionOne",
        "cloud-type": "openstack",
        "owner-defined-type": "t1",
        "cloud-region-version": "titanium_cloud",
        "complex-name": "clli1",
        "cloud-zone": "CloudZone",
        "sriov-automation": false,
        "identity-url": "WillBeUpdatedByMultiCloud",
        "esr-system-info-list": {
            "esr-system-info": [
                {
                "esr-system-info-id": "<random UUID, e.g. 5c85ce1f-aa78-4ebf-8d6f-4b62773e9bde>",
                "service-url": "http://<your openstack keystone endpoint, e.g. http://10.12.25.2:5000/v3>",
                "user-name": "<your openstack user>",
                "password": "<your openstack password>",
                "system-type": "VIM",
                "ssl-insecure": true,
                "cloud-domain": "Default",
                "default-tenant": "<your openstack project name>",
                "system-status": "active"
                }
            ]
          }
        }'

associate the cloud region with the location object:

.. code-block:: console

    curl -X PUT \
    https://$ONAP_AAI_IP:$ONAP_AAI_PORT/aai/v13/cloud-infrastructure/cloud-regions/cloud-region/CloudOwner/RegionOne/relationship-list/relationship \
    -H 'Authorization: Basic QUFJOkFBSQ==' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 7407d60c-8ce7-45de-ada3-4a7a9e88ebd4' \
    -H 'Real-Time: true' \
    -H 'X-FromAppId: jimmy-postman' \
    -H 'X-TransactionId: 9999' \
    -d '{
        "related-to": "complex",
        "related-link": "/aai/v13/cloud-infrastructure/complexes/complex/clli1",
        "relationship-data": [
            {
            "relationship-key": "complex.physical-location-id",
            "relationship-value": "clli1"
            }
            ]
        }'


Trigger the MultiCloud registration
===================================

.. code-block:: console

    curl -X POST \
    'http://$ONAP_MSB_IP:$ONAP_MSB_PORT/api/multicloud-titanium_cloud/v0/CloudOwner_RegionOne/registry' \
    -H 'Accept: application/json' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 8577e1cc-1038-471d-8b3b-d36fe44ae023'


Verify
======
verify if the cloud region was registered properly (with HPA information populated)

.. code-block:: console

    curl -X GET \
    'https://$ONAP_AAI_IP:$ONAP_AAI_PORT/aai/v13/cloud-infrastructure/cloud-regions/cloud-region/CloudOwner/RegionOne?depth=all' \
    -H 'Accept: application/json' \
    -H 'Authorization: Basic QUFJOkFBSQ==' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 2899359f-871b-4e61-a307-ecf8b3144e3f' \
    -H 'Real-Time: true' \
    -H 'X-FromAppId: jimmy-postman' \
    -H 'X-TransactionId: 9999'

Note: The response of querying a cloud region above should return with a comprehensive cloud region object, you should find out the "hpa-capabilities" under certain flavor object with name prefixed by "onap."


