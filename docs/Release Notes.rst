..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=============
Release Notes
=============


Version:
--------
1.1.0

Release Date
------------
2018-05-24


New Features
------------
* Allow to check capacity capability for smart VNF placement across VIMs.
* Declarative template driven framework to generate API dynamically.
* Federate the events of VIM layer with ONAP message bus which provide direct help to HA fencing and improve the efficiency of VM recover with performance verification.
* Enable basic HPA discovery and representing at Multi VIM/Cloud when registry.
* Enable distributed log collection mechanism to a centralized logging analysis system.
* Improve parallelism of Multi VIM/Cloud service framework with performance verification.
* Upload and download images based on Cloud storage capabilities to support remote image distribution requirement.

Bug Fixes
---------
- `MULTICLOUD-225 <https://jira.onap.org/browse/MULTICLOUD-225>`_
  Allow to forward header properties through Multi VIM/Cloud framework

- `MULTICLOUD-221 <https://jira.onap.org/browse/MULTICLOUD-221>`_
  Fix VESAgent health check flow

- `MULTICLOUD-220 <https://jira.onap.org/browse/MULTICLOUD-220>`_
  Fix Multi VIM/Cloud plugins to enable ID binding with each request.


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

====


Version
-------
1.0.0

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

===========

End of Release Notes
