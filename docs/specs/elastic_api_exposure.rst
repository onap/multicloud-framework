..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

====================================
Elastic API exposure for Multi Cloud
====================================

This spec is to provide a framework for Multi-Cloud to dynamically expose API.

Problem Description
===================

Multi-Cloud provides VIM API for other projects in ONAP. API will vary for
different projects. However, Multi-Cloud exposes its API by static code. That
is, whenever a API needs to be added, updated, or removed, someone has to
add/remove some customized code for that. Moreover, the swagger files that
describe API of Multi-Cloud are maintained manually. With the growing of numbers
of ONAP use cases, more and more APIs need to be added in Multi-Cloud. Current
way to maintain API is time-consuming and error-prone.

Proposed Change
===============

This spec proposes exposing Multi-Cloud API dynamically. APIs and RESTful
resources will be defined in YAML files. A framework will be provided to
parse the YAML files. For the northbound of Multi-Cloud, this framework
will expose API according to the definition in YAML files. For the southbound
of Multi-Cloud, this framework will call the related southbound API.

This spec aims to provide a framework to generate Multi-Cloud API from YAML
file. But migrating all API to the framework is out of the scope in this spec.
It could be implemented in other wort items.

Definition of API
-----------------

Take the API of `host` as example. The API will be defined as follow. URLs of
the API are defined under `paths`. The are several attributes for the API. The
number of kinds of attributes is not constrained to following example, other
attributes can be added if needed.

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

`parameters` are the variables in the URL. It can be extracted from URL and then
used in data retrieving and manipulating.

`parameters` are discriminated by `name`, and validated by `type` and `format`.

post, put, get, delete
~~~~~~~~~~~~~~~~~~~~~~

These attributes represents the supported HTTP method. In above example, only
`get` method is defined. When client sends other HTTP method to the URL, a 404
response will be returned.

`responses` defines the response of the request. `success_code` is the HTTP code
in the response. `description` is an optional parameter. It describes the response.
`schema` points to the RESTful resource that will be in the response body. In
above example, the RESTful resource is `host`. It should be found in the RESTful
resource definition section.

vim_path
~~~~~~~~

`vim_path` defines the relative URL path of the southbound VIM. Multi-Cloud will
use this path to retrieve data from VIM.

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

`vim_resource` points to the resource that comes from southbound VIM. Multi-Cloud
will use the resource to build its own resource.

properties
~~~~~~~~~~

`properties` defines the properties of the resource. Each property has a name
and several attributes. The number of kinds of attributes is not constrained
to the example, other attributes can be added if needed.

`type` of property means the type of current property. It can be some simple data,
like string or integer. It can also be some composite data like, object or array.

`required` of property means if this property is required for the resource. If it
is required, missing this property will cause request failure. Default value of
`required` is false.

`source` of property means that current property will be built from it. It is
usually a property from `vim_resource`. By default, it will be the same property
in `vim_resource`.

`action` of property means that current property will be build by using this action.
By default, it will be `copy`, which means the data from property of VIM resource
is copied to property of Multi-Cloud resource. Other actions can be defined for
different scenarios.

`minimal` is one of the constraint of the property. It means the minimal possible
value of the property. If value of the property is less than minimal value. The
request will fail.

Swagger File generation
-----------------------

Multi-Cloud is using Swagger file to describe its API. It is maintained manually.
Since this spec proposes to use YAML file to generate Multi-Cloud's API, Swagger
file can also be generated from YAML file. The API generating framework will also
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