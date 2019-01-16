.. This work is licensed under a Creative Commons Attribution 4.0 International License.

===============================================================
MultiCloud SDC Client Design for k8s and windRiver/Openstack
===============================================================
To support  multicloud plugin access the concerned artifacts, do prepration
if necessnary, need registering the artifact management as a sdc client and
use shared folder method so that enable user to freely access the artifacts.
by configuring such client, user could decide what artifact needs download,
what's the actions to do for the downloaded artifact etc.

after OOM deploy such artifact management, the plugins could access all the
downloaded artifacts by shared folder method if necessary.

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
it locally by specified rule of layout as under "vf-module-model-customization-id" directory
the related heat and heat_env file will be put with the name of own uuid, an addtional
metadata.json file will be there which includes details description about vf module.

2.check if subplugin is configurated in the configuration file

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

Layout example
==============
vfmodule-meta.json content
--------------------------
::

 [
  {
    "vfModuleModelName": "VcpevsVgw0116a..base_vcpe_vgw..module-0",
    "vfModuleModelInvariantUUID": "718c9883-8fd6-463a-b00d-0c696e0ab475",
    "vfModuleModelVersion": "1",
    "vfModuleModelUUID": "585fce63-101f-49f2-95d6-c53423baa48a",
    "vfModuleModelCustomizationUUID": "4668b783-2dba-444f-b3d8-a508f0d0c0f2",
    "isBase": true,
    "artifacts": [
      "0a38b7ef-93b9-4d48-856d-efb56d53aab8",
      "4d4a37ef-6a1f-4cb2-b3c9-b380a5940431"
    ],
    "properties": {
      "min_vf_module_instances": "1",
      "vf_module_label": "base_vcpe_vgw",
      "max_vf_module_instances": "1",
      "vfc_list": "",
      "vf_module_description": "",
      "vf_module_type": "Base",
      "availability_zone_count": "",
      "volume_group": "false",
      "initial_count": "1"
    }
  }
 ]

service-meta.json content
-------------------------
::

 {
  "artifacts":
   [
    {
     "artifactName": "base_template.env",
     "artifactType": "HEAT_ENV",
     "artifactURL":
     "/sdc/v1/catalog/services/ead658ee-f224-4e49-9f3f-8b4c49ed18dc/resourceInstances/vfwnextgen0/artifacts/0a38b7ef-93b9-4d48-856d-efb56d53aab8",
     "artifactDescription": "Auto-generated HEAT Environment deployment artifact",
     "artifactChecksum": "YzdmZDQxMjdiYjBmZDU1YWQ5YTMxZGExNWM4MjRlYzQ=",
     "artifactUUID": "0a38b7ef-93b9-4d48-856d-efb56d53aab8",
     "artifactVersion": "2",
     "generatedFromUUID": "20b803f5-b137-45aa-9196-6b79f9b9f527.heat4",
     "artifactLabel": "heat4env",
     "artifactGroupType": "DEPLOYMENT"
     },
     {
     "artifactName": "base_template.yaml",
     "artifactType": "HEAT",
     "artifactURL":
     "/sdc/v1/catalog/services/ead658ee-f224-4e49-9f3f-8b4c49ed18dc/resourceInstances/vfwnextgen0/artifacts/4d4a37ef-6a1f-4cb2-b3c9-b380a5940431",
     "artifactDescription": "created from csar",
     "artifactTimeout": 60,
     "artifactChecksum": "MGMwNzkwNmZkODExZmFkMTgwMTljMGIwNWMxOWZlODY=",
     "artifactUUID": "4d4a37ef-6a1f-4cb2-b3c9-b380a5940431",
     "artifactVersion": "2",
     "artifactLabel": "heat4",
     "artifactGroupType": "DEPLOYMENT"
     }
   ]
  }

the directory layout
--------------------
under 4668b783-2dba-444f-b3d8-a508f0d0c0f2 dir, there would be 4 files:
::

    base_dummy.yaml it's a HEAT artifact
    base_dummy.env  it's a HEAT_ENV artifact
    vfmodule-meta.json
    service-meta.json includes all artifacts details info of the artifact_list


Dependency
==============
1. SDC support:
   SDC-2041 SDC supports K8S plugin to expose APIs to add/delete cloud specific artifacts
   SDC-2045 create User and Password for MultiCloud component to access secure API
   A CSAR example including k8s artifact
2. SO support:
   modify the current interface between SO and Mutlicloud
3. MutliCloud support:
   implement the invoke logic  for the downloaded artifact conconered by k8s, clarify all the
   necessary information needed.
4. OOM support:
   need a configuration for necessary pods during deployment
   need to define how to the common setting instead of hard-code

Test Use Cases
==============
1. For k8s. the artifacts are Helm chart. need a k8s lab env for validation. need to clarify if there is some connection
between the VNFs, like using VirtualLink or just a service which is a simple wrap of one VNF

2. For OpenStack/WindRiver, use vFW test case with HEAT/HEAT_ENV artifacts.
