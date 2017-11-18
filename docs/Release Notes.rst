=============
Release Notes
=============

Release Date
------------
2017-11-16


New Features
------------
* Keystone proxy for convenient integration with modules which depend on original OpenStack functions
* Multiple VIM registry and unregister
* Resources LCM functions
* Auto-deployment support to both K8s and heat
* Hierarchical binding based integration with the third party SDN controller
* Basic Fcaps alert collection support, VM abnormal status is thrown out as an example
* Fake cloud based Unit and system test framework
* Complete code coverage detection, CSIT, and document framework
* Provide several plugins of different backbends, including: Vanilla OpenStack (based on Ocata) and commercial Clouds including OpenStack (including Titanium - Mitaka from Wind River and VIO - Ocata from VMware)

Bug Fixes
---------
- `MULTICLOUD-123 <https://jira.onap.org/browse/MULTICLOUD-123>`_
  Append v3 to keystone url by default, if keystone version is missing.

- `MULTICLOUD-102 <https://jira.onap.org/browse/MULTICLOUD-102>`_
  Throw exception in Multi Cloud when backend OpenStack throw exceptions.

- `MULTICLOUD-101 <https://jira.onap.org/browse/MULTICLOUD-101>`_
  Fix failed to add image info to AAI if image name didn't contain '-'.
  

Known Issues
------------
None

Security Issues
---------------
None

Upgrade Notes
-------------
None

Deprecation Notes
-----------------
None

Other
-----
None
