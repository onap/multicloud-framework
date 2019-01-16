..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

===============================================================
MultiCloud SDC Client Design for k8s and windRiver/Openstack
===============================================================
To support  multicloud plugin access the concerned artifacts, do prepration
if necessnary, need registering the artifact management as a sdc client and
add DB support so that enable user to freely configure such client, user
could decide what artifact needs download, what's the actions to do for the
downloaded artifact etc.

after OOM deploy such artifact management which includes  SDC client
and writing/deleting into DB. then plugins ONLY do reading the artifacts
at instantiation time.

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
whole content of HEAT/HEAT_ENV. then MutliCloud will do a search, based on these
indications from SO, to find the downloaded artifacts during instantiation time


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
it into DB by moving the DB logic in k8s plugin to SDC client

2.optional step to invoke the pre-configurated Post API once the concerned
artifact has been downloaded. current k8s plugin need leverage such function

then it will provide interface let plugins do reading the artifacts at instantiation time.

leverage the Policy distribution framework by doing below change:

1.modify the SDC Reception Handler to add its support to download resource level artifacts
like HEAT/HEAT_ENV and K8S_CLOUD.

2.change the SDC client configuration interface by storing the artifact into Database

3.add a multicloudPost into plugin Handler of the framework which will do the post API
on the downloaded artifact which will be put into the DB.

**With respect to k8s artifact**:

the required input data format for k8s API, suppose resource level too, like VnfId, UUID

**With respect to openStack/windRiver artifact**:

the requried input data format for openStack/windRiver API for resource level.
once there are more than once artifacts type concered like HEAT and HEAT_ENV type, the donwload url
are different like below:

HEAT_ENV:"/sdc/v1/catalog/services/17e8291d-e7c1-49df-9070-653756ef5bdd/resourceInstances/testpolicy10/artifacts/ca186ae2-3efa-40b6-b3b4-cdf0314f1337"

HEAT:
"/sdc/v1/catalog/services/17e8291d-e7c1-49df-9070-653756ef5bdd/resourceInstances/testpolicy10/artifacts/06a817ed-ea23-4fff-a98a-f233aa787c44"

the 17e8291d-e7c1-49df-9070-653756ef5bdd is uuid of service

the ca186ae2-3efa-40b6-b3b4-cdf0314f1337 is the artifactUUID of resource level---testpolicy10, type is HEAT_ENV

the 06a817ed-ea23-4fff-a98a-f233aa787c44 is the artifactUUID of resource level---testpolicy10, type is HEAT

then how to info this info to MultiCloud API?

Dependency
==============
1. SDC support:
   SDC-2041 SDC supports K8S plugin to expose APIs to add/delete cloud specific artifacts
   SDC-2045 create User and Password for MultiCloud component to access secure API
   A CSAR example including k8s artifact
2. SO support:
   modify the current interface between SO and Mutlicloud
   add UUID of the resource instead of the whole content
   remove the heat and heat_env part from template_data
3. MutliCloud support:
   implement the post API for the downloaded artifact for k8s, clarify all the necessary information needed.
4. OOM support:
   need a configuration for necessary pods during deployment
   need to define how to the common setting instead of hard-code

Test Use Cases
==============
1. For k8s. the artifacts are Helm chart. need a k8s lab env for validation. need to clarify if there is some connection
between the VNFs, like using VirtualLink or just a service which is a simple wrap of one VNF

2. For OpenStack/WindRiver, use vFW test case with HEAT/HEAT_ENV artifacts.
