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
deploy ONAP itself on k8s. Our scope is to deploy/manage VNFs on
container/container orchestration engine(coe). The first target is
k8s. Other CoE will also be addressed if someone steps up to support it.


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
  Both vFW and vDNS are targeted. Since custom TOSCA node definitions
  are used (please refer to tosca section below), new TOSCA templates
  are needed for them. (In future, post-Beijing, this will be revised
  to share common TOSCA template.)

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
model driven API, modeling based on Beijing experience. Once we
figured out what multicloud COE API should look like and what adapters
in other projects(SO, APP-C) are needed(or not needed) in long term,
the inter-project discussion (mainly with SO, APP-C) will start in
Casablanca cycle.

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


Northbound API Design
=====================

REST API Impact and Base URL
----------------------------

Similar to other plugins(e.g. openstack plugin), k8s plugin has
its own API endpoint and base URL so that it doesn't affect other
multicloud northbound API.

Base URL for kubernets plugin:

https://msb.onap.org:80/api/multicloud/v0/

NOTE: Each multicloud plugin has its own API endpoint(ip address).
So the plugin is distinguished by endpoint IP address with MSB.
"multicloud-kubernetes" name space for MSB is used.
NOTE: each COE support has its own API end point and name space.
their name spaces will be "multicloud-<coe name>". With model driven
API, we will have API agnostic to COE. in that case the name space
"multicloud-coe" will be used.

cloud-id
--------
In ONAP, cloud-id is the format of <cloudOwner>_<cloudRegion>
Since k8s doesn't have notion of region, cloud admin will assign
unique string it as cloudRegion and it will be used as a part of cloud-id.

APIs for VNF Lifecycle Management
---------------------------------

* PATH: /<cloud-id>/proxy/<resources>
* METHOD: All methods

Northbound components, e.g. APP-C, use these APIs for lifecycle management of
VNF resources within the Kubernetes cluster, e.g. pods. In essence, these APIs
provide simple proxy (or passthrough) functions with authorization adjustment
to Kubernetes API Server so that the relevant lifecycle management operations
are actually achieved by Kubernetes cluster itself. In another word, these API
requests are proxied to "{kubernetes api prefix}/<resources>" within Kubernetes
cluster without any changes to http/https request body.
the API end point is stored in AA&I and the API consumer will get it from
AA&I.

For details of Kubernetes API, please refer to
https://kubernetes.io/docs/reference/api-overview/

NOTE: kubernetes doesn't have concept of region and tenant at this moment.
So region and tenant_id isn't in path.
NOTE: VF-C is ETSI NFV orchestrater.(NFV-O) In Beijing release, this isn't
addressed because container is out of scope of ETSI NFV at the time of
writing. Post-Beijing, this will be given consideration. First target
is APP-C as it's easier.

API for VNF Deployment
----------------------

* PATH: /<cloud-id>/package
* METHOD: POST
  media type of Content-Type and/or filename of Contest-Disposition are used
  to specify package type.

  As private media type, application/onap-multicloud-<coe name>-<type> is used.
  More concretely for Beijing release the following media types are used.
  * Content-Type: application/onap-multicloud-kubernetes-csar
  * Content-Type: application/onap-multicloud-kubernetes-helm
  As supplement, filename is also used to guess media type. As http header type
  Contest-Disposition is used to pass filename.
  * Content-Disposition: attachment; filename="fname.tgz"
  first media type is tried and then filename is tried. If both are present
  media type is used.

This API provides northbound components, e.g. SO, with the function of
deploying containerized VNF package into Kubernetes cluster. The VNF package
is delivered as payload of HTTP request body in the API call. The VNF package
could be a CSAR or Helm Charts.

CSAR deployment package will include a yaml deployment file and other
artifacts.
This approach would work for simple VNFs consisting of single PODs.

For VNFs comprising of multiple PODs which are dependent on each other, Helm
based approach would be used. The VNF package would be described as a Helm
package consisting of a set of Helm charts and k8s yamls for each constituent
service that is part of the VNF.

There would be no change required in the Northboud API from MultiCloud for
either CSAR package or Helm package or any other package in the future. SO
calls this MultiVIM Northbound API and sends the k8s package (e.g. csar, or
tgz) as payload. k8s Plugin will distinguish package types based on its suffix
and interact with k8s cluster appropriately:

* For CSAR: k8s yaml file will be extracted from CSAR. k8s REST API server
  will be called to create k8s resources (e.g. pods), which is equivalent to
  "kubectl create -f <file.yaml>". The TOSCA file in CSAR is expected to
  include onap.multicloud.container.kubernetes.proxy.nodes.resources_yaml
  node which is explained below. In another word, Kubernetes yaml is stored as
  artifact in CSAR. it is extracted and then it is fed to k8s API.

* For TGZ: call Tiller API (gRPC-based) and pass through the Helm package

The Kubernetes API Server (RESTful) or Helm Tiller Server (gRPC) URLs are
configured for k8s Plugin when the Kubernetes cluster is created and Helm
is installed.

With this single API for package, when we need to add new package
support in the future, no extra code in SO is needed.

swagger.json
------------

* PATH: swagger.json
  swagger.json for kubernetes API definitions
* METHOD: GET

returns swagger.json definitions of k8s API similar to other multicloud plugins

Internal APIs for Implementations
---------------------------------

Some internal APIs may be needed by the implementation details of above
northbound APIs. For example, when implementing VNF Deployment API above,
we may need internal APIs to assist calling Helm Tiller Server or Kubernetes
API Server, e.g. similar to "kubectl create -f xxx.yaml".

The internal API, if needed, will be handled in implementation, which is out
of scope of this section of the document.

Test plan
---------

In this section test play is discussed. In Beijing cycle, test is minimal
or stretched goal because the effort in Beijing is PoC/experimental
to get experience. the following class of test would be planned as
stretched goal.

* Unit Test

  * API input/output
* functional test

  * communication to backend(K8S API server, helm tiller server)
* CSIT as end-to-end test


Register/Unregister Kubernetes Cluster Instance
===============================================

This is done via A&AI ESR `ESR`_ to follow the way of the existing
multicloud.  some attributes, e.g. region id, don't make sense for
k8s. In that case predefined value, e.g. 'default', are used.
The info for basic authentication, i.e. the pair of (username, password),
against kuberenetes API is registered and stored in A&AI.

NOTE: ESR will call registry API when register a new VIM(k8s). we need to
make sure that we have this API in this plugin and give them response.

NOTE: HPA(kubernetes cluster features/capabilities) is out of scope
for Beijing Assumption K8s cluster instance is already
pre-build/deployed Dynamic instantiation is out of scope(for Beijing)

attributes for A&AI ESR
-----------------------

This subsection describes how attributes for VIM registration are specified.
For actual definitions, please refer to `ESR`_
Some attributes doesn't apply to kubernetes so that such attributes will
be left unspecified if it's optional or define pre-defined constants if
it's mandatory.

URI /api/aai-esr-server/v1/vims
Operation Type	POST

Request Body:

::

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
  complexName        O          String  None. (not specified)
                                        as kubernetes doesn't have notion of
                                        complex.
  ------------------ ---------- ------- ----------------------------------------
  cloudExtraInfo     O          String  json string(dictionary) for necessary
                                        info. For now "{}" empty dictionary.
                                        For helm support, URL for tiller server
                                        is stored.
  ------------------ ---------- ------- ----------------------------------------
  vimAuthInfos       M          [Obj]   Auth information of Cloud
                                        list of authInfoItem which is described
                                        below.
  ================== ========== ======= ========================================

There are several constraints/assumptions on cloudOwner and
cloudRegionId. For k8s, cloudRegionId is (ab)used to
specify k8s cluster instance. ONAP admin has to assign unique id for
cloudRegionId as id for k8s cluster instance.

NOTE: complexName: this will be revised post-Beijing. "complex" is used to
specify (latitude, longitude) of a data center location for the purpose of
homing optimization. If those values can be obtained somehow, this should
be populated.

authInfoItem

Basic authentication is used for k8s api server.

::

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

NOTE: For some issues `issue23`_, ESR should provide authenticating by
bearer token for Kubernetes cluster if possible beside basic authentication.
Those extra value will be stored in cloudExtraInfo. This is stretched goal.


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

TOSCA node definitions
-----------------------

Introduce new nodes to wrap k8s ingredients(k8s yaml, helm etc.) These
TOSCA node definitions are short term work around to re-use the existing
component/workflow until model driven API is defined/implemented.
For Beijing, human will write this TOSCA by hands for PoC. Post Beijing,
packaging needs to be revised to align with modeling and SO. Also SDC,
VNF-SDK need to be addressed for creation.

* onap.multicloud.nodes.kubernetes.proxy

  * node definitions

    ::

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

For VNFs that are packages as Helm package there would be only one
TOSCA node in the TOSCA template which would have reference to the
Helm package.

* onap.multicloud.nodes.kubernetes.helm

  * node definitions

    ::

      data_types:
        onap.multicloud.container.kubernetes.helm.nodes.helm_package:
        properties:
          name:
            type: string
            description: >
              Name of application
          path:
            type: string
            description: >
              Paths to Helm package file

This TOSCA node definitions wrap kubernetes yaml file or helm chart.
cloudify.nodes.Kubernetes isn't reused in order to avoid definition conflict.

Instantiation
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
                           | CSAR or TGZ
                           |
            +--------------|-------+
            | multicloud   |       |
            |              V       |
            | +------------------+ |  handle CSAR or TGZ (Helm Charts) file
            | | k8s plugin       | |  e.g. extract k8s yaml from CSAR, and
            | +------------+-----+ |  pass through requests to k8s/Helm API
            +--------------|-------+
                           | k8s/Helm request
                           |
                           V
            +----------------------+
            | k8s/Helm API server  |
            +----------------------+


NOTE: In this work flow. only the northbound deployment API endpoint is needed
for VNF deployment. LCM APIs are only needed for lifecycle management. Other
internal APIs, e.g. k8s YAML API may be needed only for internal
implementation.

SO ARIA multicloud plugin needs to be twisted to call k8s plugin.

The strategy is to keep the existing design of ONAP or to follow
agreed design.
The key point of The interaction between SO and multicloud is

* SO decomposes VNFD/NSD into single atomic resource
  (e.g. VNF-C corresponding to single VM or single container/pod)
  and send requests to create each resources via deployment API.
* multicloud accepts each request for single atomic resource and
  create single resource(e.g. VM or container/pod)
* multicloud doesn't do resource decomposition. The decomposition is task
  of SO.

API work flow example and k8s API
---------------------------------
* register k8s cluster to A&AI ESR
  <cloud-id> is obtained
* ONAP north bound components generates a TOSCA template targeted for k8s.
* SO calls Multicloud deployment API and passes the entire BluePrint(as CSAR or
  TGZ) to k8s plugin, e.g.:
  POST https://msb.onap.org:80/api/multicloud/v0/<cloud-id>/package
* k8s plugin handles the CSAR or TGZ accordingly and talks to k8s API Server
  or Helm Tiller Server to deploy containerized VNF
  POST <k8s api server>://api/v1/namespaces/{namespace}/pods
  to create pods. then <pod id> is obtained
* DELETE https://msb.onap.org:80/api/multicloud/v0/<cloud-id>/proxy/api/v1/namespaces/{namespace}/pods/<pod id>
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
.. _Isaku proposal: https://schd.ws/hosted_files/onapbeijing2017/9d/onap-kubernetes-arch-design-proposal.pdf
.. _Bin Hu proposal: https://wiki.onap.org/download/attachments/16007890/ONAP-SantaClara-BinHu-final.pdf?version=1&modificationDate=1513558701000&api=v2

ONAP components
---------------
.. _ESR: Extenral System Register https://wiki.onap.org/pages/viewpage.action?pageId=11930343#A&AI:ExternalSystemOperationAPIDefinition-VIM
.. _AAI: Active and Available Inventory https://wiki.onap.org/display/DW/Active+and+Available+Inventory+Project
.. _OOM: ONAP Operations Manager https://wiki.onap.org/display/DW/ONAP+Operations+Manager+Project
.. _ONAPREST: RESTful API Design Specification https://wiki.onap.org/display/DW/RESTful+API+Design+Specification

kubernetes
----------
.. _kubernetes-python-client: Kubernetes python client https://github.com/kubernetes-client/python

.. _issue23: https://github.com/kubernetes/kubeadm/issues/23

.. misc
.. ----
.. .. _cloud-region: How to add a new cloud region and some thoughts https://wiki.onap.org/download/attachments/25429038/HowToAddNewCloudRegionAndThoughts.pdf


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
The purpose of this appendix is to help readers to understand this
proposal by giving future direction and considerations.
At some point, this appendix will be separated out into its own document
for long-term right design.

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
  ::

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
  ::

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
