..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

===============================================================
MultiCloud SDC Client Design for k8s and windRiver/Openstack
===============================================================
To support registering sdc client as a sidecar which makes user
the choice to freely configure such client, user could decide
what artifact needs download, where it plans to store, what's
the actions to do for the downloaded artifact etc. 

after OOM deploy it as a sidecar container which will be paired
with each multicloud plugin inside the same pod, with sharing the
volume to allow the multicloud plugin to acess the downloaded 
artifacts by sdc client.

Problems Statement
==================
the SDC artifacts is part of CSAR, which is downloaded by SDC client. 
Currently in Casablanca release, some components: SO, Policy, AAI
register as a SDC client, could download the concerned artifacts based 
on the Notification from SDC. 

There are two things need support in Dublin release in MultiCloud side:
1. support k8s. download k8s related artifacts from SDC directly and 
do specified postprocessing
2. support windriver/openstack plugin to download HEAT/HEAT_ENV related
artifacts from SDC and change the currently API interface between SO and 
mutlicloud to transfer the indication of these artifacts instead of  the 
whole content of HEAT/HEAT_ENV. then mutlicloud will do search, based on 
these indication from SO, within the local downloaded artifacts.

Proposed framework
=========================================================


SDC Reception Handler<----> Reception Handler<--> Distribution Main <---->Plugin Handler<---->multicloudPost
                                                        |
                                                        |
                                                    REST Server

refer to
https://wiki.onap.org/display/DW/Policy+Platform+-+SDC+Service+Distribution+Software+Architecture


Proposed alternative Solutions
==========================================================
leverage the Policy distribution framework by doing below change
1. modify the SDC Reception Handler to add it support to download resource level artifacts 
like HEAT/HEAT_ENV and K8S_CLOUD

2. change the SDC client configuration interface by adding downloadPath and PostAPI 

3. add a multicloudPost into plugin Handler of the framework  which will do the postAPI 
on the downloaded artifact which will be put into the downloadPath. the logic and interface 
maybe different for k8s and openstack/windriver, like the parameters to be transferred.

**With respect to k8s artifact**:
the requried input data format for k8s API, suppose resource level too, like vnfid, UUID

**With respect to openStack/windRiver artifact**
the requried input data format for openStack/windRiver API for resource level.
once there are more than once artifacts type concered like HEAT and HEAT_ENV type, the donwload url 
are different like below:
HEAT_ENV:
"/sdc/v1/catalog/services/17e8291d-e7c1-49df-9070-653756ef5bdd/resourceInstances/testpolicy10/artifacts/ca186ae2-3efa-40b6-b3b4-cdf0314f1337"
HEAT:
"/sdc/v1/catalog/services/17e8291d-e7c1-49df-9070-653756ef5bdd/resourceInstances/testpolicy10/artifacts/06a817ed-ea23-4fff-a98a-f233aa787c44"

the 17e8291d-e7c1-49df-9070-653756ef5bdd is uuid of service
the ca186ae2-3efa-40b6-b3b4-cdf0314f1337 is the artifactUUID of resource level---testpolicy10, type is HEAT_ENV
the 06a817ed-ea23-4fff-a98a-f233aa787c44 is the artifactUUID of resource level---testpolicy10, type is HEAT

then how to info these info to multicloud API?

Dependency
==============
1. SDC support: 
   SDC-2041 SDC supports K8S plugin to expose APIs to add/delete cloud specific artifacts
   SDC-2045 create User and Password for Multicloud component to access secure api   
   A CSAR example including k8s artifact
2. SO support:
   modify the currently interface between SO and Mutlicloud:
https://onap.readthedocs.io/en/casablanca/submodules/multicloud/framework.git/docs/MultiCloud-APIv1-Specification.html#infrastructure-workload-lcm
   add UUID of resource instead of whole content 
   remove the heat and heat_env part from template_data
3. MutliCloud support:
   implement the postAPI for downloaded artifact for HEAT/HEAT_ENV/k8s, clarify all the necessnary information needed.
4. OOM support:
   need add a sidecar configuration for necessnary pods during deployment
   need define how to the common setting instead of hard-code

Test Use Cases
==============
1. For k8s. the artifacts is Helm chart. need a k8s lab env for validation. need clarify if there are some connection
between the VNFs, like using VirtualLink or just a service which is a simple wrap of one vnf. 
2. For openstack/windriver, use vFW test case with HEAT/HEAT_ENV artifacts.




