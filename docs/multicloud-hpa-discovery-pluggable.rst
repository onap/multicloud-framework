.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware, Inc.

==================================
MultiCloud HPA Discovery Pluggable
==================================

To better support more HPA capabilities without modifying the implementation code 
for Multicloud. So we need to extract the HPA discovery part of the code and make
it into a plug-in.


Problem Description
===================

Currently, every time we add an HPA feature, we need to rebuild the image. You 
also need to re-mirror and redeploy during the test. Production environment 
needs to remove the original container, create new container which has great
impact for business continuity.


Propose Change
==============

Extract the original hpa discovery part code and make it into a plug-in.
we need to add hook in openstack, kubernetes and others plugin. then we develop
plug-ins for various languages.

We plan to develop python language and golang language.


Usage
=====
For python
python setup.py in openstack plugin


Tests
=====

#. Unit Tests with tox

Reference
=========
https://wiki.onap.org/display/DW/Multicloud+HPA+Discovery+Pluggable