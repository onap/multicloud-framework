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

This document provides the release notes for the ``honolulu`` release.

Summary
=======

This release introduces new features into k8s plugin that enhance Day 2 configuration support for k8s resources and brings features for monitoring of CNF health. 

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
|                           | onap/multicloud-k8s:0.8.1                       |
+---------------------------+-------------------------------------------------+
| **Release designation**   | honolulu 8.0.0                                  |
|                           |                                                 |
+---------------------------+-------------------------------------------------+


New features
------------

New features in k8s Plugin:
- Refined Configuration API allows for flexible modification of the CNF configuration. Configuration API allows to create, modify and delete k8s resource templates and it allows their parameterization base on dedicated or inherited from the CNF instance input parameters.

- Query API allows for the gathering of the filtered out the status of CNF what can be helpful to get precise information for particular resources belonging to the CNF. Query API produces results in the same format as Status API that offers the full set of information about CNF status.

- HealthCheck API allows for the execution of dedicated healthcheck jobs (similar to helm test operation) that can verify on demand the current status of the CNF. The API can be used also to retrieved the results of healthcheck job execution, which can be extended with Status/Query API

All new the features of k8splugin are explained and showcased as a part of `vFW CNF Use Case <https://docs.onap.org/projects/onap-integration/en/honolulu/docs_vFW_CNF_CDS.html>` and in the k8s API documentation.

**Bug fixes**

N/A

**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  MultiCloud OpenStack: image creating API cannot handle large image file

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  MultiCloud OpenStack: API request to multicloud with authorization header will be rejected

- `MULTICLOUD-601 <https://jira.onap.org/browse/MULTICLOUD-601>`_
  MultiCloud k8s: move to sigs yaml from ghodss

- `MULTICLOUD-661 <https://jira.onap.org/browse/MULTICLOUD-661>`_
  MultiCloud k8s: OVN Installation issues

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
