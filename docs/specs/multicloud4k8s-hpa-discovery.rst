.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2019 Intel, Inc.

=================================================
MultiCloud for k8s HPA Discovery And Registration
=================================================
Discover HPA capability and register it to A&AI

Problem Description
===================

Kubernetes clusters using ONAP Multicloud K8s Plugin Project for R4/R5 do not report hardware
features to A&AI. Consequently during the CNF/VNF life cycle CNFs/VNFs that require or recommend
specific hardware during instantiation cannot dynamically reach the correct cluster and node that
provides the needed hardware capabilities.


Propose Change
==============

Cluster Registration FLOW
-------------------------
#. Cluster is deployed.
#. K8s registration agent is deployed on cluster.
#. K8s registration agent gathers features and cluster info.
#. K8s registration agent registers with K8s scheduler with labels identifying it's capabilities.
#. K8s Scheduler hook gives HPA Placement Plugin hardware features.
#. HPA Placement Plugins stores features in mongoDB.

AAI Registration
----------------
#. CLI registers cluster with K8s plugin.
#. Registration asks HPA Placement for cluster features.
#. HPA Placement gets cluster features from mongoDB.
#. HPA Placement plugin returns map of nodes/features to registration.
#. Registration module munges features into tenant/flavors updates AAI.

Converting node feature label to flavor properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Node feature label::
    
    feature.node.kubernetes.io/network-sriov.capable=True
    feature.node.kubernetes.io/network-sriov.configured=True
    feature.node.kubernetes.io/pci-1200_8086.present=True

Comments: The other parameter we can set default because node feature label can not provide.


Flavor properties::
    
    +-----------------------------------------------------+-------------+
    | Field                                               | Value       |
    +-----------------------------------------------------+-------------+
    | OS-FLV-DISABLED:disabled                            | False       |
    | OS-FLV-DISABLED:ephemeral                           | 0           |
    | feature.node.kubernetes.io/network-sriov.capable    | True        |
    | feature.node.kubernetes.io/network-sriov.configured | True        |
    | feature.node.kubernetes.io/pci-1200_8086.present    | True        |
    | disk                                                | 1           |
    | id                                                  | 1           |
    | name                                                | k8s_sriov_1 |
    | ram                                                 | 512         |
    | vcpus                                               | 1           |
    +-----------------------------------------------------+-------------+

Add API for K8s Plugin
^^^^^^^^^^^^^^^^^^^^^^
API V0 URL: POST http://{{MSB_IP}}:{{MSB_PORT}}/api/v0/multicloud-k8s/{{cloud-owner}}_{{cloud-region-id}}/registry
API V1 URL: POST http://{{MSB_IP}}:{{MSB_PORT}}/api/v1/multicloud-k8s/{{cloud-owner}}/{{cloud-region-id}}/registry

Request Body:
>>>>>>>>>>>>>
::

  {
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
| 202 - ACCEPTED     | Discover HPA and successfully register it                            |
+--------------------+----------------------------------------------------------------------+

Error
.....

+----------------------------+--------------------------------------------------------------+
| Code                       | Reason                                                       |
+============================+==============================================================+
| 500 - INTERAL_SERVER_ERROR | Some content in the request was invalid.                     |
+----------------------------+--------------------------------------------------------------+


Response Body
:::::::::::::
::

    {
    }
	
API V0 URL: GET http://{{MSB_IP}}:{{MSB_PORT}}/api/v0/multicloud-k8s/{{cloud-owner}}_{{cloud-region-id}}/registry
API V1 URL: GET http://{{MSB_IP}}:{{MSB_PORT}}/api/v1/multicloud-k8s/{{cloud-owner}}/{{cloud-region-id}}/registry

Request Body:
>>>>>>>>>>>>>
::

  {
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
| 202 - ACCEPTED     | Discover HPA and successfully register it                            |
+--------------------+----------------------------------------------------------------------+

Error
.....

+----------------------------+--------------------------------------------------------------+
| Code                       | Reason                                                       |
+============================+==============================================================+
| 500 - INTERAL_SERVER_ERROR | Some content in the request was invalid.                     |
+----------------------------+--------------------------------------------------------------+


Response Body
:::::::::::::
::

    {
    }
	
	
API V0 URL: DELETE http://{{MSB_IP}}:{{MSB_PORT}}/api/v0/multicloud-k8s/{{cloud-owner}}_{{cloud-region-id}}/registry
API V1 URL: DELETE http://{{MSB_IP}}:{{MSB_PORT}}/api/v1/multicloud-k8s/{{cloud-owner}}/{{cloud-region-id}}/registry

Request Body:
>>>>>>>>>>>>>
::

  {
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
| 204 - NO_CONTENT   | Successfully delete HPA information from AAI                         |
+--------------------+----------------------------------------------------------------------+

Error
.....

+----------------------------+--------------------------------------------------------------+
| Code                       | Reason                                                       |
+============================+==============================================================+
| 500 - INTERAL_SERVER_ERROR | Some content in the request was invalid.                     |
+----------------------------+--------------------------------------------------------------+


Response Body
:::::::::::::
::

    {
    }

Work Items
==========

#. Work with CLI.
#. Work with AAI.
#. Expose API by broker and k8s plugin.

Tests
=====

#. Unit Tests with tox
#. Pairwise test with AAI project.
#. Integration test with vFW HPA test.
#. CSIT Tests, the input/ouput of broker and k8s plugin see API design above.

Reference
=========
https://wiki.onap.org/display/DW/Extending+HPA+for+K8S
