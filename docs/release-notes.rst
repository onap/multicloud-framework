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

This document provides the release notes for the ``jakarta`` release.

Summary
=======


Version: 11.0.0
---------------

There are no updates to the Multicloud project for the Kohn release


Release Data
============

+---------------------------+-------------------------------------------------+
| **Project**               | MultiCloud                                      |
|                           |                                                 |
+---------------------------+-------------------------------------------------+
| **Docker images**         | onap/multicloud-framework:1.9.0                 |
|                           | onap/multicloud-framework-artifactbroker:1.9.0  |
|                           | onap/multicloud-openstack-starlingx:1.5.7       |
|                           | onap/multicloud-openstack-windriver:1.5.7       |
|                           | onap/multicloud-openstack-fcaps:1.5.7           |
|                           | onap/multicloud-openstack-pike:1.5.7            |
|                           | onap/multicloud-k8s:0.10.1                      |
+---------------------------+-------------------------------------------------+
| **Release designation**   | kohn                                            |
|                           |                                                 |
+---------------------------+-------------------------------------------------+


New features
------------

N/A

**Bug fixes**

N/A

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

- `MULTICLOUD-1459 <https://jira.onap.org/browse/MULTICLOUD-1331>`_
  MultiCloud k8s: Multicloud-k8s dockers contain GPLv3

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

For more information on the ONAP ``kohn`` release, please see:

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
