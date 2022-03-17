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

This document provides the release notes for the ``istanbul`` release.

Summary
=======


Version: 9.0.1
--------------

The ``istanbul`` maintenance release addresses some vulnerabilities mainly
for the log4j dependencies.

- Updated the direct dependency log4j libraries to 2.14.1
- Please note log4j is still on older versions in a transitive dependencies for
  * onap/multicloud-framework-artifactbroker:1.7.2


Version: 9.0.0
--------------

This release introduces new features in k8splugin, including many bug-fixes bringing better compatibility with Helm 3.5 specification.

Release Data
============

+---------------------------+-------------------------------------------------+
| **Project**               | MultiCloud                                      |
|                           |                                                 |
+---------------------------+-------------------------------------------------+
| **Docker images**         | onap/multicloud-framework:1.7.1                 |
|                           | onap/multicloud-framework-artifactbroker:1.7.1  |
|                           | onap/multicloud-openstack-starlingx:1.5.6       |
|                           | onap/multicloud-openstack-windriver:1.5.6       |
|                           | onap/multicloud-openstack-fcaps:1.5.6           |
|                           | onap/multicloud-openstack-pike:1.5.6            |
|                           | onap/multicloud-k8s:0.9.3                       |
+---------------------------+-------------------------------------------------+
| **Release designation**   | istanbul                                        |
|                           |                                                 |
+---------------------------+-------------------------------------------------+


New features
------------

New features in k8s Plugin related with CNF support implemented by *REQ-627*:

- Implement Query API on the main level to let reading the k8s resources for specified cluster and namespace but not related with particular Instance
- Implement pre- and post-install/delete hooks
- Modify Instance API POST Response to include hook information
- Update Ready flag in Status API to indicate the real status of the Instance
- Update Status API and Instance API GET to return hook information but only when additional query param is specified

- `<https://jira.onap.org/browse/MULTICLOUD-1345>`_
- `<https://jira.onap.org/browse/REQ-627>`_

**Bug fixes**

- `MULTICLOUD-1269 <https://jira.onap.org/browse/MULTICLOUD-1269>`_
  MultiCloud k8s: K8s Plugins keeps failed RB Instance

- `MULTICLOUD-1332 <https://jira.onap.org/browse/MULTICLOUD-1332>`_
  MultiCloud k8s: k8s resource from configuration are not being deleted with instance

- `MULTICLOUD-1334 <https://jira.onap.org/browse/MULTICLOUD-1334>`_
  MultiCloud framework: Update or Remove Java 8

- `MULTICLOUD-1338 <https://jira.onap.org/browse/MULTICLOUD-1338>`_
  MultiCloud k8s: Foreground delete policy prevents deletion of the pods

- `MULTICLOUD-1377 <https://jira.onap.org/browse/MULTICLOUD-1377>`_
  MultiCloud k8s: Wrong parameter used for creation of rb-definition-version

- `MULTICLOUD-1397 <https://jira.onap.org/browse/MULTICLOUD-1397>`_
  MultiCloud k8s: CRD installation problem

- `MULTICLOUD-1398 <https://jira.onap.org/browse/MULTICLOUD-1398>`_
  MultiCloud k8s: K8s Resource Delete order the same like for installation

- `MULTICLOUD-1409 <https://jira.onap.org/browse/MULTICLOUD-1409>`_
  MultiCloud k8s: Query API for Instance returns resources that do not belong to Instance

- `MULTICLOUD-1414 <https://jira.onap.org/browse/MULTICLOUD-1414>`_
  MultiCloud k8s: Config API takes values only from Config create request

**Known Issues**

- `MULTICLOUD-359 <https://jira.onap.org/browse/MULTICLOUD-359>`_
  MultiCloud OpenStack: image creating API cannot handle large image file

- `MULTICLOUD-421 <https://jira.onap.org/browse/MULTICLOUD-421>`_
  MultiCloud OpenStack: API request to multicloud with authorization header will be rejected

- `MULTICLOUD-601 <https://jira.onap.org/browse/MULTICLOUD-601>`_
  MultiCloud k8s: move to sigs yaml from ghodss

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
<https://wiki.onap.org/display/SV/Istanbul+Multicloud>`_.

Workarounds
-----------

N/A

Security Notes
--------------

**Fixed Security Issues**

CVE issue pertained to multicloud-openstack components reported
by https://wiki.onap.org/display/SV/Istanbul+Multicloud
has been fixed by upgrading dependencies

CVE issue pertained to multicloud-framework components reported
by https://wiki.onap.org/display/SV/Istanbul+Multicloud
has been fixed by upgrading dependencies

**Known Security Issues**


Fixing of CVE issue pertained to multicloud-framework-artifactbroker components
reported by https://wiki.onap.org/display/SV/Istanbul+Multicloud
is an ongoing effort


Fixing of CVE issue pertained to multicloud-openstack-vmware components
reported by https://wiki.onap.org/display/SV/Istanbul+Multicloud
will not be fixed due to lack of commitment from community


Test Results
============

N/A

References
==========

For more information on the ONAP ``istanbul`` release, please see:

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

  - `Project Vulnerability Review Table for Multicloud <https://wiki.onap.org/display/SV/Istanbul+Multicloud>`_

  - `Multicloud K8s Plugin Service APIs <https://wiki.onap.org/display/DW/MultiCloud+K8s-Plugin-service+API's>`_
