..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.


======================================================
API Resource Model Definition for Elastic API Exposure
======================================================

Elastic API Exposure supports the processing of a specification document to
define an object-oriented API. The API specification uses YAML syntax to define
the structure and relationships of the objects that are manipulated by the
API. This specification defines metadata definitions that can be used
for the automatic generation of human readable API documentation.

The specification allows for the definition of two types of objects: base
objects and API objects.  A base object is an object specification that does
not have an API definition field.  An object with an API definition field is
an API object. A base object is used to define a set of attributes that can be
included in other object definitions.  Base objects can extend other base
objects but not API objects.  An API object can only extend base objects. No
code is created for base objects.  The Elastic API framework will generate code
for API objects.

For each API object, the Elastic API framework will generate code for database
storage and code to support a REST API to manipulate the object.  The API
endpoints created will support the basic CRUD operations on the object. Each
API object will have a corresponding database table.  Each API object is
required to have a primary key field.  The primary key field is used as the
identifier in the generated API endpoints.  For example, if we define an API
object for a Host with a path name of ``hosts``, the following API URL endpoints
will be generated where <host_id> is the primary key.

.. csv-table::
   :header: "Operation", "URL", "Description"
   :widths: 5, 15, 15

   POST, /<baseURL>/<api_name>/<version>/hosts,   Create Host object
   PUT, /<baseURL>/<api_name>/<version>/hosts/<host_id>,   Modify Host object
   GET, /<baseURL>/<api_name>/<version>/hosts,   Get all Host objects
   GET, /<baseURL>/<api_name>/<version>/hosts/<host_id>,   Get one Host object
   DELETE, /<baseURL>/<api_name>/<version>/hosts/<host_id>,   Delete a Host object

The content type for all of the operations is ``application/json``.

The API objects can have pointer relationships and parent/child relationships
to other API objects.  A pointer relationship can be created by defining a
field that uses the object name of another object as its type.  When this type
of relationship is specified, the primary key of the referenced object becomes
a foreign key in the object's database table.  A parent/child relationship can
be created at the API level by specifying the parent object name in an
object's API definition (see ApiDef_).  When a parent/child relationship is
specified, a pointer relationship from the child object to the parent object
is automatically created using the primary key of the parent.

In addition, different API endpoints are generated to manipulate the child
object.  For example, assume we define an API object for Port and another API
object for Interface where the Interface is a child of the Port.  If the path
names are ``ports`` and ``interfaces``, the following API URL endpoints for the
Interface object would be generated.

.. list-table::
   :widths: 5 15 16
   :header-rows: 1

   * - Operation
     - URL
     - Description
   * - POST
     - /<baseURL>/<api_name>/<version>/ports/<port_id>/interfaces
     -  Create Interface object
   * - PUT
     - /<baseURL>/<api_name>/<version>/ports/<port_id>/interfaces/<interface_id>
     -  Modify Interface object
   * - GET
     - /<baseURL>/<api_name>/<version>/ports/<port_id>/interfaces
     -  Get all Interface objects for Port
   * - GET
     - /<baseURL>/<api_name>/<version>/ports/<port_id>/interfaces/<interface_id>
     -  Get one Interface object
   * - DELETE
     - /baseURL/<api_name>/<version>/ports/<port_id>/interfaces/<interface_id>
     -  Delete an Interface object

This document describes the specification for defining an API.

Schema Definition
-----------------

The schema exposes two types of fields. Fixed fields, which have a declared
name, and Patterned fields, which declare a regex pattern for the field name.
Patterned fields can have multiple occurrences as long as each has a unique
name.  Each field will have a value that is defined as a primitive type or as
an JSON object.  The JSON objects are very similar to the Schema Object found
in Swagger.  However, some extensions are added and only a small subset of the
properties are supported.

Primitive Data Types
--------------------

.. list-table::
   :widths: 15 20 30
   :header-rows: 1

   * - Type
     - Description
     - Associated Properties
   * - integer
     - Integer number
     - - format: int32, int64  (default: int32)
       - min: <integer>
       - max: <integer>
   * - number
     - Floating point number
     - n/a
   * - string
     - Text String
     - - length: <integer> (default: 255)
       - format: date-time, json, ipv4, ipv6, mac, url, email
   * - boolean
     - Boolean value (true/false)
     - n/a
   * - uuid
     - Text string in UUID format
     - n/a
   * - enum
     - Text string from a list of values
     - - values: [<string>]

File Structure
--------------

The API is defined by a single file.  The Root Object is defined by the
MultiVimApiDef object.

MultiVimApiDef
~~~~~~~~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   file_version, string,  true, API Resource File Version
   imports, string, false, File path to common base object definitions
   info, InfoDef_,  true, Metadata for this API specification that is useful in the generation of human readable API documentation.
   objects, ObjectsDef_,  true, Object definitions for this API

**Example**

::

  file_version: 1.0
  imports: base/base.yaml
  info:
    name: example-api
    version: 1.0
    description "An Example API Specification"
    author:
      name: "MultiVIM Team"
      url: https://wiki.onap.org/
      email: bh526r@att.com
  objects:
    Port:
      api:
        name: host
        plural_name: hosts
      extends: BaseHost
      attributes:
        alarms:
          type: string
          length: 255
          description: "Alarm summary for host"
    ...

The MultiVimApiDef is the root object for the API specification. The ``file_version``
is used to identify the format used to create this file. The ``info`` field
contains the metadata about the API.  The ``objects`` field contains the base
and API object definitions for the API.

InfoDef
~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   name, string,  true, Name of the API
   version, string,  true, Version of the API
   description, string,  false, Description of the API
   author, AuthorDef_,  false, Information about API authorship

The InfoDef is where metadata about the API can be specified.  At a minimum the
``name`` and ``version`` of the API must be specified.

**Example**

::

  name: example-api
  version: 1.0
  description "An Example API Specification"
  author:
    name: "MultiVIM Team"
    url: https://wiki.onap.org/
    email: bh526r@att.com

AuthorDef
~~~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   name, string,  true, Name of the author
   url, string,  false, URL to author website
   email, string,  false, Email address of author


The AuthorDef allows authorship information about the API to be specified.
This information is optional.

**Example**

::

  name: "MultiVIM Team"
  url: https://wiki.onap.org/
  email: bh526r@att.com

ObjectsDef
~~~~~~~~~~

.. csv-table::
   :header: "Pattern Field", "Type", "Required", "Description"
   :widths: 10, 5, 3, 20

   [_a-zA-Z][_a-zA-Z0-9]*, ObjectDef_,  true, Field/Value Object definitions

The ObjectsDef allows one or more objects to be specified for the API.  The
field name should follow the lexical definitions for a Python identifier.

**Example**

::

      NetworkService:
        api:
          name: network
          plural_name: networks
        extends: BaseService
        attributes:
          ipv4_family:
            type: string
            length: 255
            description: "Comma separated list of route target strings"
          ipv6_family:
            type: string
            length: 255
            description: "Comma separated list of route target strings"

ObjectDef
~~~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   api, ApiDef_,  false, API path information for object
   extends, string,  false, Name of a base object definition to extend
   attributes, AttributesDef_,  true, Attribute definitions of object
   policies, PolicyDef_, false, Access rules for this API object

The ObjectDef defines either a base object or an API object.  If the ``api``
field is present, it is an API object.  If the ``api`` field is omitted, it is a
base object.  The ``extends`` field (if present) must specify the ObjectDef name
of another base object.  The ``policies`` field is only allowed for an API
object except that default policies are defined in ``BaseObject`` and other base
objects at system level. This is because both ``BaseObject`` and other
base objects are expected to be extended by other user-defined API objects.
If the ``policies`` field is omitted in those API objects, default policies
that are inherited from super class will apply. Or in rare cases, if a user-defined
API object does not extend ``BaseObject`` or other base object,
and no ``policies`` field is defined in the API object either,
no access control is applied to the API object.

**Example**

::

    api:
      name: port
      plural_name: ports
    extends: BasePort
    attributes:
      alarms:
        type: string
        length: 255
        description: "Alarm summary for port"
    policies:
      create: "rule:admin_or_owner"
      delete: "rule:admin_or_owner"
      list: "rule:admin"
      get: "rule:admin_or_owner"
      update: "rule:admin_or_owner"


ApiDef
~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   name, string,  true, Singular path name for the object
   plural_name, string,  false, Plural path name for the object
   parent, string,  false, Name of an ObjectDef specification

The ApiDef defines the API path and optionally a parent/child relationship for
the object.  The ``parent`` field (if present) must specify the ObjectDef name
of another API object.  The ``name`` field is used by the generated CLI code to
identify the object to be manipulated.  The ``plural_name`` field is used by the
generated API code as part of the path to identify the object to be manipulated.
If the ``plural_name`` field is omitted, an 's' character is added to the name
for the API path during code generation.

**Example**

::

  name: interface
  plural_name: interfaces
  parent: Port

PolicyDef
~~~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   create, string,  false, Rule specifier string
   delete, string,  false, Rule specifier string
   list, string,  false, Rule specifier string
   get, string,  false, Rule specifier string
   update, string,  false, Rule specifier string

The PolicyDef defines the Role-Based Access Control (RBAC) for the object.  The
access to the object can be controlled for each generated action.

**Example**

::

  create: "rule:admin_or_network_owner"
  delete: "rule:admin_or_network_owner"
  list: "rule:admin"
  get: "rule:admin_or_owner"
  update: "rule:admin_or_network_owner"

AttributesDef
~~~~~~~~~~~~~

.. csv-table::
   :header: "Pattern Field", "Type", "Required", "Description"
   :widths: 10, 5, 3, 20

   [_a-zA-Z][_a-zA-Z0-9]*, AttributeSchemaDef_,  true, Field/Value Attribute definitions

The AttributesDef allows one or more attributes to be specified for the
object.  The field name should follow the lexical definitions for a Python
identifier.

**Example**

::

  id:
    type: uuid
    required: true
    primary: true
    description: "UUID of Interface instance"


AttributeSchemaDef
~~~~~~~~~~~~~~~~~~

.. csv-table::
   :header: "Fixed Field", "Type", "Required", "Description"
   :widths: 5, 5, 3, 20

   type, string,  true, Primitive data type or ObjectDef name
   primary, boolean, false, Primary key for object (if true)
   description, string,  false, Description of the attribute
   required, boolean, false, Required flag for object creation (default: false)
   length, integer, false, Length if type is string (default: 255)
   values, [string], false, Array of strings (required if type is enum)
   format, string, false, Format if type is integer or string
   min, integer, false, Min value if type is integer
   max, integer, false, Max value if type is integer

Each attribute is defined by an AttributeSchemaDef.  The ``type`` field is
mandatory and can specify a primitive data type or it can be the name of an
ObjectDef.  The ObjectDef name must be for an API object.  One attribute for
an object must have the ``primary`` field specified.  The ``required`` field is
used to specify if the attribute must be present when creating an object.  If
the ``type`` is enum, the ``values`` field must be present and define an array of
valid strings for the enumeration.

If the ``type`` is integer:

* The ``format`` field can specify if the integer is 32 or 64 bit. Default is int32
* The ``min`` field can specify the valid minimum value
* The ``max`` field can specify the valid maximum value

If the ``type`` is string:

* The ``format`` field can specify the formatting that will be validated for the string.
  The string formatting validations supported are:

    * date-time - Validated according to Date_Time_
    * json - Valid JSON string
    * ipv4 - Validated according to IPV4_
    * ipv6 - Validated according to IPV6_
    * mac - Valid MAC address according to IEEE 802
    * uri - Validated according to URI_
    * email - Validated according to EMAIL_
* The ``length`` field can specify the size of the string. Default is 255

**Example**

The following example shows the AttributeSchemaDef definitions for ipaddress,
subnet_prefix, status, and profile.

::

  ipaddress:
    type: string
    length: 23
    description: "IP Address of port"
    format: ipv4
  subnet_prefix:
    type: integer
    description: "Subnet mask"
    format: int32
    min: 1
    max: 31
  status:
    type: enum
    required: true
    description: "Operational status of Port"
    values:
      - 'ACTIVE'
      - 'DOWN'
  profile:
    type: string
    length: 128
    description: "JSON string for binding profile dictionary"
    format: json

References
~~~~~~~~~~

`Date_Time <https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-7.3.1>`_
`IPV4 <https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-7.3.4>`_
`IPV6 <https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-7.3.5>`_
`URI <https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-7.3.6>`_
`EMAIL <https://tools.ietf.org/html/draft-wright-json-schema-validation-00#section-7.3.2>`_

.. _Example_Specs:

Complete Example Specification
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

<To be defined once base specification is agreed>
