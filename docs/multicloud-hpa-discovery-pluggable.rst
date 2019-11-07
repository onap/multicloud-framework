.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware, Inc.

==================================
MultiCloud HPA Discovery Pluggable
==================================

To better support more HPA capabilities without modifying the implementation code 
for Multicloud. So we need to extract the HPA discovery part of the code and make
it into many drivers(HPA Discovery Driver). if we want to add new HPA feature or 
modify the implementation code for the existing feature, we just need to modify 
HPA Discovery Driver code.

Problem Description
===================

Currently, every time we add or modify an HPA feature, we need to modify the
HPA discovery code, which will affect the other HPA feature. It is difficult
for device vendors to participate because they need to understand the entire
ONAP process.In fact, we should design it so that it only requires the vendor
to focus on both discovery and use.


Propose Change
==============

Extract the original HPA discovery part code and make it into many drivers. we
need to add hook in Openstack, Kubernetes and others plugin. then we develop
drivers for various language. we will get below benefit:

#. Each driver development is independent, and modifying the driver and adding
the driver does not have any impact on each other.
#. It is easier for vendors to participate in the development of ONAP. They no
longer need to understand the whole working principle of ONAP, but only need to
understand the interface of the driver.
#. Any driver problem will not affect the HPA discovery process.
#. The publishing difficulty of openstack package is reduced. Even if the latest
driver has a little problem. We just need to point to the stable driver version
of the pip package in the dockerfile.

We plan to develop drivers in python language(in R6 release) and drivers in golang
language(in R7 release).

Add New Driver
-------------------------------
The hardware vendor determines whether upstream drivers. If vendor upstream driver,
then submitted the driver code to multi-openstack repo. then He/She will also need package the driver and push to pip repo for distribution. In case that vendor doesn't upstream the driver, he/she could maintain the driver in local repo as well.  

  # Dockerfile(Example)

  from multi-windriver:1.4.0

  pip install <hpa-discovery-driver1(device vendor1)>

  pip install <add hpa-discovery-driver2(device vendor2)>

Driver Version Control
----------------------

We will put HPA discovery drivers as pip package to release pip REPO. name like onap-device-driver-<drivername>-<version>

when we release the plugin, we will add the driver of pip package to the docker.

Driver code Management
----------------------
For python language driver, we put it into multi-cloud openstack plugin.

For golang language driver, we put it into multi-cloud kubernetes plugin.

Security
--------
we solve one flavor each time, the result maximin is 1500 bytes. We will verify the validity of the data?

CRUD
----
Driver don't write AAI, plugin will write AAI. driver just format data.

Create
^^^^^^
Fill discover HPA data, transform data, format data in the driver, and then return the data, call AAI.

Delete
^^^^^^
Driver don't write AAI and just return resource URL accord to flavor information.

Update
^^^^^^
update same as create, please see https://git.onap.org/multicloud/openstack/tree/share/common/msapi/helper.py L142

Consume
-------
OOF can parse the AAI data, the driver just need to follow AAI schema. OOF output is oof_directives which is defined in https://git.onap.org/multicloud/framework/tree/docs/specs/multicloud_infra_workload.rst.

check VIM capacity will store in driver. driver will get information from "Collectd". This will be completed in next release.

FCAPS
-----
Driver with some issue: alarm(pool or other mode).
if driver have some issues, the hpa capabilities will be empty.

Usage
=====
Local use
---------

To integrate with multicloud openstack plugin, run below command

 $ python setup.py build

 $ python setup.py install

based on pip package
--------------------

To integrate with multicloud openstack plugin, run below command in create docker image

 $ pip install <package-name>

Tests
=====

#. Unit Tests with tox.
#. Integration test will use SRIOV-NIC as example.

Reference
=========
https://wiki.onap.org/display/DW/Multicloud+HPA+Discovery+Pluggable
