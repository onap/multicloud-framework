..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============

Version: 5.0.1 (El Alto Release)
-----------------------------------

:Release Date: 2019-10-24

**New Features**

* Rebase to Python 3


**The MultiCloud services consists of following components:**

**MultiCloud Broker (version: 1.4.2)**

* Rebase to Python 3
* Update django version to 2.2.3


**MultiCloud ArtifactBroker (version: 1.4.2)**

* None


**MultiCloud FCAPS (version: 1.4.1)**

* Rebase to Python 3
* Update django version to 2.2.3


**MultiCloud Plugin for Wind River Titanium Cloud (version: 1.4.1)**

* Rebase to Python 3
* Update django version to 2.2.3

**MultiCloud Plugin for StarlingX (version: 1.4.1)**

* Rebase to Python 3
* Update django version to 2.2.3


**MultiCloud Plugin for OpenStack versions (version: 1.3.4)**

* Rebase to Python 3 for pike plugin
* Update django version to 2.2.3

**MultiCloud Plugin for VIO (version 1.4.1)**

* Rebase to Python 3
* Update django version


**MultiCloud Plugin for Azure (version 1.2.4)**

* None

**MultiCloud Plugin for Kubernetes (version: 0.5.0)**

* Add support for Network Operator
* Update Kubespray and Kube version in KuD
* Use Multus DaemonSet for installing Multus in KuD
* Use NFD DaemonSet to integrate Node Feature discovery in KuD

**Bug Fixes**


- `MULTICLOUD-644 <https://jira.onap.org/browse/MULTICLOUD-644>`_
  MultiCloud k8s: KUD - Go version is not correct

- `MULTICLOUD-684 <https://jira.onap.org/browse/MULTICLOUD-684>`_
  MultiCloud k8s: KUD - OVN Network Operator

- `MULTICLOUD-663 <https://jira.onap.org/browse/MULTICLOUD-663>`_
  MultiCloud k8s: Multus error when the namesapace is not default

- `MULTICLOUD-681 <https://jira.onap.org/browse/MULTICLOUD-681>`_
  MultiCloud k8s: Use Multus DaemonSet for installing Multus in KuD

- `MULTICLOUD-772 <https://jira.onap.org/browse/MULTICLOUD-772>`_
  MultiCloud k8s: Update the version of Kubespray in KUD

- `MULTICLOUD-797 <https://jira.onap.org/browse/MULTICLOUD-797>`_
  MultiCloud k8s: Use NFD DaemonSet for integrating Node Feature discovery in KuD

- `MULTICLOUD-614 <https://jira.onap.org/browse/MULTICLOUD-614>`_
  MultiCloud k8s: Create KubeConfigDir if it does not exist

- `MULTICLOUD-662 <https://jira.onap.org/browse/MULTICLOUD-662>`_
  MultiCloud k8s: Add Find/Get method to get instance for a particular profile

- `MULTICLOUD-574 <https://jira.onap.org/browse/MULTICLOUD-574>`_
  MultiCloud k8s: Use defferedrestmapper instead of the normal one

- `MULTICLOUD-557 <https://jira.onap.org/browse/MULTICLOUD-557>`_
  MultiCloud k8s: Kubernetes kind plugins should implement an interface

- `MULTICLOUD-835 <https://jira.onap.org/browse/MULTICLOUD-835>`_
  MultiCloud Framework: Cannot Register cloud region with multicloud via REST

**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  MultiCloud OpenStack: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  MultiCloud OpenStack: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  MultiCloud OpenStack: API request to multicloud with authorization header will be rejected

- `MULTICLOUD-661 <https://jira.onap.org/browse/MULTICLOUD-661>`_
  MultiCloud k8s: OVN Installation issues

- `MULTICLOUD-601 <https://jira.onap.org/browse/MULTICLOUD-601>`_
  MultiCloud k8s: move to sigs yaml from ghodss

- `MULTICLOUD-602 <https://jira.onap.org/browse/MULTICLOUD-602>`_
  MultiCloud WindRiver: Error when registering a cloud after deleting it

- `MULTICLOUD-846 <https://jira.onap.org/browse/MULTICLOUD-846>`_
  MultiCloud Pike: Query stack by infra_workload API returns false status

**Security Notes**

*Fixed Security Issues*


*Known Security Issues*


*Known Vulnerabilities in Used Modules*


MULTICLOUD code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain open have
been assessed for risk and determined to be false positive.

The MULTICLOUD open Critical security vulnerabilities and their risk
assessment have been documented as part of the
`project <https://wiki.onap.org/pages/viewpage.action?pageId=68541501>`_.


Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

  - `Project Vulnerability Review Table for Multicloud <https://wiki.onap.org/pages/viewpage.action?pageId=68541501>`_

  - `Multicloud K8s Plugin Service APIs <https://wiki.onap.org/display/DW/MultiCloud+K8s-Plugin-service+API's>`_

**Upgrade Notes**

None

**Deprecation Notes**

* The maintenance with regarding to MultiCloud plugin for OpenStack Newton
  has been stopped from Casablanca Release.
* The maintenance with regarding to MultiCloud plugin for OpenStack Ocata
  has been stopped from El Alto Release.
* The maintenance with regarding to MultiCloud plugin for OpenStack Lenovo
  has been stopped from El Alto Release.

**Other**

None


Version: 4.0.0 (Dublin Release)
-----------------------------------

:Release Date: 2019-06-10

**New Features**

* Upgraded the Generic API to offload Infrastructure's workload LCM from SO to
  MutliCloud
* Upgraded the Capacity Check API to support F-GPS for OOF
* Enhanced the security by enabling secured communication and run as
  non-root user
* Enhanced the multicloud NBI to support multi-tenant by new header field
* Minimized docker image footprint by rebasing images to Alpine
* Refactored and enhance the MultiCloud OpenStack VES agent service to a
  standalone service as multicloud-fcaps
* Updated the plugin for Wind River Titanium Cloud to realize the enhanced
  the Generic API
* Added plugin for Kubernetes based cloud regions which supports deployment
  via Helm Charts
* Added artifactbroker as a SDC client to retrieve VNF artifacts for Multicloud
  plugins services
* Added plugin for StarlingX
* Added plugin for ThinkCloud


**The MultiCloud services consists of following components:**

**MultiCloud Broker (version: 1.3.3)**

* Extended infra_workload API for better integration of SO and MultiCloud
* Extended check_vim_capacity API to check capacity at AZ level
* Added optional header field "Project" to support multi-tenants
* Added plugin type for k8s and starlingx
* Run as non-root user


**MultiCloud ArtifactBroker (version: 1.3.3)**

* Added artifactbroker service to retrieve VNF artifacts from SDC
* Deployed as a sidecar for MultiCloud Plugin services
* Run as non-root user


**MultiCloud FCAPS (version: 1.3.4)**

* Common service to support relay FCAPS data from OpenStack
* Rebased image to alpine in favor of Docker image footprint
* Enable HTTPS endpoints to realize secured communication requirement
* Run as non-root user


**MultiCloud Plugin for Wind River Titanium Cloud (version: 1.3.4)**

* Enhanced the infra_workload to realize the extended API requirements
* Enhanced the capacity_check API to check the capacity on AZ level
* Enhanced the API handler to accept new request Header "Project"
* Refactored the helper codes into separated thread.
* Rebased image to alpine in favor of Docker image footprint
* Enable HTTPS endpoints to realize secured communication requirement
* Move the vesagent functionality to MultiCloud FCAPS module
* Run as non-root user

**MultiCloud Plugin for StarlingX (version: 1.3.4)**

* Align to MultiCloud Plugin for Wind River
* Run as non-root user


**MultiCloud Plugin for OpenStack versions (version: 1.3.4)**

* Support OpenStack Ocata, Pike
* Rebased image to alpine in favor of Docker image footprint
* Enable HTTPS endpoints to realize secured communication requirement
* Run as non-root user

**MultiCloud Plugin for VIO (version 1.3.1)**

* Enable CCVPN DR API through extention.
* Enable multi architecture support for MultiCloud-VIO image.
* Enable vsphere operation support and vmdk validation.
* Enhanced the capacity_check API to check the capacity on AZ level.
* Enable HTTPS endpoints to realize secured communication requirement.
* Optimize image size and run as non-root user.


**MultiCloud Plugin for Azure (version 1.2.4)**

* Rebased image to alpine in favor of Docker image footprint
* Run as non-root user

**MultiCloud Plugin for Kubernetes (version: 0.4.0)**

* Create workloads in Kubernetes based cloud regions
* Provides REST api to upload Helm Charts as artifacts
* The Helm Charts can be customized via a Profile API before deployment
* Added a connectivity API that allows you to upload KubeConfig
  information that is then used to access/modify resources in a cluster
* Provides a Day 2 configuration API that allows modifying resources in
  a cluster
* Tested with Edgex Helm charts and vFirewall Helm charts


**Bug Fixes**

- `MULTICLOUD-605 <https://jira.onap.org/browse/MULTICLOUD-605>`_
  MultiCloud Plugin: Robot Heat Bridge fails to Multicloud due to the
  keystone client in Init Bridge is not getting the identity url
  back from MultiCloud.

- `MULTICLOUD-657 <https://jira.onap.org/browse/MULTICLOUD-657>`_
  MultiCloud WindRiver: VF-C cannot enumerate tenants list with API v1

- `MULTICLOUD-651 <https://jira.onap.org/browse/MULTICLOUD-651>`_
  MultiCloud artifactbroker: artifactbroker does not compose appropriate
  meta files for MultiCloud plugins

- `MULTICLOUD-653 <https://jira.onap.org/browse/MULTICLOUD-653>`_
  MultiCloud k8s: vFw Helm charts installs ok but not traffic seen on sink

- `MULTICLOUD-656 <https://jira.onap.org/browse/MULTICLOUD-656>`_
  MultiCloud WindRiver: MultiCloud WindRiver plugin cannot load VF Module
  artifacts which are fed by artifactbroker

- `MULTICLOUD-633 <https://jira.onap.org/browse/MULTICLOUD-633>`_
  MultiCloud Doc: Update the infra_workload API to reflect enhancement
  in Dublin

- `MULTICLOUD-584 <https://jira.onap.org/browse/MULTICLOUD-584>`_
  MultiCloud FCAPS: Multicloud-fcaps fails health check with 502

- `MULTICLOUD-627 <https://jira.onap.org/browse/MULTICLOUD-627>`_
  MultiCloud Azure: multicloud-azure docker image cannot boot up
  after rebasing to alpine

- `MULTICLOUD-611 <https://jira.onap.org/browse/MULTICLOUD-611>`_
  MultiCloud WindRiver: 500 resturn code for some infra_workload API calls

- `MULTICLOUD-603 <https://jira.onap.org/browse/MULTICLOUD-603>`_
  MultiCloud WindRiver: Error returns while querying workload-id after
  workload create

- `MULTICLOUD-588 <https://jira.onap.org/browse/MULTICLOUD-588>`_
  MultiCloud Broker: broker fails to boot up after rebasing to alpine

- `MULTICLOUD-477 <https://jira.onap.org/browse/MULTICLOUD-477>`_
  MultiCloud OpenStack: service URL definitions interfere with each other

- `MULTICLOUD-476 <https://jira.onap.org/browse/MULTICLOUD-476>`_
  MultiCloud Broker: POST fails on v1 interface

- `MULTICLOUD-478 <https://jira.onap.org/browse/MULTICLOUD-478>`_
  MultiCloud OpenStack: Handling of mapping from v3 keystone to v2 keystone
  is faulty

- `MULTICLOUD-479 <https://jira.onap.org/browse/MULTICLOUD-479>`_
  MultiCloud WindRiver: heatbridge_update not working correctly

- `MULTICLOUD-645 <https://jira.onap.org/browse/MULTICLOUD-645>`_
  MultiCloud k8s: Multicloud-k8s to SO responses don't match

- `MULTICLOUD-283 <https://jira.onap.org/browse/MULTICLOUD-283>`_
  MultiCloud Doc: API documentation : POST tokens is missing

- `MULTICLOUD-585 <https://jira.onap.org/browse/MULTICLOUD-585>`_
  MultiCloud k8s: CustomResourceDefinitions are not getting created

- `MULTICLOUD-595 <https://jira.onap.org/browse/MULTICLOUD-595>`_
  MultiCloud WindRiver: unexpected exception during registration without privilege

- `MULTICLOUD-582 <https://jira.onap.org/browse/MULTICLOUD-582>`_
  MultiCloud k8s: Fix error in CSIT setup.sh

- `MULTICLOUD-575 <https://jira.onap.org/browse/MULTICLOUD-575>`_
  MultiCloud k8s: k8s docker build is broken

- `MULTICLOUD-462 <https://jira.onap.org/browse/MULTICLOUD-462>`_
  MultiCloud k8s: Namespace should be created by k8splugin before resources
  are created in kubernetes

- `MULTICLOUD-483 <https://jira.onap.org/browse/MULTICLOUD-483>`_
  MultiCloud StarlingX: Starling-X healthcheck test is FAIL

- `MULTICLOUD-562 <https://jira.onap.org/browse/MULTICLOUD-562>`_
  MultiCloud k8s: Fix multicloud-k8s csit

- `MULTICLOUD-558 <https://jira.onap.org/browse/MULTICLOUD-558>`_
  MultiCloud k8s: Make profile keys explicit

- `MULTICLOUD-552 <https://jira.onap.org/browse/MULTICLOUD-552>`_
  MultiCloud OpenStack: HPA passthrough discovery is not right

- `MULTICLOUD-525 <https://jira.onap.org/browse/MULTICLOUD-525>`_
  MultiCloud k8s: chart name should not be mandatory

- `MULTICLOUD-439 <https://jira.onap.org/browse/MULTICLOUD-439>`_
  MultiCloud k8s: reflect.deepequal does not work in tests

- `MULTICLOUD-440 <https://jira.onap.org/browse/MULTICLOUD-440>`_
  MultiCloud k8s: Refactor definition_test code

- `MULTICLOUD-438 <https://jira.onap.org/browse/MULTICLOUD-438>`_
  MultiCloud k8s: definition upload calls db create in wrong order

- `MULTICLOUD-435 <https://jira.onap.org/browse/MULTICLOUD-435>`_
  MultiCloud k8s: Delete should not error out if there is no document found

- `MULTICLOUD-619 <https://jira.onap.org/browse/MULTICLOUD-619>`_
  MultiCloud k8s: System wide environment variables not sourced by default
  for tests

- `MULTICLOUD-607 <https://jira.onap.org/browse/MULTICLOUD-607>`_
  MultiCloud k8s: Wrong logic for pip installation/upgrade

- `MULTICLOUD-610 <https://jira.onap.org/browse/MULTICLOUD-610>`_
  MultiCloud k8s: kud installation fails with old golang version

- `MULTICLOUD-620 <https://jira.onap.org/browse/MULTICLOUD-620>`_
  MultiCloud k8s: aio.sh is not rerunnable

- `MULTICLOUD-643 <https://jira.onap.org/browse/MULTICLOUD-643>`_
  MultiCloud artifactbroker: gson parse issue for the list of VF_MODULE_ARTIFACT

- `MULTICLOUD-620 <https://jira.onap.org/browse/MULTICLOUD-620>`_
  MultiCloud k8s: aio.sh is not rerunnable

- `MULTICLOUD-620 <https://jira.onap.org/browse/MULTICLOUD-620>`_
  MultiCloud k8s: aio.sh is not rerunnable

**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  MultiCloud OpenStack: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  MultiCloud OpenStack: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  MultiCloud OpenStack: API request to multicloud with authorization header will be rejected

- `MULTICLOUD-644 <https://jira.onap.org/browse/MULTICLOUD-644>`_
  MultiCloud k8s: KUD - Go version is not correct

- `MULTICLOUD-663 <https://jira.onap.org/browse/MULTICLOUD-663>`_
  MultiCloud k8s: Multus error when the namesapace is not default

- `MULTICLOUD-614 <https://jira.onap.org/browse/MULTICLOUD-614>`_
  MultiCloud k8s: Create KubeConfigDir if it does not exist

- `MULTICLOUD-662 <https://jira.onap.org/browse/MULTICLOUD-662>`_
  MultiCloud k8s: Add Find/Get method to get instance for a particular profile

- `MULTICLOUD-661 <https://jira.onap.org/browse/MULTICLOUD-661>`_
  MultiCloud k8s: OVN Installation issues

- `MULTICLOUD-574 <https://jira.onap.org/browse/MULTICLOUD-574>`_
  MultiCloud k8s: Use defferedrestmapper instead of the normal one

- `MULTICLOUD-601 <https://jira.onap.org/browse/MULTICLOUD-601>`_
  MultiCloud k8s: move to sigs yaml from ghodss

- `MULTICLOUD-602 <https://jira.onap.org/browse/MULTICLOUD-602>`_
  MultiCloud WindRiver: Error when registering a cloud after deleting it


**Security Notes**

*Fixed Security Issues*

- `OJSI-130 <https://jira.onap.org/browse/OJSI-130>`_
  In default deployment MULTICLOUD (multicloud-azure) exposes HTTP port 30261 outside of cluster.

- `OJSI-148 <https://jira.onap.org/browse/OJSI-148>`_
  In default deployment MULTICLOUD (multicloud) exposes HTTP port 30291 outside of cluster.

- `OJSI-150 <https://jira.onap.org/browse/OJSI-150>`_
  In default deployment MULTICLOUD (multicloud-ocata) exposes HTTP port 30293 outside of cluster.

- `OJSI-151 <https://jira.onap.org/browse/OJSI-151>`_
  In default deployment MULTICLOUD (multicloud-windriver) exposes HTTP port 30294 outside of cluster.

- `OJSI-153 <https://jira.onap.org/browse/OJSI-153>`_
  In default deployment MULTICLOUD (multicloud-pike) exposes HTTP port 30296 outside of cluster.

- `OJSI-149 <https://jira.onap.org/browse/OJSI-149>`_
  In default deployment MULTICLOUD (multicloud-vio) exposes HTTP port 30292 outside of cluster.


*Known Security Issues*


*Known Vulnerabilities in Used Modules*


MULTICLOUD code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain open have
been assessed for risk and determined to be false positive.

The MULTICLOUD open Critical security vulnerabilities and their risk
assessment have been documented as part of the
`project <https://wiki.onap.org/pages/viewpage.action?pageId=64004594>`_.


Quick Links:
  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

  - `Project Vulnerability Review Table for Multicloud <https://wiki.onap.org/pages/viewpage.action?pageId=64004594>`_

  - `Multicloud K8s Plugin Service APIs <https://wiki.onap.org/display/DW/MultiCloud+K8s-Plugin-service+API's>`_

**Upgrade Notes**

None

**Deprecation Notes**

* The maintenance with regarding to MultiCloud plugin for OpenStack Newton
  has been stopped from Casablanca Release.

**Other**

None


Version: 3.0.1 (Casablanca Maintenance Release)
-----------------------------------------------

:Release Date: January 31st, 2019


**New Features**

None

**The MultiCloud services in this release consist of following components:**

- MultiCloud Broker (version: 1.2.2)

- MultiCloud Plugin for Wind River Titanium Cloud (version: 1.2.4)

- MultiCloud Plugin for OpenStack Ocata (version: 1.2.4)

- MultiCloud Plugin for OpenStack Pike (version: 1.2.4)

- MultiCloud Plugin for VIO (version 1.2.2)

- MultiCloud Plugin for Azure (version 1.2.2)


**Bug Fixes**

- `MULTICLOUD-253 <https://jira.onap.org/browse/MULTICLOUD-253>`_
  meta_data initialized with wrong type

- `MULTICLOUD-386 <https://jira.onap.org/browse/MULTICLOUD-386>`_
  Multicloud Fails with Keystone v2.0

- `MULTICLOUD-390 <https://jira.onap.org/browse/MULTICLOUD-390>`_
  Cloud userdata is not passed to openstack nova instance.

- `MULTICLOUD-391 <https://jira.onap.org/browse/MULTICLOUD-391>`_
  Remove non-standard disclaimer from license files

- `MULTICLOUD-404 <https://jira.onap.org/browse/MULTICLOUD-404>`_
  multicloud return value is inconsistent between plugin and broker

- `MULTICLOUD-405 <https://jira.onap.org/browse/MULTICLOUD-405>`_
  multicloud metadata format is incorrect

- `MULTICLOUD-407 <https://jira.onap.org/browse/MULTICLOUD-407>`_
  multicloud does not pass userdata to openstack

- `MULTICLOUD-412 <https://jira.onap.org/browse/MULTICLOUD-412>`_
  Discover snapshots get error

- `MULTICLOUD-414 <https://jira.onap.org/browse/MULTICLOUD-414>`_
  Fix reboot vm problem

- `MULTICLOUD-415 <https://jira.onap.org/browse/MULTICLOUD-415>`_
  multicloud ocata and pike cannot discover VIM resources

- `MULTICLOUD-423 <https://jira.onap.org/browse/MULTICLOUD-423>`_
  multicloud DELETE without a stack-id still deletes a stack

- `MULTICLOUD-431 <https://jira.onap.org/browse/MULTICLOUD-431>`_
  Multicloud registration error with image version 1.2.2

- `MULTICLOUD-456 <https://jira.onap.org/browse/MULTICLOUD-456>`_
  Multicloud Infra_workload API performance issue with image version 1.2.3



**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  OPENO images API: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  OPENO servers API: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  API request to multicloud with authorization header will be rejected


**Security Notes**

*Fixed Security Issues*


*Known Security Issues*

- `OJSI-130 <https://jira.onap.org/browse/OJSI-130>`_
  In default deployment MULTICLOUD (multicloud-azure) exposes HTTP port 30261 outside of cluster.

- `OJSI-148 <https://jira.onap.org/browse/OJSI-148>`_
  In default deployment MULTICLOUD (multicloud) exposes HTTP port 30291 outside of cluster.

- `OJSI-150 <https://jira.onap.org/browse/OJSI-150>`_
  In default deployment MULTICLOUD (multicloud-ocata) exposes HTTP port 30293 outside of cluster.

- `OJSI-151 <https://jira.onap.org/browse/OJSI-151>`_
  In default deployment MULTICLOUD (multicloud-windriver) exposes HTTP port 30294 outside of cluster.

- `OJSI-153 <https://jira.onap.org/browse/OJSI-153>`_
  In default deployment MULTICLOUD (multicloud-pike) exposes HTTP port 30296 outside of cluster.

- `OJSI-149 <https://jira.onap.org/browse/OJSI-149>`_
  In default deployment MULTICLOUD (multicloud-vio) exposes HTTP port 30292 outside of cluster.


*Known Vulnerabilities in Used Modules*


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

The latest release tag 1.2.4 for OpenStack plugins is not part of OOM chart in
Casablanca Maintenance Release yet. Please update the OOM chart manually
to upgrade the docker images with version tag 1.2.4 to fix bug: MULTICLOUD-456


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
