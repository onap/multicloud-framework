.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

===================================================
Container based network service/function deployment
===================================================
https://wiki.onap.org/pages/viewpage.action?pageId=16007890

This proposal will to implement PoC or experiment in Beijing release(R-2)
in order to get experience/feedback for future progress.


Problem Description
===================
The currently ONAP supports only VM based cloud infrastructure.  On
the other hand, in the industry container technology is getting more
momentum.  Increasing VNF density on each node and latency
requirements are driving container based VNFs.  This project enhances
ONAP to support VNFs as containers in addition to VNFs as VMs.

Support for multiple container orchestration technologies as cloud
infrastructure - allow VNFs to run within container technology and
also allow closed feedback loop same to VM based VIM. e.g. openstack
Support for co-existence of VNF VMs and VNF containers - Add container
orchestration technology in addition to the traditional VM-based VIM
or environment managed by ONAP. So that VNFs can be deployed within
container by ONAP.  Support for uniform network connectivity among VMs
and containers.


Proposed Change
===============

Scope for Beijing release(R-2)
------------------------------
Basic principle
* First baby step to support containers in a Kubernetes cluster via a Multicloud SBI /K8S  Plugin
* Minimal implementation with zero impact on MVP of Multicloud Beijing work

Use Cases
* Sample VNFs(vFW and vDNS)
  (vCPE use case is post Beijing release)

integration scenario
--------------------
* Register/unregister k8s cluster instance which is already deployed.
  dynamic deployment of k8s is out of scope.
* onboard VNFD/NSD to use container
* Instantiate / de-instantiate containerized VNFs through K8S Plugin
  in K8S cluster
* Vnf configuration with sample VNFs(vFW, vDNS)


Northbound API design
=====================

REST API Impact and base URL
----------------------------
Similar to other case, k8s plugin has its own URL prefix so that it
doesn't affect other multicloud northbound API.

Base URL for kubernets plugin:

https://msb.onap.org:80/api/multicloud-kubernetes/v0

Metadata
--------
* PATH: swagger.json
  Metadata for kubernetes API definitions
* METHOD: GET

register/unregister kubernetes cluster instance
-----------------------------------------------
* PATH: clusters
* METHOD: POST
  * Register kubernetes cluster instance
  * Returns cloud-id
  * K8s instance tracking. Locations etc.
* METHOD: DELETE, GET, PUT

NOTE: HPA(kubernetes cluster features/capabilities) is out of scope
for Beijing Assumption K8s cluster instance is already
pre-build/deployed Dynamic instantiation is out of scope(for Beining)

Kubernetes proxy api
--------------------
* PATH: clusters/<cloud-id>/proxy/<resources>
* METHOD: All methods

proxy(or passthru) API to kubernetes API with authorization adjustment
to kubernetes API server to {kubernetes api prefix}/<resources>
without any changes to http request body.  For details of kubernetes
API, please refer to
https://kubernetes.io/docs/reference/api-overview/
Note: kubernetes doesn't have concept of region, tenant.(at this point). So region and tenant_id isn't in path.

Kubernetes yaml
---------------
* PATH: clusters/<cloud-id>/yaml
* METHOD: POST

Similar to kubectl -f xxx.yaml. it accepts template to create k8s
resources.  Maybe this isn't necessary as the caller can be easily
convert k8s yaml to k8s API calls.  Shortcut to POST to multiple k8s
resources.

Kubernetes: Helm
----------------
TBD: need discussion with Munish.

* PATH: clusters/<cloud id>/helm/<helm URL: grpc>
* METHOD: all method
Pass through to helm tiller api server with authorization adjustment

Kubernetes: CSAR
-----------------
temporally work around. This api will be removed after Beijing PoC until SO adaptor is resolved.

* PATH: clusters/<cloud id>/csar
* METHOD: POST

Extract k8s yaml file from CSAR and create k8s resources.


On boarding/packaging/instantiation
===================================
We shouldn't change the current existing work flow.
In Short term: Use additional node type/capability types etc.
In longer term way: work with TOSCA community to add additional node
type to express k8s.

Packaging and on-boarding
-------------------------
Reuse CASR so that the existing work flow doesn't need change.For
Beijing CSAR is used with its own TOSCA node definition. (in longer
term, once multicloud project has model driven API, it will be used.)

TOSCA nodes definitions
-----------------------
Introduce new nodes to wrap k8s ingredients(k8s yaml, helm etc.) This
is short term solution until model driven API is defined/implemented.

* onap.multicloud.nodes.kubernetes.proxy
* onap.multicloud.nodes.kubernetes.helm

This wraps kubernets yaml file or help chart as
necessary. cloudify.nodes.Kubernetes isn't reused in order to avoid
definition conflict.

instantiation
-------------
SO ARIA adaptor can be used. (with twist to have SO to talk to
multicloud k8s plugin instead of ARIA) Instantiation and SO

OOF : TBD
=========
Policy matching is done by OOF.
For Beijing. Enhancement to policy is stretched goal.
Decomposing service design(NSD, VNFD) from VNF package is done by SO with OOF(homing)


Kubernetes cluster authentication
=================================
Note: https://kubernetes.io/docs/admin/authentication

Because Kubernetes cluster installation is not mentioned, we should
treat all users as normal users when authenticate to
Kubernetes VIM. There are several ways to authenticate Kubernetes
cluster:

Using kubeconfig file
---------------------
Users provide each Kubernetes VIM information as a cluster, user or context in kubeconfig files.

kubeconfig files::

        apiVersion: v1
        clusters:
        - cluster:
           certificate-authority: fake-ca-file
           server: https://1.2.3.4
         name: development
        - cluster:
           insecure-skip-tls-verify: true
           server: https://5.6.7.8
         name: scratch
        contexts:
        - context:
           cluster: development
           namespace: frontend
           user: developer
         name: dev-frontend
        - context:
           cluster: scratch
           namespace: default
           user: experimenter
         name: exp-scratch
        current-context: ""
        kind: Config
        preferences: {}
        users:
        - name: developer
         user:
           client-certificate: fake-cert-file
           client-key: fake-key-file

In this scenario, when user want to deploy a VNF, user should provide:
* Kubeconfig file path: Path to the kubeconfig file to use for CLI requests
* Cluster: The name of the kubeconfig cluster to use
* Context: The name of the kubeconfig context to use
* User: The name of the kubeconfig user to use

These files are stored in file system of one host, where multi cloud
k8s is installed. Because all tenant VIM information is saved as
files, it may be not the good way to manage Kubernetes cluster. It
also cause complicated management of Kubernetes VIM.

Details for configure access multiple clusters, please refer to
https://kubernetes.io/docs/tasks/access-application-cluster/configure-access-multiple-clusters

Using Bearer token
------------------
Similar as above approach, but we only store some necessary parameter
to validate an user using Bearer token. When register a Kubernetes
VIM, user should fill in the following information:

* Kubernetes API address: The address and port of the Kubernetes API server
  (e.g. 192.168.1.2:6443)
* Bearer token: Bearer token for authentication to the API server
* Client certificate file: Path to a client certificate file for TLS (optional)

Using basic authentication
--------------------------
Different way, username and password for authenticating

* Kubernetes API address: The address and port of the Kubernetes API server
  (e.g. 192.168.1.2:6443)
* Username: Username for basic authentication to the API server
* Password: Password for basic authentication to the API server
* Client certificate file: Path to a client certificate file for TLS (optional)


Note:
Using bearer token and basic authentication (username and password)
may gain some benefits. Users provide their authentication information
of Kubernetes VIM where VNFs will be deployed.
It may be similar to OpenStack, users can provide their Kubernetes VIM
information for registering.
It can work with Kubernetes client java and kubectl.

References
==========
Past presentations/proposals
* Munish proposal: https://schd.ws/hosted_files/onapbeijing2017/dd/Management%20of%20Cloud%20Native%20VNFs%20with%20ONAP%20PA5.pptx
* Isaku proposal:https://schd.ws/hosted_files/onapbeijing2017/9d/onap-kubernetes-arch-design-proposal.pdf
* Bin Hu proposal:https://wiki.onap.org/download/attachments/16007890/ONAP-SantaClara-BinHu-final.pdf?version=1&modificationDate=1513558701000&api=v2


Contributors
============
* Isaku Yamahata <isaku.yamahata@intel.com> <isaku.yamahata@gmail.com>
* Bin Hu <bh526r@att.com>
* Munish Agarwal <munish.agarwal@ericsson.com>
* Phuoc Hoang <phuoc.hc@dcn.ssu.ac.kr>
