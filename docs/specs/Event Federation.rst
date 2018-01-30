..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=================
Event/Alert/Metrics Federation
=================

As a cloud mediation layer, Multicloud could be invoked by many projects, through Olso notification listener, Multicloud will
provide VM status/events check and also can customize the type of event which user would like to subscribe. There are six
kinds of VM status can be chosen: DELETE, PAUSE, POWER_OFF, REBUILD，SHUT_DOWN, SOFT_DELETE. Once any change of VM status
is detected of the given type, Multicloud will catch the event and throw it to DMaaP. APPC and VFC can no longer poll the
 Nova by cdp poll but just listen to DMaaP to get the notification of VM status.



Problem Description
===================

This spec is to extend multicloud to support publishing and subscribing events/notification through DMaaP


Use Cases
===================

One typical use case is to allow users to pub/sub messages just by DMaaP instead of CDP Poll or other stuff, Can be
integrated with APPC and VFC instead of CDP poll, may drastically reduce the time of close loop of an event


Proposed change
===================
The proposed change will include two parts: * listener: to listen the events of the status change VM * publisher: to
throw the event to DMaaP. This feature must be dicussed with APPC team which hasn't been done yet.
