.. This work is licensed under a Creative Commons Attribution 4.0
   International License.
.. http://creativecommons.org/licenses/by/4.0
.. (c) ONAP Project and its contributors
.. _release_notes:

************************
MultiCloud Release Notes
************************

Abstract
========

This document provides the release notes for the ``guilin`` release.

Summary
=======

MultiCloud Project enhances artifactbroker to support CNF orchestration with
artifact format of helm charts (without wrapping in a dummy heat template)


Release Data
============

+---------------------------+-------------------------------------------------+
| **Project**               | MultiCloud                                      |
|                           |                                                 |
+---------------------------+-------------------------------------------------+
| **Docker images**         | onap/multicloud-framework:1.6.0                 |
|                           | onap/multicloud-framework-artifactbroker:1.6.0  |
|                           | onap/multicloud-openstack-starlingx:1.5.5       |
|                           | onap/multicloud-openstack-windriver:1.5.5       |
|                           | onap/multicloud-openstack-fcaps:1.5.5           |
|                           | onap/multicloud-openstack-pike:1.5.5            |
<<<<<<< HEAD   (adacf4 K8s Plugin v1 API Specification)
|                           | onap/multicloud-azure:1.2.4                     |
|                           | onap/multicloud-k8s:0.7.0                       |
=======
|                           | onap/multicloud-k8s:0.8.1                       |
>>>>>>> CHANGE (33d495 Update release note for honolulu k8splugin)
+---------------------------+-------------------------------------------------+
| **Release designation**   | Guilin 7.0.0                                    |
|                           |                                                 |
+---------------------------+-------------------------------------------------+


New features
------------

Enables CNF orchestration with artifact format of helm charts

All new the features of k8splugin are explained and showcased as a part of `vFW CNF Use Case <https://docs.onap.org/projects/onap-integration/en/honolulu/docs_vFW_CNF_CDS.html>` and in the k8s API documentation.

**Bug fixes**

- `MULTICLOUD-1195 <https://jira.onap.org/browse/MULTICLOUD-1195>`_
  Multicloud used wrong MSB URL
  
**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  MultiCloud OpenStack: image creating API cannot handle large image file

- `MULTICLOUD-389 <https://jira.onap.org/browse/MULTICLOUD-389>`_
  MultiCloud OpenStack: keypair cannot be passed for nova instance creation

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  MultiCloud OpenStack: API request to multicloud with authorization header will be rejected

- `MULTICLOUD-601 <https://jira.onap.org/browse/MULTICLOUD-601>`_
  MultiCloud k8s: move to sigs yaml from ghodss

- `MULTICLOUD-661 <https://jira.onap.org/browse/MULTICLOUD-661>`_
  MultiCloud k8s: OVN Installation issues

<<<<<<< HEAD   (adacf4 K8s Plugin v1 API Specification)
=======
- `MULTICLOUD-1269 <https://jira.onap.org/browse/MULTICLOUD-1269>`_
  MultiCloud k8s: K8s Plugins keeps failed RB Instance

- `MULTICLOUD-1295 <https://jira.onap.org/browse/MULTICLOUD-1295>`_
  MultiCloud k8s: Bug in Multicloud K8S Plugin Detemplating

- `MULTICLOUD-1312 <https://jira.onap.org/browse/MULTICLOUD-1312>`_
  MultiCloud k8s: Query API returns 500 instead of 404

- `MULTICLOUD-1329 <https://jira.onap.org/browse/MULTICLOUD-1329>`_
  MultiCloud k8s: Redundant data in MongoDB created

- `MULTICLOUD-1330 <https://jira.onap.org/browse/MULTICLOUD-1330>`_
  MultiCloud k8s: Consul operation interface problems

- `MULTICLOUD-1331 <https://jira.onap.org/browse/MULTICLOUD-1331>`_
  MultiCloud k8s: Instance status update failure
>>>>>>> CHANGE (33d495 Update release note for honolulu k8splugin)

Deliverables
------------

Software Deliverables
~~~~~~~~~~~~~~~~~~~~~


Documentation Deliverables
~~~~~~~~~~~~~~~~~~~~~~~~~~


Known Limitations, Issues and Workarounds
=========================================

System Limitations
------------------

N/A


Known Vulnerabilities
---------------------


MULTICLOUD code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain open have
been assessed for risk and determined to be false positive.

The MULTICLOUD open Critical security vulnerabilities and their risk
assessment have been documented as part of the
`project
<https://wiki.onap.org/pages/viewpage.action?pageId=68541501>`_.


Workarounds
-----------

N/A


Security Notes
--------------

**Fixed Security Issues**

N/A

**Known Security Issues**

N/A

Test Results
============

N/A

References
==========

For more information on the ONAP ``guilin`` release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_


.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://wiki.onap.org
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org


Quick Links
===========

  - `MULTICLOUD project page <https://wiki.onap.org/pages/viewpage.action?pageId=6592841>`_

  - `Passing Badge information for MULTICLOUD <https://bestpractices.coreinfrastructure.org/en/projects/1706>`_

  - `Project Vulnerability Review Table for Multicloud <https://wiki.onap.org/pages/viewpage.action?pageId=68541501>`_

  - `Multicloud K8s Plugin Service APIs <https://wiki.onap.org/display/DW/MultiCloud+K8s-Plugin-service+API's>`_
