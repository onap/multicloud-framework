..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

================================
MultiCloud API v1 Specification
================================

The is the specification for MultiCloud API version v1.

Note: "MultiCloud API Specification V1" refers to the specification for MultiCloud API version v0

API Catalog
===========

1. **Scope**
^^^^^^^^^^^^

The scope of the present document is to describe the MutliCloud NorthBound API
specification.

2. **Terms, Definitions and Abbreviations**
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

For the purposes of the present document, the following abbreviations
apply:

===================== =========================================================
Abbreviation           Description
===================== =========================================================
NFVO                  Network Functions Virtualization Orchestrator
VNFM                  Virtual Network Function Management
VIM                   Virtualized Infrastructure Manager
MultiVIM/MultiCloud   MultVIM driver services for OPEN-O to drive VIM instances
===================== =========================================================

3. **Image Management**
^^^^^^^^^^^^^^^^^^^^^^^

3.1. **Create Image**
---------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/images
Operation              POST
Direction              NSLCM->MULTIVIM
Description            Create Image and Upload the image file to the VIM
===================== =========================================================

3.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
name             M         1            String     Image Name
imagePath        M         1            String     Image Local Path from catalog
imageType        M         1            String     Image Type
                                                     ami, ari, aki, vhd, vhdx, vmdk, raw, qcow2, vdi, iso
visibility       O         1            string     Visibility for this image.
                                                   public, private, shared, or community
containerFormat  M         1            string     ami,ari,aki,bare,ovf,ova, docker
properties       O         0..N         List       Examples:--property vmware_disktype=streamOptimized --property vmware_adaptertype="lsiLogic"
================ ========= ============ ======== ================================


::

    {

    "imageName": "cirros",

    "imagePath": "/home/cirros.qcow2",

    "imageType": "qcow2"

    "containerFormat":"bare"

    }

3.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  id                  M         1       String                      Image UUID in the VIM
  name                M         1       String                      Image Name
  returnCode          M         1       Int                         0: Already exist 1: Newly created
  imageType           M         1       String                      Image Type
                                                                      ami, ari, aki, vhd, vhdx, vmdk, raw, qcow2, vdi, iso
containerFormat       M         1       string                      ami,?ari,?aki,?bare,?ovf,?ova, ?docker
  visibility          O         1       string                      Visibility for this image.
                                                                      public, private, shared, or community
  properties          O                 0..N                List of key-value pairs
  vimid               M         1       String                      vim id
  vimName             O         1       string                      vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  tenantId            M         1       String                      Tenant UUID
================ ========= ============ ======== ================================

*202*: accepted

500: failed

::

    {
        "id": "3c9eebdbbfd345658269340b9ea6fb73",
        "name": "cirros",
        "returnCode": 1
    }

3.2. **Delete Image**
---------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/images/{imageId}
Operation              Delete
Direction              NSLCM->MULTIVIM
Description            Delete Image
===================== =========================================================

3.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

3.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

3.3. **List Images**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/images
Operation              GET
Direction              NSLCM->MULTIVIM
Description            Query Image list
===================== =========================================================

3.3.1. **Request**
>>>>>>>>>>>>>>>>>>

============== ========= ============ ======== ================================
Parameter      Qualifier Cardinality  Content    Description
============== ========= ============ ======== ================================
  limit             O         1       integer         Requests a page size of items. Returns a number of items up to a limit value. Use the limit parameter to make an initial limited request and use the ID of the last-seen item from the response as the marker parameter value in a subsequent limited request.
  marker            O         1       string          The ID of the last-seen item. Use the limit parameter to make an initial limited request and use the ID of the last-seen item from the response as the marker parameter value in a subsequent limited request.
  name              O         1       String          Filters the response by a name, as a string. A valid value is the name of an image
============== ========= ============ ======== ================================


3.3.2. **Response**
>>>>>>>>>>>>>>>>>>>


================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
images            M         0..N         List      Image List

id                M         1            String    Image ID

size              M         1            int       Image Size

name              M         1            String    Image Name

status            M         1            String    Image Status

imageType         M         1            String    Image Type
                                                     ami, ari, aki, vhd, vhdx, vmdk, raw, qcow2, vdi, iso
containerFormat   M         1            string    ami,?ari,?aki,?bare,?ovf,?ova, ?docker
visibility        O         1            string    Visibility for this image.
                                                     public, private, shared, or community
vimId             M         1            String    vim id
vimName           O         1            string    vim name
cloud-owner       M         1            String    cloud owner
cloud-region-id   M         1            string    cloud region id
tenantId          M         1            String    Tenant UUID
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {
        "vimid": "",
        "vimname": "",
        "imageList": [{
            "status": "active",
            "id": "5e2757c1-f846-4727-915c-9a872553ed75",
            "size": 862016,
            "name": "vim-plus-cgsl40g-z.qcow2"
        }]
    }


3.4. **Get Image**
------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/images/{imageid}
Operation              GET
Direction              NSLCM->MULTIVIM
Description            Query Image Information
===================== =========================================================



3.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

3.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  id                  M         1       String          Image ID
  size                M         1       int             Image Size
  name                M         1       String          Image Name
  status              M         1       String          Image Status
  imageType           M         1       String          Image Type
                                                          ami, ari, aki, vhd, vhdx, vmdk, raw, qcow2, vdi, iso
containerFormat       M         1       string          ami,?ari,?aki,?bare,?ovf,?ova, ?docker
  visibility          O         1       string          Visibility for this image.
                                                          public, private, shared, or community
  vimId               M         1       String          vim id
  vimName             M         1       string          vim name
  cloud-owner         M         1       String          cloud owner
cloud-region-id       M         1       string          cloud region id
  tenantId            M         1       String          Tenant UUID
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {
        "vimid": "",
        "vimname": "",
        "status": "active",
        "id": "5e2757c1-f846-4727-915c-9a872553ed75",
        "size": 862016,
        "name": "vim-plus-cgsl40g-z.qcow2"
    }

4. **Network Management**
^^^^^^^^^^^^^^^^^^^^^^^^^

4.1. **Create Network**
-----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/networks
Operation              POST
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Create network on the VIM
===================== =========================================================

4.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       String          Logical network name
  shared              M         1       boolean         Whether to share(1:sharing;0:private)
vlanTransparent       O         1       boolean         Whether to support VLAN pass through(1:true;0:false)
  networkType         O         1       String          Network type
                                                          flat, vlan, vxlan, gre, portgroup
  segmentationId      O         1       Int             id of paragraph
physicalNetwork       O         1       string          The physical network where this network should be implemented. The Networking API v2.0 does not provide a way to list available physical networks. For example, the Open vSwitch plug-in configuration file defines a symbolic name that maps to specific bridges on each compute host.
  routerExternal      O         1       boolean        Indicates whether this network can provide floating IPs via a router.
================ ========= ============ ======== ================================


::

    {
        "tenant": "tenant1",
        "networkName": "ommnet",
        "shared": 1,
        "vlanTransparent": 1,
        "networkType": "vlan",
        "segmentationId": 202,
        "physicalNetwork": "ctrl",
        "routerExternal": 0
    }

4.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
status            M          1          string     Network status
id                M          1          string     Network id
name              M          1          string     Network name
tenantId          M          1          String     Tenant UUID
segmentationId    O          1          int        Segmentation id
networkType       O          1          string     Network type
physicalNetwork   O          1          string     The physical network where this network should be implemented. The Networking API v2.0 does not provide a way to list available physical networks. For example, the Open vSwitch plug-in configuration file defines a symbolic name that maps to specific bridges on each compute host.
vlanTransparent   O          1          boolean    Whether to support VLAN pass through(1:true;0:false)
shared            O          1          boolean    Whether to share(1:sharing;0:private)
routerExternal    O          1          boolean    Indicates whether this network can provide floating IPs via a router.
returnCode        M          1          int        0: Already exist 1: Newly created
vimId             M          1          String     vim id
vimName           O          1          string     vim name
cloud-owner       M          1          String     cloud owner
cloud-region-id   M          1          string     cloud region id
================ ========= ============ ======== ================================

202: accepted

500: failed

::

    {
        "returnCode": 0,
        "vimId": "11111",
        "vimName": "11111",
        "status": "ACTIVE",
        "id": "3c9eebdbbfd345658269340b9ea6fb73",
        "name": "net1",
        "tenant": "tenant1",
        "networkName": "ommnet",
        "shared": 1,
        "vlanTransparent": 1,
        "networkType": "vlan",
        "segmentationId": 202,
        "physicalNetwork": "ctrl",
        "routerExternal": 0
    }

4.2. **Delete Network**
-----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/networks/{networkId}
Operation              Delete
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Delete a network on the VIM
===================== =========================================================

4.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

4.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

4.3. **List Network**
---------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/networks
Operation              GET
Direction              VNFLCM,NSLCM->MULTIVIM
Description            List networks on the VIM
===================== =========================================================

4.3.1. **Query**
>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
name             O         1            String    Filters the response by a name, as a string. A valid value is the name of a network
================ ========= ============ ======== ================================

4.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
vimId             M           1         String        vim id
vimName           O           1         string        vim name
cloud-owner       M           1         String        cloud owner
cloud-region-id   M           1         string        cloud region id
networks                      0..N      List          Network list
status            M           1         string        Network status
id                M           1         string        Network id
name              M           1         string        Network name
tenantId          M           1         String        Tenant UUID
segmentationId    O           1         int           Segmentation id
networkType       O           1         string        Network type
physicalNetwork   O           1         string        The physical network where this network should be implemented. The Networking API v2.0 does not provide a way to list available physical networks. For example, the Open vSwitch plug-in configuration file defines a symbolic name that maps to specific bridges on each compute host.
vlanTransparent   O           1         boolean       Whether to support VLAN pass through(1:true;0:false)
shared            O           1         boolean       Whether to share(1:sharing;0:private)
routerExternal    O           1         boolean       Indicates whether this network can provide floating IPs via a router
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {

        "vimId": "11111",

        "vimName": "111",

        "networks":

            [{

                "status": "ACTIVE",

                "id": "3c9eebdbbfd345658269340b9ea6fb73",

                "name": "net1",

                "tenant": "tenant1",

                "networkName": "ommnet",

                "shared": 1,

                "vlanTransparent": 1,

                "networkType": "vlan",

                "segmentationId": 202,

                "physicalNetwork ": "ctrl",

                "routerExternal ": 0

            }]

    }

4.4. **Get Network**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/networks/{networkId}
Operation              get
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Get a network on the VIM
===================== =========================================================

4.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

4.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  status              M         1       string          Network status
  id                  M         1       string          Network id
  name                M         1       string          Network name
  tenantId            M         1       String          Tenant UUID
  segmentationId      O         1       int             Segmentation id
  networkType         O         1       string          Network type
physicalNetwork       O         1       string          The physical network where this network should be implemented. The Networking API v2.0 does not provide a way to list available physical networks. For example, the Open vSwitch plug-in configuration file defines a symbolic name that maps to specific bridges on each compute host.
vlanTransparent       O         1       boolean         Whether to support VLAN pass through(1:true;0:false)
  shared              O         1       boolean         Whether to share(1:sharing;0:private)
  routerExternal      O         1       boolean         Indicates whether this network can provide floating IPs via a router.
  returnCode          M         1       int             0: Already exist 1: Newly created
  vimId               M         1       String          vim id
  vimName             O         1       string          vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {

        "vimId":"11111",

        "vimName":"11111",

        "status": "ACTIVE",

        "id": "3c9eebdbbfd345658269340b9ea6fb73",

        "name": "net1",

        "tenant": "tenant1",

        "networkName": "ommnet",

        "shared": 1,

        "vlanTransparent": 1,

        "networkType":"vlan",

        "segmentationId":202,

        "physicalNetwork ":"ctrl",

        "routerExternal ":0

    }

5. **Subnetwork Management**
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

5.1. **Create Subnets**
-----------------------


===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/subnets
Operation              POST
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Create subnet on the VIM
===================== =========================================================

5.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  networkId           M         1       String                   Network Id
  name                M         1       String                   SubnetName
  cidr                M         1       String                   Subnet cidr
  ipVersion           M         1       Int                      Ip type
                                                                    4,6
  enableDhcp          O         1       boolean                  Whether to allow
                                                                         1: yes;0: no
  gatewayIp           O         1       String                   Gateway ip
dnsNameservers        O         1..n    List        List of servers
  hostRoutes          O         1..n    List        List of routes
allocationPools       O         1..n    list        List of allocation
  -->allocation
  -->start            O         1       String                   Start ip
  -->end              O         1       String                   End ip
================ ========= ============ ======== ================================

::

    {

        "tenant": "tenant1",

        "network_id": "d32019d3-bc6e-4319-9c1d-6722fc136a22",

        "subnetName": "subnet1",

        "cidr": "10.43.35.0/24",

        "ipVersion": 4,

        "enableDhcp": 1,

        "gatewayIp": "10.43.35.1",

        "dnsNameservers": [],

        "allocationPools": [{

            "start": "192.168.199.2",

            "end": "192.168.199.254"

        }],

        "hostRoutes": []

    }

5.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  returnCode          M         1       int                      0: Already exist 1: Newly created
  vimId               M         1       String                   vim id
  vimName             O         1       string                   vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  status              M         1       string                   subnetwork status
  id                  M         1       string                   subNetwork id
  tenantId            M         1       String                   Tenant UUID
  networkId           O         1       String                   Network Id
  networkName         O         1       String                   Network Name
  name                M         1       String                   SubnetName
  cidr                M         1       String                   Subnet cidr
  ipVersion           M         1       Int                      Ip type
                                                                  4,6
  enableDhcp          O         1       boolean                  Whether to allow
                                                                  1: yes;0: no
  gatewayIp           O         1       String                   Gateway ip
dnsNameservers        O         1..n    List          List of servers
  hostRoutes          O         1..     List           List of routes
allocationPools       O         1..n    List           list of allocation
  -->allocation
  -->start            O         1       String                   Start ip
  -->end              O         1       String                   End ip
================ ========= ============ ======== ================================

202: accepted

500: failed

::

    {

        "returnCode": 0,

        "vimId": "11111",

        "vimName": "11111",

        "status": " ACTIVE",

        "id": " d62019d3-bc6e-4319-9c1d-6722fc136a23",

        "tenant": "tenant1",

        "network_id": "d32019d3-bc6e-4319-9c1d-6722fc136a22",

        "name": "subnet1",

        "cidr": "10.43.35.0/24",

        "ipVersion": 4,

        "enableDhcp": 1,

        "gatewayIp": "10.43.35.1",

        "dnsNameservers": [],

        "allocationPools": [{

            "start": "192.168.199.2",

            "end": "192.168.199.254"

        }],

        "hostRoutes": []

    }

5.2. **Delete Subnets**
-----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/subnets/{subnetId}
Operation              Delete
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Delete a subnet on the VIM
===================== =========================================================

5.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

5.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

5.3. **List Subnets**
---------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/subnets
Operation              Get
Direction              VNFLCM,NSLCM->MULTIVIM
Description            List subnets on the VIM
===================== =========================================================

5.3.1. **Query**
>>>>>>>>>>>>>>>>

msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/subnets?{……}

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name              O         1         String          Filters fields of the response by a name, as a string. A valid value is the name of a subnet
================ ========= ============ ======== ================================

5.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String                   vim id
  vimName             O         1       string                   vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  subnets             M         0..N    List                     Network list
  status                        1       string                   subnetwork status
  id                            1       string                   subNetwork id
  tenantId            M         1       String                   Tenant UUID
  networkId           O         1       String                   Network Id
  networkName         O         1       String                   Network Name
  name                M         1       String                   SubnetName
  cidr                M         1       String                   Subnet cidr
  ipVersion           M         1       Int                      Ip type
                                                                    4,6
  enableDhcp          O         1       boolean                  Whether to allow
                                                                    1: yes;0: no
  gatewayIp           O         1       String                   Gateway ip
dnsNameservers        O         1..n    List          List of servers
  hostRoutes          O         1..     List           List of routes
allocationPools       O         1..n    List         list of allocation
  -->allocation
  -->start            O         1       String                   Start ip
  -->end              O         1       String                   End ip
================ ========= ============ ======== ================================

**200: ok**

**500: failed**

::

    {

        "vimId": "11111",

        "vimName": "11111",

        "subnets": [

            {

                "status": " ACTIVE",

                "id": " d62019d3-bc6e-4319-9c1d-6722fc136a23",

                "tenant": "tenant1",

                "network_id": "d32019d3-bc6e-4319-9c1d-6722fc136a22",

                "name": "subnet1",

                "cidr": "10.43.35.0/24",

                "ipVersion": 4,

                "enableDhcp": 1,

                "gatewayIp": "10.43.35.1",

                "dnsNameservers": [],

                "allocationPools": [{

                    "start": "192.168.199.2",

                    "end": "192.168.199.254"

                }],

                "hostRoutes": []

            }

        ]

    }

5.4. **Get Subnets**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/subnets/{subnetid}
Operation              GET
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Get subnet on the VIM
===================== =========================================================

5.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

5.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String                   vim id
  vimName             O         1       string                   vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  status                        1       string                   subnetwork status
  id                            1       string                   subNetwork id
  tenantId            M         1       String                   Tenant UUID
  networkId           O         1       String                   Network Id
  networkName         O         1       String                   Network Name
  name                M         1       String                   SubnetName
  cidr                M         1       String                   Subnet cidr
  ipVersion           M         1       Int                      Ip type
                                                                   4,6
  enableDhcp          O         1       boolean                  Whether to allow
                                                                   1: yes;0: no
  gatewayIp           O         1       String                   Gateway ip
dnsNameservers        O         1..n    List          List of servers
  hostRoutes          O         1..     List           List of routes
allocationPools       O         1..n    List           list of allocation
  -->allocation
  -->start            O         1       String                   Start ip
  -->end              O         1       String                   End ip
================ ========= ============ ======== ================================

202: accepted

500: failed

::

    {

        "status": " ACTIVE",

        "id": " d62019d3-bc6e-4319-9c1d-6722fc136a23",

        "tenant": "tenant1",

        "network_id": "d32019d3-bc6e-4319-9c1d-6722fc136a22",

        "name": "subnet1",

        "cidr": "10.43.35.0/24",

        "ipVersion": 4,

        "enableDhcp": 1,

        "gatewayIp": "10.43.35.1",

        "dnsNameservers": [],

        "allocationPools": [{

            "start": "192.168.199.2",

            "end": "192.168.199.254"

        }],

        "hostRoutes": []

    }

6. **Virtual Port**
^^^^^^^^^^^^^^^^^^^

6.1. **Create Virtual Port**
----------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/ports
Operation              POST
Direction              VNFLCM->MULTIVIM
Description            Create a vport on the VIM
===================== =========================================================

6.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  networkId           M         1       string          Network UUID
  subnetId            O         1       string          Subnet UUID
  name                M         1       string          Port name
  macAddress          O         1       string          Mac address
  ip                  O         1       string          Ip address
  vnicType            O         1       string          Virtual network card type,
                                                          the value of three kinds of normal/direct/macvtap
  securityGroups     O          1       string      The IDs of security groups applied to the port
================ ========= ============ ======== ================================

6.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  returnCode          M         1       int             0: Already exist 1: Newly created
  vimId               M         1       String          vim id
  vimName             O         1       string          vim name
  cloud-owner         M         1       String          cloud owner
cloud-region-id       M         1       string          cloud region id
  status              M         1       string          status
  id                  M         1       string          Port Id
  name                M         1       string          Port name
  tenantId            M         1       String          Tenant UUID
  networkName         M         1       string          Network name
  networkId           M         1       string          Network Id
  subnetName          M         1       string          Subnet name
  subnetId            M         1       string          SubnetId
  macAddress          O         1       string          Mac address
  ip                  O         1       string          Ip address
  vnicType            O         1       string          Virtual network card type,
                                                          the value of three kinds of normal/direct/macvtap
  securityGroups     O          1       string      List of security group names.
================ ========= ============ ======== ================================

6.2. **Delete Virtual Port**
----------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/ports/{portid}
Operation              POST
Direction              VNFLCM->MULTIVIM
Description            Delete a vport on the VIM
===================== =========================================================

6.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

6.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

6.3. **List Virtual Port**
--------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/ports
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            List vports on the VIM
===================== =========================================================

6.3.1. **Query**
>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name              M         1         string          Port name to filter out list of virtual ports
================ ========= ============ ======== ================================

6.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String          vim id
  vimName             O         1       string          vim name
  cloud-owner         M         1       String          cloud owner
cloud-region-id       M         1       string          cloud region id
  tenantId            M         1       String          Tenant UUID
  Ports               M         0..N    List            ports
  id                  M         1       string          Port Id
  name                M         1       string          Port name
  status              M         1       string          status
  networkName         O         1       string          Network name
  networkId           M         1       string          Network Id
  subnetName          O         1       string          Subnet name
  subnetId            M         1       string          SubnetId
  macAddress          O         1       string          Mac address
  ip                  O         1       string          Ip address
  vnicType            O         1       string          Virtual network card type,
                                                          the value of three kinds of normal/direct/macvtap
  securityGroups      O         1       string          List of security group names.
================ ========= ============ ======== ================================

**200: ok**

**500: failed**


6.4. **Get Virtual Port**
-------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/ports/{portid}
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            Get a vport on the VIM
===================== =========================================================

6.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

6.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

=============== ========= ============ ======== =================================
Parameter       Qualifier Cardinality  Content    Description
=============== ========= ============ ======== =================================
  vimId              M         1       String          vim id
  vimName            O         1       string          vim name
  cloud-owner        M         1       String          cloud owner
cloud-region-id      M         1       string          cloud region id
  status             M         1       string          status
  id                 M         1       string          Port Id
  name               M         1       string          Port name
  tenantId           M         1       String          Tenant UUID
  networkName        M         1       string          Network name
  networkId          M         1       string          Network Id
  subnetName         M         1       string          Subnet name
  subnetId           M         1       string          SubnetId
  macAddress         O         1       string          Mac address
  ip                 O         1       string          Ip address
  vnicType           O         1       string          Virtual network card type,
                                                         the value of three kinds of normal/direct/macvtap
securityGroups       O         1       string          List of security group names
=============== ========= ============ ======== =================================

**200: ok**

**500: failed**


7. **Server Management**
^^^^^^^^^^^^^^^^^^^^^^^^

7.1. **Create Server**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/servers
Operation              POST
Direction              VNFLCM->MULTIVIM
Description            Create a vserver on the VIM
===================== =========================================================

7.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string       server name
  boot                M         1       String       Start parameters
  nicArray            O         1..n    List         List
  contextArray        O         1..n    list         list of context
  volumeArray         O         1..n    List         List
availabilityZone      O         1       string       Usable field
  flavorId            M         1       String       server Flavor id
  metadata            O         1       List         Metadata key and value pairs. The maximum size of the metadata key and value is 255 bytes each.
  userdata            O         1       string       Configuration information or scripts to use upon launch. Must be Base64 encoded.
                                                        NOTE: The ‘null’ value allowed in Nova legacy v2 API, but due to the strict input validation, it isn’t allowed in Nova v2.1 API.
  securityGroups      O         1       List         One or more security groups. Specify the name of the security group in the name attribute. If you omit this attribute, the API creates the server in the default security group.
  serverGroup         O         1       string       the ServerGroup for anti-affinity and affinity
================ ========= ============ ======== ================================


**boot**

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  type                M         1       int        Startup mode
                                                     1. boot from the volume
                                                     2. boot from image
  volumeId            O         1       string          Volume Id(type=1)
  imageId             O         1       String          ImageId（type=2）
================ ========= ============ ======== ================================

**contextArray**

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  fileName            M         1       String          Injection file name
  fileData            M         1       string          Injection file content (injection file content inside the <mac>$MAC\_1</mac> $MAC\_1 need to be replaced by the MAC address, of which 1 is NIC index. )
================ ========= ============ ======== ================================


**volumeArray**

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  volumeId            M         1       String          Volume Id
================ ========= ============ ======== ================================

**nicArray**

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  portId              M         1       String          Port Id
================ ========= ============ ======== ================================


::

    {

        "tenant": "tenant1",

        "name": "vm1",

        "availabilityZone": "az1",

        "flavorName": "vm_large",

        "boot": {

            "type": 1,

            " volumeName": "volume1"

        },

        "flavorId": "vm_large_134213",

        "contextArray": [{

            "fileName": "test.yaml",

            "fileData": "…."

        }],

        "volumeArray": [{

            "volumeName": "vol1",

        }],

        "nicArray": [{

            "portId": "port_a"

        }],

        "metada": {

            "foo": "foo value"

        },

        "userdata": "abcdedf"

    }

7.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String     vim id
  vimName             O         1       string     vim name
  cloud-owner         M         1       String     cloud owner
cloud-region-id       M         1       string     cloud region id
  returnCode                    1       int        0: Already exist 1: Newly created
  id                  M         1       string     server id
  name                          1       string     server name
  tenantId            M         1       String     Tenant UUID
  boot                M         1       String      Start parameters
  nicArray            O         1..n    List           List
  volumeArray         O         1..n    List            List
availabilityZone      O         1       string           Usable field
  flavorId            M         1       String          server Flavor
  metadata            O         1       List            Metadata key and value pairs. The maximum size of the metadata key and value is 255 bytes each.
  securityGroups      O         1       List            One or more security groups. Specify the name of the security group in the name attribute. If you omit this attribute, the API creates the server in the default security group.
  serverGroup         O          1      string           the ServerGroup for anti-affinity and affinity
  status              M          1      string           Server status,
                                                            0:INACTIVE,1:ACTIVE,2:ERROR
================ ========= ============ ======== ================================


202: accepted

500: failed

::

    {

    "id": "3c9eebdbbfd345658269340b9ea6fb73",

    "name": "vm1",

    "returnCode": 1,

    }

7.2. **Delete Server**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/servers/{serverid}
Operation              DELETE
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Delete a vserver on the VIM
===================== =========================================================

7.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

7.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

7.3. **List Server**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/servers
Operation              GET
Direction              VNFLCM,NSLCM->MULTIVIM
Description            List vservers on the VIM
===================== =========================================================

7.3.1. **Request**
>>>>>>>>>>>>>>>>>>

msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/vms?{……}

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string          server name
================ ========= ============ ======== ================================

7.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String                                  vim id
  vimName             O         1       string                                  vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  servers             M         1       array                                   server list
  id                  M         1       string                                  server id
  name                M         1       string                                  server name
  tenantId            M         1       String                                  Tenant UUID
  boot                M         1       String                                  Start parameters
  nicArray             O        1..n    List                             List
  volumeArray          O        1..n    List                          List
availabilityZone      O         1       string                                  Usable field
  flavorId            M         1       String                                  server Flavor
  metada              O         1       keypair                                 Metadata key and value pairs. The maximum size of the metadata key and value is 255 bytes each.
  securityGroups      O         1       List          One or more security groups. Specify the name of the security group in the name attribute. If you omit this attribute, the API creates the server in the default security group.
  serverGroup          O         1      string                              the ServerGroup for anti-affinity and affinity
================ ========= ============ ======== ================================


200: ok

500: failed

7.4. **Get Server**
-------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/servers/{serverid}
Operation              GET
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Get a vserver on the VIM
===================== =========================================================

7.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

7.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String      vim id
  vimName             O         1       string      vim name
  cloud-owner         M         1       String      cloud owner
cloud-region-id       M         1       string      cloud region id
  id                  M         1       string      server id
  name                M         1       string      server name
  tenantId            M         1       String      Tenant UUID
  boot                M         1       String      Start parameters
  nicArray             O        1..n    List        List
volumeArray            O        1..n    List        List
availabilityZone      O         1       string      Usable field
  flavorId            M         1       String      server Flavor
  metadata            O         1       List        Metadata key and value pairs. The maximum size of the metadata key and value is 255 bytes each.
  serverGroup         O         1       List        One or more security groups. Specify the name of the security group in the name attribute. If you omit this attribute, the API creates the server in the default security group.
  serverGroup         O         1       string      the ServerGroup for anti-affinity and affinity
================ ========= ============ ======== ================================


200: ok

500: failed


7.5. **Heal Server**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/servers/{serverid}/action
Operation              POST
Direction              VNFLCM,NSLCM->MULTIVIM
Description            Act on a vserver on the VIM
===================== =========================================================

7.5.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  os-start            M         1       none       The action to start a stopped server.
  os-stop             M         1       none       The action to stop a running server.
  reboot              M         1       object     The action to reboot a server.
  type                O         1       int        The type of the reboot action.
                                                      The valid values are HARD and SOFT
================ ========= ============ ======== ================================


7.5.2. **Response**
>>>>>>>>>>>>>>>>>>>

Normal response codes: 202

Error response codes: unauthorized(401), forbidden(403), itemNotFound(404), conflict(409)

8. **Flavor Management**
^^^^^^^^^^^^^^^^^^^^^^^^

8.1. **Create Flavor**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/flavors
Operation              POST
Direction              VNFLCM->MULTIVIM
Description            Create a flavor on the VIM
===================== =========================================================

8.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string     Flavor Name
  vcpu                M         1       int        Virtual CPU number
  memory              M         1       int        Memory size
  disk                M         1       int        The size of the root disk
  ephemeral           O         1       int        The size of the ephemeral disk
  swap                O         1       int        The size of the swap disk
  isPublic            O         1       boolean    Whether the flavor is public (available to all projects) or scoped to a set of projects. Default is True if not specified.
  extraSpecs          O         0..N    List       EPA parameter
================ ========= ============ ======== ================================


8.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  id                  M         1       string     Flavor id
  name                M         1       string     Flavor name
  returnCode          M         1       int        0: Already exist 1: Newly created
  tenantId            M         1       String     Tenant UUID
  vcpu                M         1       int        Virtual CPU number
  memory              M         1       int        Memory size
  disk                M         1       int        The size of the root disk
  ephemeral           M         1       int        The size of the ephemeral disk
  swap                M         1       int        The size of the swap disk
  isPublic            M         1       boolean    Whether the flavor is public (available to all projects) or scoped to a set of projects. Default is True if not specified.
  extraSpecs          O        0..N     List       EPA parameter
  vimId               M         1       String     vim id
  vimName             O         1       string     vim name
  cloud-owner         M         1       String     cloud owner
cloud-region-id       M         1       string     cloud region id
================ ========= ============ ======== ================================


8.2. **Delete Flavor**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/flavors /{flavorid}
Operation              DELETE
Direction              VNFLCM->MULTIVIM
Description            Delete a flavor on the VIM
===================== =========================================================

8.2.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

8.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

204: no content

8.3. **List Flavor**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/flavors
Operation              GET
Direction              VNFLCM,NSLCM->MULTIVIM
Description            List flavors on the VIM
===================== =========================================================

8.3.1. **Query**
>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string          Flavor name to filter out list
================ ========= ============ ======== ================================

8.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  flavors             M         0..N    list      Vm list
  id                  M         1       string    Flavor id
  name                M         1       string    Flavor Name
  vcpu                M         1       int       Virtual CPU number
  memory              M         1       int       Memory size
  disk                M         1       int       The size of the root disk
  ephemeral           M         1       int       The size of the ephemeral disk
  swap                M         1       int       The size of the swap disk
  isPublic            M         1       boolean   Whether the flavor is public (available to all projects) or scoped to a set of projects. Default is True if not specified.
  extraSpecs          O         0..N    List      EPA parameter
  vimId               M         1       String    vim id
  vimName             O         1       string    vim name
  cloud-owner         M         1       String    cloud owner
cloud-region-id       M         1       string    cloud region id
  tenantId            M         1       String    Tenant UUID
================ ========= ============ ======== ================================


200: ok

500: failed

8.4. **Get Flavor**
-------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/flavors/{flavorid}
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            Get a flavor on the VIM
===================== =========================================================

8.4.1. **Request**
>>>>>>>>>>>>>>>>>>

N/A

8.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  id                  M         1       string     Flavor id
  name                M         1       string     Flavor Name
  vcpu                M         1       int        Virtual CPU number
  memory              M         1       int        Memory size
  disk                M         1       int        The size of the root disk
  ephemeral           M         1       int        The size of the ephemeral disk
  swap                M         1       int        The size of the swap disk
  isPublic            M         1       boolean    Whether the flavor is public (available to all projects) or scoped to a set of projects. Default is True if not specified.
  extraSpecs          O         0..N    List       EPA parameter
  vimId               M         1       String     vim id
  vimName             O         1       string     vim name
  cloud-owner         M         1       String     cloud owner
cloud-region-id       M         1       string     cloud region id
  tenantId            M         1       String     Tenant UUID
================ ========= ============ ======== ================================

200: ok

500: failed

9. **Volume Management**
^^^^^^^^^^^^^^^^^^^^^^^^

9.1. **Create Volume**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/volumes
Operation              POST
Direction              VNFLCM->MULTIVIM
Description            Create volume on the VIM
===================== =========================================================

9.1.1. **Request**
>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string          Volume name
  volumeSize          M         1       int             Volume size
  imageId             O         1       string          Image UUID
  volumeType          O         1       string          Volume type
availabilityZone      O         1       string          Usable field
================ ========= ============ ======== ================================

::

    {

    "tenant": "tenant1",

    "volumeName": "volume1",

    "volumeSize": 3,

    "imageName": "cirros.qcow2",

    "volumeType": "volumetype1",

    "availabilityZone": "zone1"

    }

9.1.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  returnCode          M         1       int             0: Already exist 1: Newly created
  vimId               M         1       String          vim id
  vimName             O         1       string          vim name
  cloud-owner         M         1       String          cloud owner
cloud-region-id       M         1       string          cloud region id
  tenantId            M         1       String          Tenant UUID
  status              M         1       string          Volume status
  id                  M         1       string          Volume id
  name                M         1       string          Volume name
  volumeType          O         1       string          Volume type
availabilityZone      O         1       string          Availability Zone
================ ========= ============ ======== ================================

202: accepted

500: failed

::

    {

    "id": "bc9eebdbbfd356458269340b9ea6fb73",

    "name": "volume1",

    "returnCode": 1,

    }

9.2. **Delete Volume**
----------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/volumes/{volumeId}
Operation              DELETE
Direction              VNFLCM->MULTIVIM
Description            Delete volume on the VIM
===================== =========================================================

9.2.1. **Request**
>>>>>>>>>>>>>>>>>>

    N/A

9.2.2. **Response**
>>>>>>>>>>>>>>>>>>>

    204: no content

9.3. **List Volumes**
---------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/volumes
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            List volumes on the VIM
===================== =========================================================

9.3.1. **Request**
>>>>>>>>>>>>>>>>>>

    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/volumes?{……}

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  name                M         1       string          Volume name
================ ========= ============ ======== ================================

9.3.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String                 vim id
  vimName             O         1       string                 vim name
  cloud-owner         M         1       String       cloud owner
cloud-region-id       M         1       string        cloud region id
  tenantId            M         1       String                 Tenant UUID
  volumes             M         1       Array
  id                  M         1       string                 Volume id
  name                M         1       string                 Volume name
  createTime          O         1       string                 Create time
  status              M         1       string                 Volume status
  volumeSize          M         1       int                    Volume size
  volumeType          M         1       string                 Volume type
availabilityZone      M         1       string                 Availability Zone
  attachments         M         1..n    list        List of additional information on the cloud disk
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {

        "volumes": [

            {

                "status": "available",

                "name": "test",

                "attachments": [],

                "createTime": "2015-12-02T07:57:23.000000",

                " volumeType ": "ws",

                "id": "91b39ebb-acdc-43f3-9c2e-b0da7ad0fd55",

                "size": 20

            },

            {

                "status": "in-use",

                "name": "wangsong",

                "attachments": [

                    {

                        "device": "/dev/vdc",

                        "serverId": "3030e666-528e-4954-88f5-cc21dab1262b",

                        "volumeId": "4bd3e9eb-cd8b-456a-8589-910836a0ab31",

                        "hostName": null,

                        "id": "4bd3e9eb-cd8b-456a-8589-910836a0ab31"

                    }

                ],

                "createTime": "2015-12-02T06:39:40.000000",

                " volumeType ": null,

                "id": "4bd3e9eb-cd8b-456a-8589-910836a0ab31",

                "size": 40

            }

        ]

    }

9.4. **Get Volumes**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/volumes/{volumeid}
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            Get volume on the VIM
===================== =========================================================

9.4.1. **Request**
>>>>>>>>>>>>>>>>>>

    N/A

9.4.2. **Response**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String                 vim id
  vimName             O         1       string                 vim name
  cloud-owner         M         1       String                      cloud owner
cloud-region-id       M         1       string                      cloud region id
  tenantId            M         1       String                 Tenant UUID
  id                            1       string                 Volume id
  name                          1       string                 Volume name
  createTime                    1       string                 Create time
  status                        1       string                 Volume status
  volumeType                    1       List         Volume type
  volumeSize                    1       int                    Volume size
availabilityZone      M         1       string                 Availability Zone
  attachments         M         1..n    list       List of additional information on the cloud disk
================ ========= ============ ======== ================================

**attachment：**

============== ========= ============ ======== ==================================
Parameter      Qualifier Cardinality  Content    Description
============== ========= ============ ======== ==================================
  device                      1       string          Device name
  serverId                    1       string          VM id
  volumeId                    1       string          Volume id
  hostName                    1       string          Host name
  id                          1       string          Device id
============== ========= ============ ======== ==================================

200: ok

500: failed

::

    {

        "status": "in-use",

        "name": "wangsong",

        "attachments": [

            {

                "device": "/dev/vdc",

                "serverId": "3030e666-528e-4954-88f5-cc21dab1262b",

                "volumeId": "4bd3e9eb-cd8b-456a-8589-910836a0ab31",

                "hostName": null,

                "id": "4bd3e9eb-cd8b-456a-8589-910836a0ab31"

            }

        ],

        "createTime": "2015-12-02T06:39:40.000000",

        "volumeType ": null,

        "id": "4bd3e9eb-cd8b-456a-8589-910836a0ab31",

        "volumeSize ": 40

    }

10. **Tenant Management**
^^^^^^^^^^^^^^^^^^^^^^^^^

10.1. **List tenants**
----------------------


===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/tenants
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            List tenants on the VIM
===================== =========================================================


10.1.1. **Query**
>>>>>>>>>>>>>>>>>

================= ========= ============ ======== ================================
Parameter         Qualifier Cardinality  Content    Description
================= ========= ============ ======== ================================
name={tenantname}      O         1       string          Tenant name to filter output list
================= ========= ============ ======== ================================


10.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ================================
  vimId               M         1       String          vim id
  vimName             O         1       string          vim name
  cloud-owner         M         1       String          cloud owner
cloud-region-id       M         1       string          cloud region id
  tenants             M         1       Array
  id                  M         1       string          tenant UUID
  name                M         1       string          tenant name
================ ========= ============ ======== ================================

200: ok

500: failed

::

    {

        " tenants ": [

            {

                "id": "1",

                "name": "test\_a"

            }

        ]

    }

11. **Limits**
^^^^^^^^^^^^^^

11.1. **List Limits of resouces**
---------------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/limits
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            Get limits on the VIM
===================== =========================================================


11.1.1. **Request**
>>>>>>>>>>>>>>>>>>>

N/A

11.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>

======================== ========= ============ ======== ========================
Parameter                Qualifier Cardinality  Content    Description
======================== ========= ============ ======== ========================
  vimId                       M         1       String          vim id                                                          vimName                     O         1       string          vim name
  cloud-owner                 M         1       String          cloud owner
cloud-region-id               M         1       string          cloud region id                                       tenantId                      M         1       string          Tenant UUID                                                     maxPersonality                O         1       int             The number of allowed injected files for each tenant.
  maxPersonalitySize          O         1       int             The number of allowed bytes of content for each injected file.
  maxServerGroupMembers       O         1       int             The number of allowed members for each server group.
  maxServerGroups             O         1       int             The number of allowed server groups for each tenant.
  maxServerMeta               O         1       int             The number of allowed metadata items for each instance.
  maxTotalCores               O         1       int             The number of allowed instance cores for each tenant.
  maxTotalInstances           O         1       int             The number of allowed instances for each tenant.
  maxTotalKeypairs            O         1       int             The number of allowed key pairs for each user.
  maxTotalRAMSize             O         1       int             The amount of allowed instance RAM, in MB, for each tenant.
maxTotalVolumeGigabytes       O         1       int             The maximum total amount of volumes, in gibibytes (GiB).
  maxTotalVolumes             O         1       int             The maximum number of volumes.
  totalVolumesUsed            O         1       int             The total number of volumes used.
  totalGigabytesUsed          O         1       int             The total number of gibibytes (GiB) used.
  network                     O         1       int             The number of networks allowed for each project.
  subnet                      O         1       int             The number of subnets allowed for each project.
  subnetpool                  O         1       int             The number of subnet pools allowed for each project.
  security\_group\_rule       O         1       int             The number of security group rules allowed for each project.
  security\_group             O         1       int             The number of security groups allowed for each project.
  router                      O         1       int             The number of routers allowed for each project.
  port                        O         1       int             The number of ports allowed for each project.
======================== ========= ============ ======== ========================

200: ok

500: failed

::

    {

    "maxPersonality": 5,

    "maxPersonalitySize": 10240,

    "maxServerMeta": 128,

    "maxTotalCores": 20,

    "maxTotalInstances": 10,

    "maxTotalKeypairs": 100,

    "maxTotalRAMSize": 51200,

    "maxServerGroups": 10,

    "maxServerGroupMembers": 10,

    }

12. **Host Management**
^^^^^^^^^^^^^^^^^^^^^^^

12.1. **List hosts**
--------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/hosts
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            list hosts on the VIM
===================== =========================================================

12.1.1. **Request**
>>>>>>>>>>>>>>>>>>>

N/A

12.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>

=============== ========= ============ ======== ==================================
Parameter       Qualifier Cardinality  Content    Description
=============== ========= ============ ======== ==================================
  vimId              M         1       String            vim id
  vimName            O         1       string            vim name
  cloud-owner        M         1       String            cloud owner
cloud-region-id      M         1       string            cloud region id
  tenantId           M         1       string            Tenant Name
  hosts              M         1       Array     List of host information
  service            M         1       string            The service running on the host
  name               M         1       string            host name
  zone               O         1       string            Available zone for the host
=============== ========= ============ ======== ==================================

200: ok

500: failed

::

    {

        "vimId": "123",

        "vimName": "vimName",

        "tenantId": "tenantId1"

        "hosts": [

            {

                "name": "b6e4adbc193d428ea923899d07fb001e",

                "service": "conductor",

                "zone": "internal",

                "vimId": "123",

                "vimName": "vimName",

                "tenantId": "tenantId1"

            },

            {

                "name": "09c025b0efc64211bd23fc50fa974cdf",

                "service": "compute",

                "zone": "nova"

                "vimId": "123",

                "vimName": "vimName",

                "tenantId": "tenantId1"

            },

            {

                "name": "e73ec0bd35c64de4a1adfa8b8969a1f6",

                "service": "consoleauth",

                "zone": "internal"

                "vimId": "123",

                "vimName": "vimName",

                "tenantId": "tenantId1"

            },

            {

                "host\_name": "396a8a0a234f476eb05fb9fbc5802ba7",

                "service": "network",

                "zone": "internal"

                "vimId": "123",

                "vimName": "vimName",

                "tenantId": "tenantId1"

            },

            {

                "name": "abffda96592c4eacaf4111c28fddee17",

                "service": "scheduler",

                "zone": "internal"

                "vimId": "123",

                "vimName": "vimName",

                "tenantId": "tenantId1"

            }

        ]

    }

12.2. **Get host**
------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/{tenantid}/hosts/{hostname}
Operation              GET
Direction              VNFLCM->MULTIVIM
Description            Get a host on the VIM
===================== =========================================================


12.2.1. **Request**
>>>>>>>>>>>>>>>>>>>

12.2.2. **Response**
>>>>>>>>>>>>>>>>>>>>

=============== ========= ============ ======== ==================================
Parameter       Qualifier Cardinality  Content    Description
=============== ========= ============ ======== ==================================
  vimId              M         1       String                vim id
  vimName            O         1       string                vim name
cloud-owner          M         1       String                cloud owner
cloud-region-id      M         1       string                cloud region id
  tenantId           M         1       string                Tenant Name
  host               M         1       List     Host resource info
  resource           M                 1..N                Object                Resource description
  cpu                M         1       Int                   The cpu info on the host.
  memory_mb          M         1       int                   The memory info on the host (in MB).
  name               M         1       string                host name
  project            M         1       string                Value: total, used_now, used_max or specific project_id
  disk_gb            M         1       int                   The disk info on the host (in GB).
=============== ========= ============ ======== ==================================

200: ok

500: failed

::

    {

        "cpu": 1,

        "disk\_gb": 1028,

        "name": "c1a7de0ac9d94e4baceae031d05caae3",

        "memory\_mb": 8192,

        "vimId": "123",

        "vimName": "vimName",

        "tenantId": "tenantId1",

        "host": [

            {

                "memory\_mb": 4960,

                "name": " c1a7de0ac9d94e4baceae031d05caae3",

                "disk\_gb": 92,

                "project": "(total)",

                "cpu": 4

            },

            {

                "memory\_mb": 1536,

                "name": " c1a7de0ac9d94e4baceae031d05caae3",

                "disk\_gb": 2,

                "project": "(used\_now)",

                "cpu": 2

            },

            {

                "memory\_mb": 1024,

                "name": " c1a7de0ac9d94e4baceae031d05caae3",

                "disk\_gb": 2,

                "project": "(used\_max)",

                "cpu": 2

            },

            {

                "memory\_mb": 1024,

                "name": " c1a7de0ac9d94e4baceae031d05caae3",

                "disk\_gb": 2,

                "project": "568f7ec425db472ba348251bf1e7eebd",

                "cpu": 2

            }

        ],

        "vimName": "openstack\_newton",

        "vimId": "dd5b6da9-5984-401f-b89f-78a9776b1a73",

        "tenantId": "568f7ec425db472ba348251bf1e7eebd"

    }

13. **VIM Management**
^^^^^^^^^^^^^^^^^^^^^^

13.1. **Update VIM Info**
-------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/registry
Operation              POST
Direction              ESR-> MULTICLOUD
Description            Register a VIM instance to ONAP
===================== =========================================================

13.1.1. **Request**
>>>>>>>>>>>>>>>>>>>

============== ========= ============ ======== ==================================
Parameter      Qualifier Cardinality  Content    Description
============== ========= ============ ======== ==================================
defaultTenant       M         1       string          default tenant name
============== ========= ============ ======== ==================================

13.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>

NA

202: accept

400: failed

13.2. **Unregistry VIM**
------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}
Operation              DELETE
Direction              ESR-> MULTICLOUD
Description            Unregister a VIM instance from ONAP
===================== =========================================================


13.2.1. **Request**
>>>>>>>>>>>>>>>>>>>

NA

13.2.2. **Response**
>>>>>>>>>>>>>>>>>>>>

NA

204: No content found

400: failed



14. **infrastructure workload LCM**
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

14.1. **Instantiate infrastructure workload**
----------------------------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload
Operation              POST
Direction              SO-> MULTICLOUD
Description            Instantiate infrastructure workload
===================== =========================================================

14.1.1. **Request**
>>>>>>>>>>>>>>>>>>>

================ ========= ============ ======== ==================================
Parameter        Qualifier Cardinality  Content    Description
================ ========= ============ ======== ==================================
generic-vnf-id       O         1        string          generif VNF ID to search AAI object
vf-module-id         O         1        string          vf module id  to search AAI object
oof_directives       O         1        Object          oof directives to update template_data
sdnc_directives      O         1        Object          sdnc directives to update template_data
template_type        M         1        string          template type with which the MultiCloud plugin inteprates template_data
                                                            "heat",etc.
template_data        M         1        Object          workload template data to instantiate workload onto VIM/Cloud instance
================ ========= ============ ======== ==================================

::

  {
     "generic-vnf-id":"vnf-id-111111",
     "vf-module-id":"vf-module-id-2222222",
     "oof_directives":{},
     "sdnc_directives":{},
     "template_type":"heat",
     "template_data":{{
         "files":{  },
         "disable_rollback":true,
         "parameters":{
            "flavor":"m1.heat"
         },
         "stack_name":"teststack",
         "template":{
            "heat_template_version":"2013-05-23",
            "description":"Simple template to test heat commands",
            "parameters":
            {
               "flavor":{
                  "default":"m1.tiny",
                  "type":"string"
               }
            },
            "resources":{
               "hello_world":{
                  "type":"OS::Nova::Server",
                  "properties":{
                     "key_name":"heat_key",
                     "flavor":{
                        "get_param":"flavor"
                     },
                     "image":"40be8d1a-3eb9-40de-8abd-43237517384f",
                     "user_data":"#!/bin/bash -xv\necho \"hello world\" &gt; /root/hello-world.txt\n"
                  }
               }
            }
         },
         "timeout_mins":60
     }
  }

14.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>

================== ========= ============ ======== ==================================
Parameter          Qualifier Cardinality  Content    Description
================== ========= ============ ======== ==================================
template_type          M         1        string          template type with which the MultiCloud plugin inteprates template_data
                                                            "heat",etc.
workload_id            M         1        string          The ID of infrastructure workload resource
template_response      M         1        Object          response from VIM/Cloud instance which is instantiating workload
================== ========= ============ ======== ==================================


201: Created

202: Accepted

400: Bad Request

401: Unauthorized

409: Conflict

::

    {
        "template_type":"heat",
        "workload_id": "1234567890abcd"
        "template_response":
        {
            "stack": {
            "id": "1234567890abcd",
            "links": [
                {
                     "href": "",
                     "rel": "self"
                }
            ]
        }
    }


14.2. **Query infrastructure workload**
---------------------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload/{workload-id}
Operation              GET
Direction              SO-> MULTICLOUD
Description            Query the status of the infrastructure workload
===================== =========================================================


14.2.1. **Request**
>>>>>>>>>>>>>>>>>>>

NA

14.2.2. **Response**
>>>>>>>>>>>>>>>>>>>>

================== ========= ============ ======== ==================================
Parameter          Qualifier Cardinality  Content    Description
================== ========= ============ ======== ==================================
template_type          M         1        string          template type with which the MultiCloud plugin inteprates template_data
                                                            "heat",etc.
workload_id            M         1        string          The ID of infrastructure workload resource
workload_status        M         1        string          Status of infrastructure workload:
                                                              DELETE_IN_PROGRESS, CREATE_COMPLETE, CREATE_FAILED
                                                              DELETE_IN_PROGRESS, DELETE_COMPLETE, DELETE_FAILED
                                                              UPDATE_IN_PROGRESS, UPDATE_COMPLETE, UPDATE_FAILED
================== ========= ============ ======== ==================================


200: OK

400: Bad Request

401: Unauthorized

404: Not Found

500: Internal Server Error

::

    {
        "template_type":"heat",
        "workload_id": "1234567890abcd",
        "workload_status":"CREATE_IN_PROCESS"
    }


14.3. **Delete infrastructure workload**
----------------------------------------

===================== =========================================================
IF Definition          Description
===================== =========================================================
URI                    msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{cloud-region-id}/infra_workload/{workload-id}
Operation              DELETE
Direction              SO-> MULTICLOUD
Description            DELETE the infrastructure workload
===================== =========================================================


14.3.1. **Request**
>>>>>>>>>>>>>>>>>>>

NA

14.3.2. **Response**
>>>>>>>>>>>>>>>>>>>>

NA


204: No Content, The server has fulfilled the request by deleting the resource.

400: Bad Request

401: Unauthorized

404: Not Found

500: Internal Server Error


15. **Proxied OpenStack APIs**
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

15.1. **Tokens**
-------------------------

+---------------------+----------------------------------------------------------------------------------------------+
| **IF Definition**   | **Description**                                                                              |
+=====================+==============================================================================================+
| URI                 | http://msb.onap.org:80/api/multicloud/v1/{cloud-owner}/{region-id}/identity/v3/auth/tokens   |
+---------------------+----------------------------------------------------------------------------------------------+
| Operation           | POST                                                                                         |
+---------------------+----------------------------------------------------------------------------------------------+
| Direction           | NSLCM-> MULTICLOUD                                                                           |
+---------------------+----------------------------------------------------------------------------------------------+

15.1.1. **Request**
>>>>>>>>>>>>>>>>>>>

+-----------------+-----------------+-------------------+---------------+-----------------------+
| **Parameter**   | **Qualifier**   | **Cardinality**   | **Content**   | **Description**       |
+=================+=================+===================+===============+=======================+
| auth            | O               | 1                 | Object        | Same as OpenStack     |
|                 |                 |                   |               | Identity Tokens API   |
+-----------------+-----------------+-------------------+---------------+-----------------------+

::

    {

    }

15.1.2. **Response**
>>>>>>>>>>>>>>>>>>>>


+-----------------+-----------------+-------------------+---------------------+--------------------------------------------------------------+
| **Parameter**   | **Qualifier**   | **Cardinality**   | **Content**         | **Description**                                              |
+=================+=================+===================+=====================+==============================================================+
| X-Subject-Token | M               | 1                 | String              | The authentication token in Header                           |
+-----------------+-----------------+-------------------+---------------------+--------------------------------------------------------------+
| token           | O               | 1                 | Object              | Token response, the same as OpenStack Identity Tokens API    |
+-----------------+-----------------+-------------------+---------------------+--------------------------------------------------------------+

201: Created

401: Unauthorized

403: Forbidden

500: failed


::

  Header:
    X-Subject-Token: a33f3b209e9b471a97fbeab8324a9a45

  Body:

      {
           "token" : {
              "user" : {
                 "domain" : {
                    "id" : "default",
                    "name" : "Default"
                 },
                 "id" : "9efb043c7629497a8028d7325ca1afb0",
                 "name" : "admin"
              },
              "catalog" : [
                 {
                    "type" : "network",
                    "endpoints" : [
                       {
                          "interface" : "public",
                          "id" : "39583c1508ad4b71b380570a745ee10a",
                          "url" : "http://172.16.77.10:80/api/multicloud-titaniumcloud/v1/CloudOwner/RegionOne/network",
                          "region_id" : "RegionOne",
                          "region" : "RegionOne"
                       }
                    ],
                    "name" : "neutron",
                    "id" : "99aefcc82a9246f98f8c281e61ffc754"
                 },
                 ...
              ]
              "project" : {
                 "name" : "admin",
                 "id" : "fcca3cc49d5e42caae15459e27103efc",
                 "domain" : {
                    "id" : "default",
                    "name" : "Default"
                 }
              },
              "is_domain" : false,
              "expires_at" : "2017-09-11T03:52:29.000000Z"
           }
      }
