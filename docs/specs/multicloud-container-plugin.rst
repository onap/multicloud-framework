.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

===================================================
Container based network service/function deployment
===================================================
https://wiki.onap.org/pages/viewpage.action?pageId=16007890

This proposal is to implement PoC in Beijing release(R-2) in order to
get experience/feedback for future progress.


Problem Description
===================
The current ONAP supports only VM based cloud infrastructure for VNF.
On the other hand, in the industry container technology is getting more
momentum.  Increasing VNF density on each node and latency
requirements are driving container based VNFs.  This project enhances
ONAP to support VNFs as containers in addition to VNFs as VMs.

It is beneficial to support for multiple container orchestration technologies
as cloud infrastructure:
* Allow VNFs to run within container technology and also allow closed
  feedback loop same to VM based VIM. e.g. openstack.
* Support for co-existence of VNF VMs and VNF containers
* Add container orchestration technology in addition to the
  traditional VM-based VIM or environment managed by ONAP.
* Support for uniform network connectivity among VMs and containers.

NOTE: This is different from OOM project `OOM`_. their scope is to
deploy ONAP itself on k8s. Our scope is to deploy/manage VNFs on k8s.


Proposed Change
===============

Scope for Beijing release(R-2)
------------------------------
Basic principle
* First baby step is to support containers in a Kubernetes cluster via a
  Multicloud SBI /K8S Plugin
  (other COE's(Container Orchestration Engine) are out of Beijing scope.
   They are future scope.)
* Minimal implementation with zero impact on MVP of Multicloud Beijing work

Use Cases
* Sample VNFs(vFW and vDNS)
  (vCPE use case is post Beijing release)

Post Beijing Release
--------------------
In Beijing release, several design aspects are compromised to re-use
the existing component/work flow with zero impact primarily because
the long term solution is not ready to use. It's acceptable this effort
in Beijing release is PoC or experimental to demonstrate functionality
and to get experience for post-Beijing design/architecture.
For example, the use of CSAR/new tosca node definitions are to re-use
the existing code(i.e. Amsteldam release). After Beijing release, those
will be revised for long term solution along with related topics. e.g.
model driven API, modeling etc...

integration scenario
--------------------
* Register/unregister k8s cluster instances which are already deployed.
  dynamic deployment of k8s is out of scope. It is assumed that admin knows
  all the necessary parameters.
* onboard VNFD/NSD to use container
* Instantiate / de-instantiate containerized VNFs through K8S Plugin
  over K8S cluster
* Vnf configuration with sample VNFs(vFW, vDNS) with the existing configuration
  interface. (no change to the existing configuration interface)



Northbound API design
=====================

REST API Impact and base URL
----------------------------
Similar to other case, k8s plugin has its own URL prefix so that it
doesn't affect other multicloud northbound API.

Base URL for kubernets plugin:

https://msb.onap.org:80/api/multicloud/v0/kubernetes/

swagger.json
------------
* PATH: swagger.json
  swagger.json for kubernetes API definitions
* METHOD: GET

returns swagger.json definitions of k8s API similar to other multicloud plugins

register/unregister kubernetes cluster instance
-----------------------------------------------
This is done via A&AI ESR `ESR`_ to follow the way of the existing
multicloud.  some attributes, e.g. region id, don't make sense for
k8s. In that case predefined value, e.g. 'default', are used.
The info for basic authentication against kuberenetes API is registered.
i.e. the pair of (username, password).

NOTE: HPA(kubernetes cluster features/capabilities) is out of scope
for Beijing Assumption K8s cluster instance is already
pre-build/deployed Dynamic instantiation is out of scope(for Beijing)


attributes for A&AI ESR
^^^^^^^^^^^^^^^^^^^^^^^

This subsection describes how attributes for VIM registration are specified.
For actual definitions, please refer to `ESR`_
Some attributes doesn't apply to kubernetes so that such attributes will
be left unspecified if it's optional or define pre-defined constants if
it's mandatory.

URI /api/aai-esr-server/v1/vims
Operation Type	POST

Request Body:

------------------ ---------- ------- ----------------------------------------
Attribute          Qualifier  Content Description
================== ========== ======= ========================================
cloudOwner         M          String  any string as cloud owner
------------------ ---------- ------- ----------------------------------------
cloudRegionId      M          String  e.g. "kubernetes-<N>" as it doesn't apply
                                      to k8s. Cloud admin assigns unique id.
------------------ ---------- ------- ----------------------------------------
cloudType          M          String  "kubernetes". new type
------------------ ---------- ------- ----------------------------------------
cloudRegionVersion M          String  kubernetes version. "v1.9", "v1.8" ...
------------------ ---------- ------- ----------------------------------------
ownerDefinedType   O          String  None. (not specified)
------------------ ---------- ------- ----------------------------------------
cloudZone          O          String  None. (not speicfied)
                                      as kubernetes doesn't have notion of
                                      zone.
------------------ ---------- ------- ----------------------------------------
complexName        O          String
------------------ ---------- ------- ----------------------------------------
cloudExtraInfo     O          String  json string(dictionary) for necessary
                                      info. For now "{}" empty dictionary
------------------ ---------- ------- ----------------------------------------
vimAuthInfos       M          [Obj]   Auth information of Cloud
================== ========== ======= ========================================

There are several constraints/assumptions on cloudOwner and
cloudRegionId. `cloud-region`_ . For k8s, cloudRegionId is (ab)used to
specify k8s cluster instance. ONAP admin has to assign unique id for
cloudRegionId as id for k8s cluster instance.


authInfoItem

Basic authentication is used for k8s api server.

-------------- --------- ------- -------------------------------------------
Attribute      Qualifier Content Description
============== ========= ======= ===========================================
cloudDomain    M         String  "kubernetes" as this doesn't apply.
-------------- --------- ------- -------------------------------------------
userName       M         String  User name
-------------- --------- ------- -------------------------------------------
password       M         String  Password
-------------- --------- ------- -------------------------------------------
authUrl        M         String  URL for kubernetes API server
-------------- --------- ------- -------------------------------------------
sslCacert      O         String  ca file content if enabled ssl on
                                 kubernetes API server
-------------- --------- ------- -------------------------------------------
sslInsecure    O         Boolean Whether to verify VIM's certificate
============== ========= ======= ===========================================



Kubernetes proxy api
--------------------
* PATH: /<cloud-id>/proxy/<resources>
* METHOD: All methods

proxy(or passthru) API to kubernetes API with authorization adjustment
to kubernetes API server to {kubernetes api prefix}/<resources>
without any changes to http/https request body.  For details of kubernetes
API, please refer to
https://kubernetes.io/docs/reference/api-overview/
Note: kubernetes doesn't have concept of region, tenant.(at this point). So region and tenant_id isn't in path.

Kubernetes yaml
---------------
* PATH: /<cloud-id>/yaml
* METHOD: POST

Similar to kubectl -f xxx.yaml. it accepts template to create k8s
resources.  Maybe this isn't necessary as the caller can be easily
convert k8s yaml to k8s API calls. For Beijing release, we don't want to
change adaptors in SO. This API is convenient/workaround API for Beijing.
This API is build on top of proxy api. Instead of directly executing kubectl
python kuberenets client is used. `python-kubernetes-client`_
Post Beijing, this API needs to be revised to adapt model driven API.

Kubernetes: Helm
----------------
TBD: need discussion with Munish.
     If he doesn't respond, remove helm related stuff.

* PATH: /<cloud id>/helm/<helm URL: grpc>
* METHOD: all method
Pass through to helm tiller api server with authorization adjustment

Kubernetes: CSAR
----------------
NOTE: the use of CSAR is temporary work around for Beijing release to avoid
modification to adapters in SO.
Post Beijing, the northound API will be revised/removed to follow
model drive API once multicloud adaptor in SO is available.

* PATH: /<cloud id>/csar
* METHOD: POST

Extract k8s yaml file from CSAR and create k8s resources.
This API is build on top of kubernetes yaml API.


On boarding/packaging/instantiation
===================================
We shouldn't change the current existing work flow.
In short term: Use additional node type/capability types etc.
In longer term way: Follow ONAP community directoin. At the moment, work
with TOSCA community to add additional node type to express k8s.

NOTE: this packaging is temporally work around until ONAP modelling
and multicloud model driven API are available. Post Beijing release
packaging will be revised to follow ONAP modeling and multicloud model
driven API.

Packaging and on-boarding
-------------------------
Reuse CASR so that the existing work flow doesn't need change. For
Beijing CSAR is used with its own TOSCA node definition. In longer
term, once multicloud project has model driven API, it will be followed
to align with modeling and SO.

TOSCA nodes definitions
-----------------------
Introduce new nodes to wrap k8s ingredients(k8s yaml, helm etc.) These
TOSCA node definitions are short term work around to re-use the existing
component/workflow until model driven API is defined/implemented.
For Beijing, human will write this TOSCA by hands for PoC. Post Beijing,
packaging needs to be revised to align with modeling and SO. Also SDC,
VNF-SDK need to be addressed for creation.

* onap.multicloud.nodes.kubernetes.proxy

  * node definitions
  .. code-block::

     data_types:
       onap.multicloud.container.kubernetes.proxy.nodes.resources_yaml:
       properties:
         name:
           type: string
           description: >
             Name of application
         path:
           type: string
           description: >
             Paths to kubernetes yaml file

* onap.multicloud.nodes.kubernetes.helm
  TBD. remove unless munish contributes.

This TOSCA node definitions wrap kubernetes yaml file or helm chart.
cloudify.nodes.Kubernetes isn't reused in order to avoid definition conflict.

instantiation
-------------
SO ARIA adaptor can be used. (with twist to have SO to talk to
multicloud k8s plugin instead of ARIA) Instantiation so that SO
can talk to multicloud k8s plugin.
NOTE: This is temporally work around for Beijing release. Post Beijing, this
needs to be revised.

work flow
---------
With Amsteldam Release, SO has ARIA adaptor which talks to ARIA orchestrator.
https://wiki.onap.org/download/attachments/16002054/Model%20Driven%20Service%20Orchestration%20-%20SO%20State%20of%20the%20Union.pptx

The work flow looks like follows::

             user request to instantiate VNF
                           |
            +--------------|-------+
            | SO           |       |
            |              V       |
            | +------------------+ |
            | | SO: ARIA adaptor | |
            | +------------+-----+ |
            +--------------|-------+
                           | CASR is sent
                           |
            +--------------|---------+
            | ARIA         |         |
            |              V         |
            | +--------------------+ |
            | | multicloud  plugin | |   template as TOSCA artifact is
            | +------------+-------+ |   extracted and build requests to
            +--------------|---------+   multicloud
                           |
                           |
            +--------------|-------+
            | multicloud   |       |
            |              V       |
            | +------------------+ |
            | | openstack plugin | |
            | +------------+-----+ |
            +--------------|-------+
                           | openstack request
                           |
                           V
            +----------------------+
            | openstack            |
            +----------------------+


This will be twisted by configuration so that SO can talks to
multicloud k8s plugin::

             user request to instantiate VNF
                           |
            +--------------|-------+
            | SO           |       |
            |              V       |
            | +------------------+ |
            | | SO: ARIA adaptor | |  configuration is twisted to call
            | +------------+-----+ |  multicloud k8s API
            +--------------|-------+
                           | CSAR
                           |
            +--------------|-------+
            | multicloud   |       |
            |              V       |
            | +------------------+ |
            | | k8s plugin       | |  extract k8s yaml file from CSAR
            | +------------+-----+ |  and passthrough request to k8s API
            +--------------|-------+
                           | k8s request
                           |
                           V
            +----------------------+
            | kubernetes API server|
            +----------------------+

Optionally helm can be used instead of directly calling k8s api server.
If necessary, ARIA multicloud plugin could be twisted to call k8s plugin.

The strategy is to keep the existing design of ONAP or to follow
agreed design.
The key point of The interaction between SO and multicloud is
* SO decomposes VNFD/NSD into single atomic resource
  (e.g. VNF-C corresponding to single VM or single container/pod)
  and send requests to create each resources.
* multicloud accepets each request for single atomic resource and
  create single resource(e.g. VM or container/pod)
* multicloud doesn't do resource decomposition. The decomposition is task
  of SO.

API work flow example and k8s API
---------------------------------
* register k8s cluster to A&AI ESR
  <cloud id> is obtained
* ONAP north bound components generates a TOSCA template targeted for k8s.
* SO calls Multicloud proxy API and passes the entire BluePrint(as CSAR) to
  k8s plugin and CSAR api POST VNFD/NSD to
  POST https://msb.onap.org:80/api/multicloud/v0/kubernetes/<cloud-id>/proxy/csar
* k8s plugin handles the CSAR accordingly and talks to k8s api server to
  deploy containerized VNF
  POST <k8s api server>://api/v1/namespaces/{namespace}/pods
  to create pods. then <pod id> is obtained
* DELETE https://msb.onap.org:80/api/multicloud/v0/kubernetes/<cloud-id>/proxy/api/v1/namespaces/{namespace}/pods/<pod id>
  to destroy pod
* to execute script inside pod, the following URL can be used.
  POST /api/v1/namespaces/{namespace}/pods/{name}/exec


Affected Projects and impact
============================

A&AI and ESR
------------
new type to represent k8s/container for cloud infrastructure will
be introduced as work around. Post Beijing official value will be
discussed for inclusion.

OOF
---
Policy matching is done by OOF.
For Beijing. Enhancement to policy is stretched goal.
Decomposing service design(NSD, VNFD) from VNF package is done by SO
with OOF(homing)

SO
--
ARIA adaptor is re-used with config tweak to avoid modification

multicloud
----------
new k8s plugin will be introduced. The details are discussed in this
documentation you're reading right now.


Kubernetes cluster authentication
=================================
For details of k8s authentication, please refer to
https://kubernetes.io/docs/admin/authentication

Because Kubernetes cluster installation is not mentioned, we should
treat all users as normal users when authenticate to
Kubernetes VIM. There are several ways to authenticate Kubernetes
cluster. For Beijing release, basic authentication will be supported.
username and password are stored in ESR.


References
==========
Past presentations/proposals
----------------------------
.. _Munish proposal: https://schd.ws/hosted_files/onapbeijing2017/dd/Management%20of%20Cloud%20Native%20VNFs%20with%20ONAP%20PA5.pptx
.. _Isaku proposal:https://schd.ws/hosted_files/onapbeijing2017/9d/onap-kubernetes-arch-design-proposal.pdf
.. _Bin Hu proposal:https://wiki.onap.org/download/attachments/16007890/ONAP-SantaClara-BinHu-final.pdf?version=1&modificationDate=1513558701000&api=v2

ONAP components
---------------
.. _ESR: Extenral System Register https://wiki.onap.org/pages/viewpage.action?pageId=11930343#A&AI:ExternalSystemOperationAPIDefinition-VIM
.. _AAI: Active and Available Inventory https://wiki.onap.org/display/DW/Active+and+Available+Inventory+Project
.. _OOM: ONAP Operations Manager https://wiki.onap.org/display/DW/ONAP+Operations+Manager+Project

kubernetes
----------
.. _kubernetes-python-client: Kubernetes python client https://github.com/kubernetes-client/python

misc
----
.. _cloud-region: How to add a new cloud region and some thoughts https://wiki.onap.org/download/attachments/25429038/HowToAddNewCloudRegionAndThoughts.pdf

Contributors
============
* Isaku Yamahata <isaku.yamahata@intel.com> <isaku.yamahata@gmail.com>
* Bin Hu <bh526r@att.com>
* Munish Agarwal <munish.agarwal@ericsson.com>
* Phuoc Hoang <phuoc.hc@dcn.ssu.ac.kr>


APPENDIX
========
This section is informative. This is out of Beijing scope and will be
revised after Beijing.
The purpose is to help readers to understand this proposal by giving
future direction and considerations.

Model driven API and kubernetes model
-------------------------------------
Currently the discussion on model driver API is on going. Once it's usable,
it will be followed and the above experimental API/code will be revised.

The eventual work flow looks like as follows::

             user request to instantiate VNF/NS
                           |
                           V
            +----------------------+         +-----+
            | SO                   |-------->| OOF | <--- policy to use
            |                      |<--------|     |      CoE instead of VM
            |                      |         +-----+      from A&AI
            | +------------------+ |
            | | SO: adaptor for  | | SO decomposes VNFD/NSD into atomic
            | | multicloud model | | resources(VDUs for VNF-C) with asking OOF
            | | driven API       | | for placement. then SO builds up
            | +------------+-----+ | requests to multicoud for instantiation.
            +--------------|-------+
                           |
                           |
            +--------------|-------+
            | multicloud   |       | So multicloud accepts request for single
            |              V       | resource of VDU which corresponds to
            | +------------------+ | VNF-C. which is mapped to single
            | | model driven API | | container/pod. multicloud doesn't
            | +------------+-----+ | decompose VDU into multiple containers.
            |              |       | CoE doesn't change such work flow.
            |              V       |
            | +------------------+ |
            | | k8s plugin       | | convert request(VDU of VNF-C) into
            | +------------+-----+ | kubernetes
            +--------------|-------+
                           | k8s request
                           |
                           V
            +----------------------+
            | kubernetes           |
            +----------------------+


Modeling/TOSCA to kubernetes conversion
---------------------------------------
In this section, conversion from TOSCA to kubernetes is discussed
so that reader can get idea for future direction.

Once ONAP information/data model is usable, similar conversion is possible.
The followings are only examples. More node definitions would be considered
as necessary::

  TOSCA node definition        k8s resource
  ============================ ================================
  tosca.nodes.Compute          (bare)single pod
                               vcpu, memory -> k8s resource
  ---------------------------- --------------------------------
  tosca.nodes.nfv.VDU.Compute  (bare)single pod


Hello world example
-------------------
This is just to show idea.
This example is very early phase and there are hard-coded values.


* TOSCA hello world
  .. code-block::

    topology_template:
      node_templates:
        my_server:
          type: tosca.nodes.Compute
          capabilities:
            # Host container properties
            host:
             properties:
               num_cpus: 2
               disk_size: 10 GB
               mem_size: 512 MB
            # Guest Operating System properties
            os:
              properties:
                # host Operating System image properties
                architecture: x86_64
                type: Linux
                distribution: RHEL
                version: 6.5


* converted k8s yaml
  .. code-block::

    $ PYTHONPATH=. python -m tosca_translator.shell -d --debug --template-file tosca_translator/tests/data/tosca_helloworld.yaml
    api_version: apps/v1beta1
    kind: Deployment
    metadata:
      labels: {name: my_server}
    spec:
      replicas: 1
      template:
        metadata:
          labels: {name: my_server}
        spec:
          containers:
          - image: ubuntu
            name: my_server
            resources:
              limits: {cpu: 2, ephemeral-storage: 10 GB, memory: 512 MB}
            requests: {cpu: 2, ephemeral-storage: 10 GB, memory: 512 MB}
