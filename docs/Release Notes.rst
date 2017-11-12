=============
Release Notes
=============

Release Date
------------
2017-11-16


New Features
------------
* keystone proxy to support original OpenStack consumed modules
* Mutiple VIM registry and unregistry
* Resources LCM functions
* Hierarchical bindig based SDN integration in each Datacenter
* Fcaps basic data collection support, OS panic alert is thrown out as an example
* Auto-deployment support to both K8s and heat
* Fake cloud based Unit and system test framework
* Complete code coverage detection and CSIT framework
* Complish plugins of different backends, including: Vanilla OpenStack (based on Ocata) and commercial Clouds including OpenStack (including Titanium - Mitaka from Wind River and VIO - Ocata from VMware) 

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
