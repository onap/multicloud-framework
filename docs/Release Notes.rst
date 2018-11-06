..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============

Version: 1.2.2
--------------

:Release Date: TBD

**New Features**

* Upgarde Northbound API to v1 which support `Consistent ID of a Cloud Region`
* Add new Generic API to offload Infrastructure's workload LCM from SO to MutliCloud
* Updates the plugin for Wind River to support Titanium Cloud R5
* Update the plugin for VIO to support VIO 5.0
* Add a plugin to support OpenStack Pike
* Add a plugin to support Azure cloud which is PoC yet.
* Add a plugin to support kubernetes which is PoC yet.


**MultiCloud Plugin for Wind River Titanium Cloud**

* Expands the HPA discovery and registration to cover SR-IOV NICs.
* Decouples AAI's cloud-region-id from OpenStack Region ID
* Automates the on-boarding multiple OpenStack instances leveraging OpenStack multi-region feature.
* Automates the decommission of a Cloud Region
* Automates the updating AAI with heat stack resources
* Enables Server Operations API for Auto-Healing


**MultiCloud Plugin for OpenStack**

* Expanding the HPA discovery and registration to cover SR-IOV NICs.
* Enabled Server Operations API for Auto-Healing


**MultiCloud Plugin for VIO**

* Support Cloud Agnostic Placement Policies in VIO plugin
* Enabled Server Operations API for Auto-Healing
* Enables marker support on logging


**MultiCloud Plugin for Azure**


**MultiCloud Plugin for Kubernetes**


**Bug Fixes**


**Known Issues**

- `MULTICLOUD-253 <https://jira.onap.org/browse/MULTICLOUD-353>`_
  OPENO servers API: meta_data is generated in wrong type

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  OPENO images API: image creating API cannot handle large image file

- `MULTICLOUD-386 <https://jira.onap.org/browse/MULTICLOUD-386>`_
  OPENO identity API: identity API cannot work with keystone endpoint v2.0

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  OPENO servers API: keypair cannot be passed for nova instance creation

- `MULTICLOUD-390 <https://jira.onap.org/browse/MULTICLOUD-390>`_
  OPENO servers API: pass userdata without contextArray, then "user_data"
  is not being passed to nova instance API.

**Security Notes**

MULTICLOUD code has been formally scanned during build time using NexusIQ and no Critical vulnerability were found.

Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

**Upgrade Notes**

None

**Deprecation Notes**

* The maintainance with regarding to MultiCloud plugin for OpenStack Newton has been stopped from Casablanca Release.

**Other**

None


Version: 1.1.2
--------------

:Release Date: 2018-06-07


**New Features**

* Allow to check capacity capability for smart VNF placement across VIMs.
* Declarative template driven framework to generate API dynamically.
* Federate the events of VIM layer with ONAP message bus which provide direct help to HA fencing and improve the
  efficiency of VM recover with performance verification.
* Enable basic HPA discovery and representing at Multi VIM/Cloud when registry.
* Enable distributed log collection mechanism to a centralized logging analysis system.
* Improve parallelism of Multi VIM/Cloud service framework with performance verification.
* Upload and download images based on Cloud storage capabilities to support remote image distribution requirement.

**Bug Fixes**

- `MULTICLOUD-225 <https://jira.onap.org/browse/MULTICLOUD-225>`_
  Allow to forward header properties through Multi VIM/Cloud framework

- `MULTICLOUD-221 <https://jira.onap.org/browse/MULTICLOUD-221>`_
  Fix VESAgent health check flow

- `MULTICLOUD-220 <https://jira.onap.org/browse/MULTICLOUD-220>`_
  Fix Multi VIM/Cloud plugins to enable ID binding with each request.


**Known Issues**

- `MULTICLOUD-242 <https://jira.onap.org/browse/MULTICLOUD-242>`_
  One known issue is that the Ocata image is not put into the consistent place as R1 and please attention to the
  download path when you choose manual installation of Ocata plugin from the image pool.

**Security Notes**

MULTICLOUD code has been formally scanned during build time using NexusIQ and no Critical vulnerability were found.

Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

**Upgrade Notes**

None

**Deprecation Notes**

None

**Other**

None

Version: 1.0.0
--------------

:Release Date: 2017-11-16


**New Features**

* Keystone proxy for convenient integration with modules which depend on original OpenStack functions
* Multiple VIM registry and unregister
* Resources LCM functions
* Auto-deployment support to both K8s and heat
* Hierarchical binding based integration with the third party SDN controller
* Basic Fcaps alert collection support, VM abnormal status is thrown out as an example
* Fake cloud based Unit and system test framework
* Complete code coverage detection, CSIT, and document framework
* Provide several plugins of different backbends, including: Vanilla OpenStack (based on Ocata) and commercial Clouds including OpenStack (including Titanium - Mitaka from Wind River and VIO - Ocata from VMware)

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
