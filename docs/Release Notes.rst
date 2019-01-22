..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============


Version: 3.0.1 (Casablanca Maintenance Release)
-----------------------------------------------

:Release Date: January 31st, 2019


**New Features**

None

**The MultiCloud services in this release consist of following components:**

- MultiCloud Broker (version: 1.2.2)

- MultiCloud Plugin for Wind River Titanium Cloud (version: 1.2.3)

- MultiCloud Plugin for OpenStack Ocata (version: 1.2.3)

- MultiCloud Plugin for OpenStack Pike (version: 1.2.3)

- MultiCloud Plugin for VIO (version 1.2.2)

- MultiCloud Plugin for Azure (version 1.2.2)


**Bug Fixes**

- `MULTICLOUD-431 <https://jira.onap.org/browse/MULTICLOUD-431>`_
  Multicloud registration error with image version 1.2.2

- `MULTICLOUD-423 <https://jira.onap.org/browse/MULTICLOUD-423>`_
  multicloud DELETE without a stack-id still deletes a stack


**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  OPENO images API: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  OPENO servers API: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  API request to multicloud with authorization header will be rejected


**Security Notes**

MULTICLOUD code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain open have
been assessed for risk and determined to be false positive.

The MULTICLOUD open Critical security vulnerabilities and their risk
assessment have been documented as part of the
`Multi-VIM/Cloud <https://wiki.onap.org/pages/viewpage.action?pageId=45310604>`_.


Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

  - `Project Vulnerability Review Table for Multicloud Casablanca Maintenance Release <https://wiki.onap.org/pages/viewpage.action?pageId=45310604>`_

**Upgrade Notes**

None

**Deprecation Notes**

* The maintenance with regarding to MultiCloud plugin for OpenStack Newton
  has been stopped from Casablanca Release.

**Other**

None


Version: 3.0.0 (Casablanca Release)
-----------------------------------

:Release Date: 2018-11-30

**New Features**

* Enriched the documentaton with Architecture descriptions
* Verified the supports to end to end vCPE TOSCA VNF use case
* Upgraded to Northbound API v1 to support `Consistent ID of a Cloud Region`
* Added new Generic API to offload Infrastructure's workload LCM from SO to
  MutliCloud
* Updated the plugin for Wind River to support Titanium Cloud R5
* Updated the plugin for VIO to support VIO 5.0
* Added a plugin to support OpenStack Pike
* Released Azure's plugin seed code
* Released Kubernetes' plugin seed code


**The MultiCloud services consists of following components:**

**MultiCloud Broker (version: 1.2.2)**

* Added plugin type for azure and pike
* Added API v1 to align to `Consistent ID of a Cloud Region`
* Added API infra_workload to enable SO and MultiCloud Integration

**MultiCloud Plugin for Wind River Titanium Cloud (version: 1.2.2)**

* Expanded the HPA discovery and registration to cover SR-IOV NICs.
* Decoupled AAI's cloud-region-id from OpenStack Region ID
* Automated the on-boarding multiple OpenStack instances leveraging OpenStack
  multi-region feature.
* Enabled the on-boarding of subclouds of Titanium Cloud in Distributed Cloud
  Mode
* Automated the decommission of a Cloud Region
* Automated the updating AAI with heat stack resources
* Enabled Server Operations API for Auto-Healing
* Cached the AAI cloud region data to improve the API handling performance
* Passed the vCPE TOSCA VNF use case with several critical issues fixed
* Fixed the keystone v2.0 endpoint issue

**MultiCloud Plugin for OpenStack (version: 1.2.2)**

* Expanded the HPA discovery and registration to cover SR-IOV NICs.
* Decoupled AAI's cloud-region-id from OpenStack Region ID
* Enabled Server Operations API for Auto-Healing
* Cached the AAI cloud region data to improve the API handling performance
* Passed the vCPE TOSCA VNF use case with several critical issues fixed
* Fixed the keystone v2.0 endpoint issue


**MultiCloud Plugin for VIO (version 1.2.2)**

* Expanded the HPA discovery and registration to cover SR-IOV NICs.
* Decoupled AAI's cloud-region-id from OpenStack Region ID
* Automated the on-boarding multiple OpenStack instances leveraging OpenStack
  multi-region feature.
* Automated the decommission of a Cloud Region
* Supported Cloud Agnostic Placement Policies in VIO plugin
* Enabled Server Operations API for Auto-Healing
* Enabled marker support on logging


**MultiCloud Plugin for Azure (version 1.2.2)**

* Released inital seed code
* Enabled flavor discovery during on-boarding of azure cloud
* Supported for OOB vFW and vDNS use cases using the plugin

**MultiCloud Plugin for Kubernetes (version: N/A)**

* Released initial seed code
* Supported Service, Deployment and Namespace Kubernetes objects for this
  initial phase
* Provided functional tests for ensuring its correct operation using an
  emulated ONAP interaction
* Included a vagrant project for provisioning a Kubernetes deployment


**Bug Fixes**

- `MULTICLOUD-253 <https://jira.onap.org/browse/MULTICLOUD-253>`_
  OPENO servers API: meta_data is generated in wrong type

- `MULTICLOUD-386 <https://jira.onap.org/browse/MULTICLOUD-386>`_
  OPENO identity API: identity API cannot work with keystone endpoint v2.0

- `MULTICLOUD-390 <https://jira.onap.org/browse/MULTICLOUD-390>`_
  OPENO servers API: pass userdata without contextArray, then "user_data"
  is not being passed to nova instance API.

**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  OPENO images API: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  OPENO servers API: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  API request to multicloud with authorization header will be rejected



**Security Notes**

MULTICLOUD code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain open have
been assessed for risk and determined to be false positive.

The MULTICLOUD open Critical security vulnerabilities and their risk
assessment have been documented as part of the
`project <https://wiki.onap.org/pages/viewpage.action?pageId=43386067>`_.


Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_
  
  - `Project Vulnerability Review Table for Multicloud <https://wiki.onap.org/pages/viewpage.action?pageId=43386067>`_

**Upgrade Notes**

None

**Deprecation Notes**

* The maintenance with regarding to MultiCloud plugin for OpenStack Newton
  has been stopped from Casablanca Release.

**Other**

None


Version: 2.0.0 (Beijing Release)
--------------------------------

:Release Date: 2018-06-07


**New Features**

* Allow to check capacity capability for smart VNF placement across VIMs.
* Declarative template driven framework to generate API dynamically.
* Federate the events of VIM layer with ONAP message bus which provide direct
  help to HA fencing and improve the
  efficiency of VM recover with performance verification.
* Enable basic HPA discovery and representing at Multi VIM/Cloud when registry.
* Enable distributed log collection mechanism to a centralized logging
  analysis system.
* Improve parallelism of Multi VIM/Cloud service framework with performance
  verification.
* Upload and download images based on Cloud storage capabilities to support
  remote image distribution requirement.

**Bug Fixes**

- `MULTICLOUD-225 <https://jira.onap.org/browse/MULTICLOUD-225>`_
  Allow to forward header properties through Multi VIM/Cloud framework

- `MULTICLOUD-221 <https://jira.onap.org/browse/MULTICLOUD-221>`_
  Fix VESAgent health check flow

- `MULTICLOUD-220 <https://jira.onap.org/browse/MULTICLOUD-220>`_
  Fix Multi VIM/Cloud plugins to enable ID binding with each request.


**Known Issues**

- `MULTICLOUD-242 <https://jira.onap.org/browse/MULTICLOUD-242>`_
  One known issue is that the Ocata image is not put into the consistent place
  as R1 and please attention to the
  download path when you choose manual installation of Ocata plugin from the
  image pool.

**Security Notes**

MULTICLOUD code has been formally scanned during build time using NexusIQ and
no Critical vulnerability were found.

Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

**Upgrade Notes**

None

**Deprecation Notes**

None

**Other**

None

Version: 1.0.0 (Amsterdam Release)
----------------------------------

:Release Date: 2017-11-16


**New Features**

* Keystone proxy for convenient integration with modules which depend on
  original OpenStack functions
* Multiple VIM registry and unregister
* Resources LCM functions
* Auto-deployment support to both K8s and heat
* Hierarchical binding based integration with the third party SDN controller
* Basic Fcaps alert collection support, VM abnormal status is thrown out as
  an example
* Fake cloud based Unit and system test framework
* Complete code coverage detection, CSIT, and document framework
* Provide several plugins of different backbends, including: Vanilla OpenStack
  (based on Ocata) and commercial Clouds including OpenStack (including
  Titanium - Mitaka from Wind River and VIO - Ocata from VMware)

**Bug Fixes**

- `MULTICLOUD-123 <https://jira.onap.org/browse/MULTICLOUD-123>`_
  Append v3 to keystone url by default, if keystone version is missing.

- `MULTICLOUD-102 <https://jira.onap.org/browse/MULTICLOUD-102>`_
  Throw exception in Multi Cloud when backend OpenStack throw exceptions.

- `MULTICLOUD-101 <https://jira.onap.org/browse/MULTICLOUD-101>`_
  Fix failed to add image info to AAI if image name didn't contain '-'.


**Known Issues**

None

**Security Issues**

None

**Upgrade Notes**

None

**Deprecation Notes**

None

**Other**

None

===========

End of Release Notes
