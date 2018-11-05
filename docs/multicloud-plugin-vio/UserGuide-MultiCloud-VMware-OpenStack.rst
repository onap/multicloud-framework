.. This work is licensed under a Creative Commons Attribution 4.0
.. International License.  http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware Inc.

=================================================
MultiCloud Plugin for VMware Integrated OpenStack
=================================================


Supported features
~~~~~~~~~~~~~~~~~~

Northbound APIs for SO
----------------------

MultiCloud VIO Plugin supports OpenStack services proxy APIs since the beginning, and later
in Casablanca release, VIO plugin supports infra_workload API for SO. Use cases like vFW/vDNS
will leverage these APIs to instantiate VNFs.

Northbound APIs for VF-C
------------------------

MultiCloud VIP Plugin supports VF-C by legacy Open-O APIs, and these APIs evolved according
to the requirements of VF-C and use cases. VoLTE use case leverage these APIs to instantiate
VNFs.

Support Placement Policies for OOF
----------------------------------

Since Beijing release, to help OOF to make better placement decision, MultiCloud expose a capacity
check API.

Support OpenStack Resources Discovery
-------------------------------------

When onboarding new VMware OpenStack through ESR UI, VIO plugin could discover current OpenStack
Resources like flavors/images/networks/hypervisors, and update them in A&AI.

Support HPA Discovery
---------------------

The HPA information will be automatically discovered and registered during VIM onboarding process.

Support Cloud Agnostic Placement Policies
-----------------------------------------

The cloud agnostic information like Guarantee/Burstable QoS will be automatically discovered and
registered during VIM onboarding process.

Support Auto-Healing Close Loop
-------------------------------

Leveraging VESAgent for meter collectrion and healthy report, VIO plugin supports
the auto-healing/auto-scaling scenarios in ONAP.

Support Events Federation
-------------------------

Federate the events of VIM layer with ONAP message bus, it provide direct help to HA fencing and improve the
efficiency of VM recover with performance verification.


Supported Use Cases
~~~~~~~~~~~~~~~~~~~

**vFW/vDNS**


**vCPE**


**VoLTE**



Cloud Agnostic Placement Policy Enablement
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Currently two types of cloud agnostic intent policies are supported by VIO plugin, Guaranteed QoS and Burstable QoS.
VIO plugin will register them when VIM onboard and OOF could make better placement decision based on these policies.

**Enable on it on flavors**

Discovering these kind of features are through flavor's extra-specs, when you set `quota:cpu_reservation_percent=100`
and `quota:memory_reservation_percent=100` in flavor, it will be recognized as `Guaranteed QoS` supported platform.
When you choose other percentage for CPU and memory reservation, it will be recognized as `Burstable QoS` supported platform.
Related information will be registered to A&AI.

.. include:: Multicloud-Fake_Cloud-Guide.rst
