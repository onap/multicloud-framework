..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=====================================================
MultiCloud alignment to Consistent ID of Cloud Region
=====================================================

To support ONAP Functional Requirement: "Centralized Representation and Consistent Identification of Cloud Regions In ONAP", MultiCloud services have to upgrade their APIs to align to the consistent ID of a cloud region.
..
https://wiki.onap.org/display/DW/Centralized+Representation+and+Consistent+Identification+of+Cloud+Regions+In+ONAP

Problems Statement
==================

With ONAP Amsterdam and Beijing Releases, there are 2 problems with respect to
the ID of a cloud region.

``Problem 1``: {vim_id}={cloud-owner}_{cloud-region-id} imposed unnecessary constraint
on how {cloud-owner} can be populated: character underscore '_' cannot be used to
populate {cloud-owner}

``Problem 2``: multicloud plugins for OpenStack leverages {cloud-region-id} as the OpenStack
Region ID to interact with represented OpenStack Instance. This implies that {cloud-region-id}
can only be populated by the represented OpenStack Region ID. This constraint implies
that two OpenStack instances with the same Region ID can only be represented with different
{cloud-owner}, even though they belong to the same owner. This is a violation to the
sematics of {cloud-owner} . on the other hand, the sematics of {cloud-region-id} refers to the
geographic region, which is not necessarily the same as OpenStack Region ID. What's more, the
fact that VID and SDNC have been using the {cloud-region-id} alone to identify a cloud region makes
the problem being unacceptable.

Proposed Solutions
==================

I would like to present 2 proposals to workaround each of problems above respectively:

``Proposal 1: depreciate the {vim_id} for all multicloud services, use composed keys {cloud-owner},
{cloud-region-id}``

This will result in upgrading the multicloud APIs and code refactoring.

We'd better to have common terminologies to facilitate the description below. Take the identity token API
as example:

::

  http(s)://{service IP}:{service port}/api/multicloud/v0/{cloud-owner}_{cloud-region-id}/identity/v3/auth/tokens
  e.g. http://1.2.3.4:9001/api/multicloud/v0/OnaplabOwner_RegionOne/identity/v3/auth/tokens


This API consists of several parts referred as below terminologies:


 - **Terminology         | Description                   | Example**
 - **service endpoint**    | http(s)://{service IP}:{service port}| http://1.2.3.4:9001
 - **service namespace**   | api/{service-name}            | api/multicloud
 - **service API version** | v0, v1, etc.                  | v0
 - **ID of a cloud region**| the ID to specify a cloud region| OnaplabOwner_RegionOne
 - **proxied API catalog** | identity,compute, network, image,volume,etc.| identity
 - **proxied API endpoint**| consists of all above| *http://1.2.3.4:9001/api/multicloud/v0/OnaplabOwner_RegionOne/identity*
 - **proxied API resource**| URI for an OpenStack resource | v3/auth/tokens

Given the terminology above, the general rules to upgrade MultiCloud North Bound API are:
 - Upgrade "service API version" from "v0" to "v1"
 - Change "ID of cloud region" from "{cloud-owner}_{cloud-region-id}" to "{cloud-owner}/{cloud-region-id}"

The upgraded API for identity token API looks like:

::

  http(s)://{service IP}:{service port}/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/identity/v3/auth/tokens
  e.g. http://1.2.3.4:9001/api/multicloud/v0/OnaplabOwner/RegionOne/identity/v3/auth/tokens


``Proposal 2: decouple the cloud region's {cloud-region-id} from OpenStack's Region ID``

Instead of populating the AAI's cloud-region-id with OpenStack Region ID, decoupling of them by put OpenStack Region ID
into other property of a cloud region object would enable the flexibility of populating arbitrary string to AAI's
cloud-region-id. There are several options to implement that, with the intention of maintaining backward compatibility
and minimized impact on AAI's schema, the proposed design is described as below.


There is a property of cloud region object named "cloud-extra-info"
..
https://wiki.onap.org/display/DW/AAI+REST+API+Documentation+-+Beijing

::

   cloud-extra-info: string
     ESR inputs extra information about the VIM or Cloud which will be decoded by MultiVIM.

the intention of this property is to enable the extending of cloud region object without impact AAI's schema. How and when to use this property is up to each multicloud
plugin respectively. This property can be populated by ONAP users through ESR VIM registration GUI Portal (the input field label: "Cloud Extra Info"). The best practice to utilize this "cloud-extra-info" property is that ONAP users to input format json string, with
which extra configuration data can be serialized as {"key":"value"} into this json string. And the corresponding MultiCloud plugin decode and utilize the input key-value pairs.
..
https://wiki.onap.org/pages/viewpage.action?pageId=25431491

**This proposal changes and workflow**:

1. Define a key "openstack-region-id" with value populated by OpenStack Region ID,
     e.g. "RegionOne", "RegionTwo", etc. which must align to the represented OpenStack instance.
2. ONAP user should put this key-value pair into "cloud-extra-info" property via ESR GUI Portal, the input string
    looks like: "{\"openstack-region-id\":\"RegionOne\"}"
3. the corresponding MultiCloud plugin should decode this string to extract this key-value pair "openstack-region-id",
     and use the value as parameter to interact with represented OpenStack Instance.
4. Given the workflow above, the AAI's "cloud-region-id" can be populated by arbitrary string.
5. In cases that either ONAP user doesn't input the key-value pair of "openstack-region-id" into "cloud-extra-info" or MultiCloud Plugin does not support the decoding/using key-value pair "openstack-region-id", the legacy constraint should be applied, that is: ONAP user should make sure AAI's "cloud-region-id" is populated by OpenStack Region ID.


