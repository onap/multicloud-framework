..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.
.. _offeredapis:

====================================
Elastic API exposure for Multi Cloud
====================================

This spec is to provide a framework for Multi-Cloud to expose API.

Problem Description
===================

Multi-Cloud provides VIM API for other projects in ONAP. API will vary for
different projects. However, Multi-Cloud exposes its API by static code.
Current way of API exposing produces code duplications.

#. When a client creates a resource through Multi-Cloud, Multi-Cloud needs
to convert the API request to back-end OpenStack API request and send to
OpenStack. When a client requests a resource through Multi-Cloud, Multi-Cloud
needs to retrieve OpenStack resource, converts to its API and reply to client.
Even though the two conversion are the same thing with different directions,
there are 2 sets of code for it.

#. Most of Multi-Cloud API shares same logic. But the code of this same logic
are duplicated for every API.

Given the fact mentioned above, current code amount of Multi-Cloud are larger
than it should be. It makes code maintaining be time-consuming and error-prone.

Besides, the swagger files that describe API of Multi-Cloud are maintained
manually. It is thousands lines of code and hard for developers to maintain.

Proposed Change
===============

This spec proposes using YAML files to describe Multi-Cloud API. A framework
will also be provided. When Multi-Cloud services start up, the framework will
read YAML files, parse them and generate API accordingly. Multi-Cloud can
dynamically expose API in this way without changing its Python code. And
developers only need to maintain YAML files that describe Multi-Cloud API.
The YAML files are expected to be less amount than current way of API exposing,
because it only contains metadata of Multi-Cloud API.

Using the proposal in this spec, metadata of API are defined in YAML files and
logic of API handling are concentrated in the framework mentioned above. So
that the code duplication can be eliminated.

To narrow down the scope of this spec, none of current Multi-Cloud API will be
changed. This spec will ONLY focus on migrating Multi-Cloud API from current
way to the proposed framework in this spec. However, migrating all API to the
proposed framework is out of the scope of this spec. A set of API for one
specific use case, for example VoLTE, will be migrated to proposed framework.
Migrating all API can be implemented in other workitem(s) in future.

To narrow down the scope of this spec, a full, normative definition of API and
resources will not be considered. Only partial API will be considered. But it
can be implemented in other workitem(s) in future.

To narrow down the scope of this spec, only the functionality that Multi-Cloud
has now will be considered and extension support will not be considered in this
spec. But it can be implemented in other workitem(s) in future.

It should be noted that, though this spec focuses on how to convert northbound
and southboud API, it doesn't prevent tieing northbound API of MultCloud with
other functionalities. In setion `Definition of API`, an example of API
definition has been given, developer can add specific code/module path as a
attribute(like `handler`) under `path`, instead of defining `vim_path`. By
doing that, developer can tie the northbound API with specific code/module,
and expose northbound API with any functionality. This spec just shows
the capability of doing this by using the elastic API exposure framework, the
implementation for now will still focus on the northbound and southboud API
conversion.

It should be noted that there is a prior art in OpenStack "Gluon" [1]_ project
which provides a model-driven framework to generate APIs based on model
definitions
in YAML. A full, normative definition and extension mechanism of "API
Specification"
[2]_ is available in Gluon. Although our current work has limited scope, for
those
who are interested in full normative definition and extension mechanism in our
future
work, please refer to those references in "Gluon" [1]_ project and its "API
Specifications" [2]_.

.. [1] https://wiki.openstack.org/wiki/Gluon
.. [2] https://github.com/openstack/gluon/blob/master/doc/source/devref/gluon_api_spec.inc

Since the API are defined by YAML files, swagger files can also be generated
from YAML files and exist without manually maintaining. The framework will
cover the conversion from YAML file to swagger files.

To keep backward compatibility, the proposal in this spec will be bound to
[MULTICLOUD-150]_.
This means that the proposal is only usable when evenlet with pecan is
enabled. So that uses don't care about this feature will not be affected.

.. [MULTICLOUD-150] https://jira.onap.org/browse/MULTICLOUD-150


Definition of API
-----------------

Take the API of `host` as example. The API will be defined as follow. URLs of
the API are defined under `paths`. There are several attributes for the API.
The number of kinds of attributes is not constrained to following example,
other attributes can be added if needed.

::

    paths:
      /{vimid}/{tenantid}/hosts/{hostid}:
        parameters:
          - type: string
            format: uuid
            name: vimid
          - type: string
            format: uuid
            name: tenantid
          - type: string
            format: uuid
            name: hostid
        get:
          responses:
            success_code: 200
            description: content of host
            schema: host
        vim_path: {nova_endpoint}/os-hypervisors

parameters
~~~~~~~~~~

`parameters` are the variables in the URL. It can be extracted from URL and
then used in data retrieving and manipulating.

`parameters` are discriminated by `name`, and validated by `type` and `format`.

post, put, get, delete
~~~~~~~~~~~~~~~~~~~~~~

These attributes represents the supported HTTP method. In above example, only
`get` method is defined. When client sends other HTTP method to the URL, a 404
response will be returned.

`responses` defines the response of the request. `success_code` is the HTTP
code in the response. `description` is an optional parameter. It describes the
response.
`schema` points to the RESTful resource that will be in the response body. In
above example, the RESTful resource is `host`. It should be found in the
RESTful resource definition section.

vim_path
~~~~~~~~

`vim_path` defines the relative URL path of the southbound VIM. Multi-Cloud
will use this path to retrieve data from VIM.

Definition of RESTful resource
------------------------------

Take the resource `host` as example. The resource will be defined as follow.
Resources are defined under `definitions`. The are several attributes for the
resource. The number of kinds of attributes is not constrained to following
example, other attributes can be added if needed.

::

    definitions:
      host:
        vim_resource: hypervisor
        properties:
          name:
            type: string
            required: true
            source: hypervisor.name
          cpu:
            type: integer
            minimal: 1
            source: hypervisor.vcpus
            action: copy
            required: true
          disk_gb:
            type: integer
            minimal: 0
            source: hypervisor.local_disk_size
            required: true
          memory_mb:
            type: integer
            minimal: 0
            source: hypervisor.memory_size
            required: true

vim_resource
~~~~~~~~~~~~

`vim_resource` points to the resource that comes from southbound VIM.
Multi-Cloud will use the resource to build its own resource.

properties
~~~~~~~~~~

`properties` defines the properties of the resource. Each property has a name
and several attributes. The number of kinds of attributes is not constrained
to the example, other attributes can be added if needed.

`type` of property means the type of current property. It can be some simple
data,
like string or integer. It can also be some composite data like, object or
array.

`required` of property means if this property is required for the resource. If
it is required, missing this property will cause request failure. Default value
of `required` is false.

`source` of property means that current property will be built from it. It is
usually a property from `vim_resource`. By default, it will be the same
property in `vim_resource`.

`action` of property means that current property will be build by using this
action.
By default, it will be `copy`, which means the data from property of VIM
resource
is copied to property of Multi-Cloud resource. Other actions can be defined for
different scenarios.

`minimal` is one of the constraint of the property. It means the minimal
possible
value of the property. If value of the property is less than minimal value. The
request will fail.

Swagger File generation
-----------------------

Multi-Cloud is using Swagger file to describe its API. It is maintained
manually.
Since this spec proposes to use YAML file to generate Multi-Cloud's API,
Swagger
file can also be generated from YAML file. The API generating framework will
also
generate Swagger file.

Implementation
==============

Work Items
----------

#. Add YAML parser for API and resource.
#. Add REST client to call southbound VIM API.
#. Add validator for resource.
#. Add action for resouce.
#. Add Swagger file generator.
#. Migrate /{vimid}/{tenantid}/hosts/{hostid} as an example.
