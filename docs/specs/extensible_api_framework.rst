..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

========================
Extensible API Framework
========================

To comply with Scalability, Security, Stability and Performance(S3P), a new
API framework is required.

Problem Description
===================

Current Multi-Cloud API framework is based on Django. Django itself is
monolithic and code pattern must follow Django object-relational mapping(ORM).
Plus, Multi-Cloud runs Django by using Django's built-in webserver currently.
According to Django Document[Django_Document]_, this mode should not be used
in production. This mode has not gone through security audits or performance
tests, and should only be used in development.

.. [Django_Document] https://docs.djangoproject.com/en/dev/ref/django-admin/#runserver-port-or-address-port

On the other side, Multi-Cloud uses static mapping for its APIs. That is,
whenever a new API should be enabled, someone has to add some customized
code for that API. With the growing of numbers of ONAP use cases, more and
more APIs need to be added in Multi-Cloud. The current way to enable a new
API is not scalable.

Proposed Change
===============

To provide a scalable, secure, high-performance API framework, a new
extensible API framework is required. New API framework should contain
following component.

Multi processes framework
-------------------------

Multiple processes can provide a parallel API handler. So, when multiple API
requests come to Multi-Cloud, they can be handled simultaneously. On the other
hand, different processes can effectively isolate different API request. So
that, in Multi-Cloud, one API request will not affect another.

To make multiple processes work together, they need to share a socket file. A
bootstrap process is required and other processes can be forked from bootstrap
process.

Managing multiple processes could be overwhelming difficult and sometimes
dangerous. Some mature library could be used to reduce related work here, for
example oslo.service[oslo_service]_.

.. [oslo_service] https://github.com/openstack/oslo.service


Lightweight API framework
-------------------------

Django is a good web framework. However, it is too monolithic. When deploy
Django, all Django components are deployed together, no matter they are used
or not. Besides, the code style should follow Django ORM, even if it is not
front-end stuff.

A lightweight framework could reduce the size of deployment and help future
architecture change. Pecan[Pecan]_ will be the idea web framework in this case.

.. [Pecan] https://pecan.readthedocs.io/en/latest/

Dynamic API exposure
--------------------

The spec proposes a dynamic way for API exposure. Instead of writing code for
each API, a general framework will be provided. This general framework will
read data model from static files, parse them, and generate HTTP API
accordingly. In this way, new API can be defined in new model files and old
API can be removed by removing stale model files. Code needs no change for API
changes.

YAML could be used to define model. And PyYAML[PyYAML]_ could be used to parse
model files.

.. [PyYAML] https://pyyaml.org/wiki/PyYAMLDocumentation

Backward compatibility
----------------------

To make sure use case and functionalities not be broken, this spec will provide
a configuration option, named `web_framework`. Default value will be `django`,
which will run current code. An alternative value is `pecan`, which will run the
API framework proposed in this spec.

Work Items
==========

#. Use Pecan to build API server.
#. Add multiple processes support.
#. Add YAML file and its parser
#. Add configuration to distinguish new API framework and old one.
#. Update deploy script to support new API framework.