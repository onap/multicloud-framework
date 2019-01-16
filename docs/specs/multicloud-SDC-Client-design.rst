..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

===============================================================
MultiCloud SDC Client Design for k8s and windRiver/Openstack
===============================================================
To support  multicloud plugin access the concerned artifacts, do prepration
if necessnary, need registering the artifact management as a sdc client and
use shared folder method so that enable user to freely access the artifacts.
by configuring such client, user could decide what artifact needs download,
what's the actions to do for the downloaded artifact etc.

after OOM deploy such artifact management, the plugins could access all the
downloaded artifacts by shared folder method if necessnary.

Problems Statement
==================
the SDC artifacts is part of CSAR, which is downloaded by SDC client.
Currently in Casablanca release, some components: SO, Policy, AAI
register as a SDC client, could download the concerned artifacts based
on the Notification from SDC

There are two things need support in Dublin release in MultiCloud side:
1. support k8s. download k8s related artifacts from SDC, and do specified
postprocessing during design-time, artifacts could be used/got during
instantiation time

2. support WindRiver/OpenStack plugin to download HEAT/HEAT_ENV related
artifacts from SDC and change the currently API interface between SO and
MutliCloud to transfer the indication of these artifacts instead of  the
whole content of HEAT/HEAT_ENV. then MutliCloud use the indications from SO,
to find the downloaded artifacts during instantiation time


Proposed framework
=========================================================


SDC Reception Handler<----> Reception Handler<-->Artifact Management<---->Plugin Handler<---->multicloudPost
refer to `SDC Service Architecture
<https://wiki.onap.org/display/DW/Policy+Platform+-+SDC+Service+Distribution+Software+Architecture>`_

Proposed alternative Solutions
==========================================================
There would be a artifact management comopnent, which will do below steps once get the
notification from SDC during design time.

1.mandatory step to download concerned artifacts from SDC directly store
it locally by specified rule of layout

2. check if subplugin is configurated in the configuration file
a. configurated: it will invoke the pre-configurated Post API once the concerned
artifact has been downloaded. current k8s plugin need leverage such function
b. not configurated: then its plugin's duty to access the artifacts by shared folder
method and parse the metadata information.


leverage the Policy distribution framework by doing below change:

1.modify the SDC Reception Handler to add its support to download resource level artifacts
like HEAT/HEAT_ENV and K8S_CLOUD.

2.change the SDC client configuration interface by storing the artifact into Database

3.add a k8s into plugin Handler of the framework which will do the post API
on the downloaded artifact which will be put into the locally

**With respect to k8s artifact**:

the required input data format for k8s API, suppose resource level too, like VnfId, UUID

**With respect to openStack/windRiver artifact**:

the requried input data format for openStack/windRiver API for resource level, it will use
"vf-module-model-customization-id" as the key to directive plugins


Dependency
==============
1. SDC support:
   SDC-2041 SDC supports K8S plugin to expose APIs to add/delete cloud specific artifacts
   SDC-2045 create User and Password for MultiCloud component to access secure API
   A CSAR example including k8s artifact
2. SO support:
   modify the current interface between SO and Mutlicloud
3. MutliCloud support:
   implement the invoke logic  for the downloaded artifact conconered by k8s, clarify all the necessary information needed.
4. OOM support:
   need a configuration for necessary pods during deployment
   need to define how to the common setting instead of hard-code

Test Use Cases
==============
1. For k8s. the artifacts are Helm chart. need a k8s lab env for validation. need to clarify if there is some connection
between the VNFs, like using VirtualLink or just a service which is a simple wrap of one VNF

2. For OpenStack/WindRiver, use vFW test case with HEAT/HEAT_ENV artifacts.
