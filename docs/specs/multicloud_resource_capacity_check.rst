.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware, Inc.

=======================================
MultiCloud Resources Capacity Check API
=======================================

To better expose VIM capabilities and available resources capacity to external
consumer, some extensions need to be done by MultiCloud.


Problem Description
===================

Current MultiCloud didn't expose any standard API/methods to check/publish the
resources capacity for each VIM, which makes external project can not realize
the capacity information of VIM. When a VIM with shortage resources was chosen
to deploy a bunch of VNFs, it will fail eventually. Exposing these information
will could let external project to make a better decision on choosing which VIM
to deploy the VNFs.


Propose Change
==============

Cloud Locations
---------------

Cloud latitude and longitude information is needed by OOF to determine the
distance between vGMuxInfra and vG. The latitude and longitude information
are part of A&AI complex schema[A&AI Complex Schema]_, which related to
cloud-region schema.

There is no change needed to MultiCloud, but cloud administrator need to
input these information when register a new VIM. Currently ESR Portal don't
expose these input forms to cloud administrator, need ESR team to add related
workflow to create complex data in A&AI and create a relationship between
location information and cloud-region record.

... [A&AI Complex Schema] https://gerrit.onap.org/r/gitweb?p=aai/aai-common.git;a=blob;f=aai-schema/src/main/resources/oxm/aai_oxm_v12.xml;h=e146c06ac675a1127ee11205c0ff2544e4d9a81d;hb=HEAD#l772


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

Available Resource Check
------------------------

A new API will be used by OOF to check the available cloud resources, which
will help OOF to make a better placement decision. OOF will give a resrouces
requirement of a specific deployment and a list of VIMs which need to be
check whether have enough resources for this deployment. The ouput of
Multicloud will be a list of VIMs which have enough resources.

There will be two part of APIs for this requirement, an check_vim_capacity API
will be added to MultiCloud borker to return a list of VIMs, another API
<vim_id>/capacity_check will be added to each MultiCloud plugins, and return
true or false based on whether the VIM have enought resources. When MultiCloud
broker receive a POST request on check_vim_capacity, it will request to each
<vim_id>/capacity_check API, and return a list of VIMs with a true in response
data.

Format of "vim_id" complying to the functional requirement of
"Consistent ID of a Cloud Region"

::
  {cloud-owner}/{cloud-region-id}

Format of "VIM ID" complying to the functional requirement of
"Consistent ID of a Cloud Region"

::

  {
    "cloud-owner": string, //cloud owner name/ID
    "cloud-region-id": string, //cloud region ID
  }

  Example: array of "VIM ID" looks like:

  [
   {"cloud-owner": "owner1", "cloud-region-id": "regionid1"},
   {"cloud-owner": "owner2", "cloud-region-id": "regionid2"}
  ]

Format of "AZinfo"

::

  {
    "availability-zone-name": string, //name of available zone
    "vCPUTotal": int,  //number of total cores, optional field
    "MemoryTotal": float,  //GB,total memory, optional field
    "StorageTotal": int, //GB total storage, optional field
    "vCPUAvail": int,  //number of available cores
    "MemoryAvail": float,  //GB, availablesize of memory
    "StorageAvail": int, //GB, available storage
  }
  }

Format of "VIMinfo" which extend the "VIM ID" as below

::

  {
    "cloud-owner": string, //cloud owner name/ID
    "cloud-region-id": string, //cloud region ID
    "AZs": array,  // list of AZinfo
  }

Input of check_vim_capacity will be

::

  {
    "vCPU": int,  // number of cores
    "Memory": float,  // size of memory, GB
    "Storage": int, //GB
    "VIMs": array, //list of "VIM ID" with which OOF wish to check
  }

Output of check_vim_capacity will be

::

  {
    "VIMs": array, //list of VIMinfo
  }

Input of <vim_id>/capacity_check will be

::

  {
    "vCPU": int,
    "Memory": float,
    "Storage": int,
  }


Output of <vim_id>/capacity_check will be

::

  {
    "result": bool,
    "AZs": array, //list of AZinfo, optional field
  }


Work Items
==========

#. Work with ESR team for location inport form.
#. Add check_vim_capacity API to MultiCloud Broker.
#. Add check_vim_capacity API to each MultiCloud Plugins.

Tests
=====

#. Unit Tests with tox
#. CSIT Tests, the input/ouput of broker and each plugin see API design above.
