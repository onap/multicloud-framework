.. This work is licensed under a Creative Commons Attribution 4.0
.. International License.  http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 Wind River Systems, Inc.

===============================================
MultiCloud Plugin for Wind River Titanium Cloud
===============================================

The following guides are provided to describe tasks that a user of
ONAP may need to perform when operating ONAP to orchestrate VNF onto
an instance of Wind River Titanium Cloud


Supported Features
``````````````````

**Proxy endpoints for OpenStack services**

    MultiCloud plugin for Wind River Titanium Cloud supports the proxy of
    OpenStack services. The catalog of proxied services is exactly same as
    the catalog of OpenStack services

**VFC specific Northbound API**

    MultiCloud plugin for Wind River Titanium Cloud supports VFC with the
    legacy APIs which was inherited from OPEN-O MultiVIM project.

**Support enhanced SO/OOF workflow**

    MultiCloud plugin for Wind River Titanium Cloud supports infra_workload
    APIs from Casablanca Release.

    These APIs enhances the workflow of Heat based VNF orchestration by:
        offloading Heat template/parameter updating from SO to MultiCloud
        plugins

        Enabling the "Centralized Representation of Cloud Regions"

        Automate the heatbridge action by updating the AAI with deployed Heat
        stack resources

**Support OOF**

    MultiCloud plugin for Wind River Titanium Cloud supports capacity check
    from Beijing Release.

**Conform to Consistent ID of a Cloud Region**

    Northbound API v1 supports the composite keys
    {cloud-owner}/{cloud-region-id} as the ID of a cloud region

**Decoupling between cloud-region-id and OpenStack Region ID**

    {cloud-region-id} should be populated by users while on-boarding a cloud
    region. With ONAP A and B release, it must be the same as the "OpenStack
    Region ID" of the represented OpenStack instance. From ONAP C release,
    this restriction has been removed.

    The backward compatibility is maintained so that user can still populate
    the {cloud-reigon-id} by "OpenStack Region ID".

    Users could also specify the "OpenStack Region ID" while onboarding a
    cloud region out of multi-region instances.

    Note: There are still restrictions to populate {cloud-owner} and
    {cloud-region-id}, please refer to section "On-board a Cloud Region"

**Support on-boarding of Multi-Region instances**

    Multiple OpenStack instances federated with the "multi-region" feature
    can be on-boarded into ONAP with a single click. ONAP user needs to
    register only the primary region into ONAP, and the multicloud plugin for
    Wind River Titanium Cloud

    Titanium Cloud will discover and on-board all other secondary regions
    automatically.

    This feature supports Titanium Cloud feature "Distributed Cloud" to
    on-board all subclouds with a single click.

    This feature can be controlled by user with configuration options while
    on-boarding a cloud region

**HPA discovery**

    MultiCloud plugin for Wind River Titanium Cloud supports discover and
    registration into AAI with regarding to following HPA capability:
    CPU Pinning, HugePage, ...

**Cloud Region decommission**

    MultiCloud plugin for Wind River Titanium Cloud support the decommission
    of a cloud region with a single API requests.

    This API is not yet integrated with ESR GUI portal.

**VESagent**

    MultiCloud plugin for Wind River Titanium Cloud supports VESagent
    which can be configured to monitor the VM status and assert or abate
    fault event to VES collector for close loop control over
    infrastructure resources.

**LOGGING**
    MultiCloud plugin for Wind River Titanium Cloud supports centralized
    logging with OOM deployed ONAP


Supported Use Cases
```````````````````

**vFW/vDNS**

   The vFW/vDNS are the VNFs modeled with HEAT templates
   MultiCloud plugin for Wind River Titanium Cloud has been tested with
   vFW/vDNS use cases from Amsterdam Release.

**vCPE**

   **vCPE (HEAT VNF) without HPA orchestration**
      vCPE is the VNF modeled with HEAT templates, basic Use case from
      Amsterdam Release does not include any HPA orchestration.

   **vCPE (HEAT VNF) with HPA orchestration**
      From Beijing Release,a variation of vCPE use case include HPA
      orchestration

   **vCPE (TOSCA VNF) with HPA orchestration**
      From Casablanca Release (With MultiCloud Release Version 1.2.2), vCPE
      use case expands to support TOSCA VNF and include HPA orchestration

   MultiCloud plugin for Wind River Titanium Cloud has been tested with both
   cases.

**vVoLTE**

   MultiCloud plugin for Wind River Titanium Cloud has been tested with vVoLTE
   use case.


Known Issues:
``````````````````

1, MULTICLOUD-359 : The image uploading API from VFC specific NBI does not
work with large image file.



.. include:: Tutorial-Onboard-WindRiver-TitaniumCloud.rst


Tutorial: Cloud Region Decommission:
````````````````````````````````````

ESR GUI Portal cannot decommission a cloud region which has been updated by
MultiCloud Plugin for Wind River Titanium Cloud, and it does not request
MultiCloud to help on that yet. So it is required to issue a rest API request
to MultiCloud with a single curl commands:


.. code-block:: console

    curl -X DELETE \
    'http://$ONAP_MSB_IP:$ONAP_MSB_PORT/api/multicloud-titaniumcloud/v0/CloudOwner_RegionOne' \
    -H 'Accept: application/json' \
    -H 'Cache-Control: no-cache' \
    -H 'Content-Type: application/json' \
    -H 'Postman-Token: 8577e1cc-1038-471d-8b3b-d36fe44ae023'


.. include:: Tutorial-HPA-Provision-Discovery-WindRiver-TitaniumCloud.rst


.. include:: Tutorial-VESagent-MultiCloud-WindRiver-TitaniumCloud.rst
