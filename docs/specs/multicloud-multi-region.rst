..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.
 Copyright (c) 2017-2018 Wind River Systems, Inc.

===============================
MultiCloud Multi-Region support
===============================

This proposal describes how MultiCloud plugin automates the on-boarding of tens or hundreds of cloud regions by leveraging multi-region feature

Problems Statement
==================

The `ONAP functional requirement for Edge Automation <https://wiki.onap.org/display/DW/Edge+Automation+through+ONAP>`_ aims to automate the VNF orchestration across edge stacks.

Before VNF can be orchestrated over these edge stacks, these edge stacks must be on-boarded into ONAP. This can be a very big challenge in case of tens of or hundred of edge stacks. Fortunately, With OpenStack multi-region feature, the OpenStack primary region will expose
all secondary regions' endpoints. Hence MultiCloud plugins could automate the discovery and registry
of all OpenStack secondary regions.


Proposed Design and Workflow
============================

**This automation assumes**:
 - ONAP could use the same set of credentials (project, user/password) to access all OpenStack regions for orchestration.
 - ONAP user will explicitly enable the automation of discovery OpenStack secondary regions during manually on-boarding the OpenStack primary region.
 - ONAP users could manually manage the cloud regions representing those secondary regions just like a normal cloud region


**With OpenStack primary region, the ONAP user will**:
.. https://wiki.onap.org/pages/viewpage.action?pageId=25431491

 - Manually on-board this primary region with ESR VIM registration portal.
 - Input the {cloud-owner} and {cloud-region-id} as the ID of cloud region which is unique.
 - Specify the location id
 - Specify the intention of automation of OpenStack secondary regions managed by this primary region. This intention is specified with key-value pair {"multi-region-discovery":"true"} populated to cloud region's property "cloud-extra-info"


**With OpenStack primary region, the corresponding MultiCloud plugin will**:
 - Discover all OpenStack secondary regions in case being explicitly indicated with key-value pair {"multi-region-discovery":"true"}
 - Register each of discovered OpenStack regions as a new cloud region into AAI.
 - Duplicate the credentials and location relationship from primary region.
 - Duplicate all or part of the cloud region's property "cloud-extra-info",
 - Add one more key-value pair to cloud region's property "cloud-extra-info": {"primary-region":["{cloud-owner}","{cloud-region-id}"]}
 - Add relationship to the cloud region of primary region: to be checked.
 - Generate the ID of cloud region representing the OpenStack secondary region with following rules:
   - {cloud-owner} should be {cloud-owner of primary region}.
   - {cloud-region-id} will be the concatenated string with format: {cloud-region-id of primary region}.{OpenStack Region ID}.
 - The composite keys {cloud-owner},{cloud-region-id of primary region}.{OpenStack Region ID} will be unique since the composite key of primary region: {cloud-owner},{cloud-region-id} is unique.


**With generated cloud region representing the OpenStack secondary region, the ONAP user could**:
 - update the credentials so that the ONAP could use updated credentials to orchestrate VNF to this cloud region
 - update the location of the cloud region by associating it to different complex object other than the one same to primary region.
 - Update cloud region's property "cloud-extra-info".
 - Decommission or delete the cloud region


Appendix:
=========

There is a property of cloud region object named "cloud-extra-info"
.. https://wiki.onap.org/display/DW/AAI+REST+API+Documentation+-+Beijing

::

   cloud-extra-info: string
     ESR inputs extra information about the VIM or Cloud which will be decoded by MultiVIM.

the intention of this property is to enable the extending of cloud region object without impact AAI's schema. How and when to use this property is up to each multicloud
plugin respectively. This property can be populated by ONAP users through ESR VIM registration GUI Portal (the input field label: "Cloud Extra Info"). The best practice to utilize this "cloud-extra-info" property is that ONAP users to input format json string, with
which extra configuration data can be serialized as {"key":"value"} into this json string. And the corresponding MultiCloud plugin decode and utilize the input key-value pairs.
