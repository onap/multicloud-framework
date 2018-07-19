..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.
 Copyright (c) 2017-2018 Wind River Systems, Inc.

==============================
MultiCloud HEAT and AAI Bridge
==============================

This proposal describes how MultiCloud plugin automates the updating of HEAT stack resources into AAI after HEAT based VNF instantiation

Problems Statement
==================

With heat template based VNF instantiation, SO interacts with target OpenStack instance directly (or indirectly via MultiCloud) to launch
HEAT stacks. Once these HEAT stacks are created successfully, the ONAP users have to update the AAI with this HEAT stack resources manually
by executing the ROBOT heatbridge script which not only imposes extra burden on ONAP users but also breaks the automating process of VNF orchestration.

Considering that SO will integrate with MultiCloud in Casablanca Release, and the updating of AAI with HEAT stack resources demands no further
interaction between SO and MultiCloud, neither any changes introduced to existing AAI schema. It is nature and elegant to automate this AAI updating process by MultiCloud plugin.

https://wiki.onap.org/display/DW/Vetted+vFirewall+Demo+-+Full+draft+how-to+for+F2F+and+ReadTheDocs?src=contextnavpagetreemode


Proposed Design and Workflow
============================

**This automation assumes**:
 - SO integrates with multicloud so that the heat based VNF will be instantiated to an OpenStack Instance indirectly via MultiCloud.
 - SO will pass on the generic VNF ID and/or VF Module ID to MultiCloud.
 - ONAP user will not execute the ROBOT heatbridge script any more.


**The proposed changes to MultiCloud Broker and Plugins**
 - Add a new northbound API, e.g. POST /api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload , the request body is a json object consists of generic vnf id, vf module id, heat template, heat env file, and other parameters, e.g. oof selected flavors
 - MultiCloud Broker distribute this API request to corresponding MultiCloud Plugin
 - MultiCloud Plugin will validate the request with input data, call OpenStack HEAT API to launch HEAT stacks, and updating AAI with the launched HEAT stack resource, and associate these resource to generic VNF/VF-Module Objects.

**Updated AAI objects**
 - /cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}
 - /network/generic-vnfs/generic-vnf/{vnf-id}/vf-modules/vf-module/{vf-module-id}


Appendix:
=========

https://wiki.onap.org/display/DW/AAI+REST+API+Documentation+-+Beijing
