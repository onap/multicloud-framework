=====================================
ONAP MultiCloud Heat Deployment Guide
=====================================

Prerequisites
~~~~~~~~~~~~~

1. A VIO platform install with nova, keystone, horizon, image,
neutron and heat service, make sure floating ip is work.

2. A local host as OpenStack client cloud access OpenStack platform,
install python, python-pip, virtualenv, python-openstackclient,
python-heatclient.




Export os enviroment
~~~~~~~~~~~~~~~~~~~~

we'll need to create a file call admin.rc with following content

keystone version 2.0 Example:

export OS_AUTH_URL=https://identity.api.opentack.com/v2.0/
export OS_USERNAME=UserName
export OS_TENANT_ID=TenantID
export OS_REGION_NAME=RegionID
export OS_PASSWORD=Password
export OS_IDENTITY_API_VERSION=2

keystone version 3.0 Example:

export OS_AUTH_URL=https://identiy.api.openstack.com/v3/
export OS_PROJECT_ID=ProjectID
export OS_PROJECT_NAME=ProjectName
export OS_USER_DOMAIN_NAME=DomainName
export OS_USERNAME=UserName
export OS_PASSWORD=Password
if [ -z "$OS_USER_DOMAIN_NAME" ]; then unset OS_USER_DOMAIN_NAME; fi

# unset v2.0 items in case set
unset OS_TENANT_ID
unset OS_TENANT_NAME


export OS_IDENTITY_API_VERSION=3




Get VIO  pem
~~~~~~~~~~~~~

Get a copy of vio.pem in load balancer vms(/etc/ssl/vio.pem) in local
host, then add the following line to your  admin.rc file:
export OS_CACERT=/your/path/vio.pem




Deploy the ONAP
~~~~~~~~~~~~~~~~

get onap heat files from git repo:
git clone http://<your-account>@gerrit.onap.org/r/a/demo

we will use onap_opentack_float.yaml and onap_openstack_float.env  heat templates
at ./demo/heat/ONAP/ dirctory.

Set env options in onap_openstack_float.env according to  VIO platform env,
Finally, heat enviroment contains correct parameters.

Next source the  admin.rc file to create shell environment variables we nedd.

source  admin.rc

Then create heat stack

openstack stack create -t onap_openstack_float.yaml -e onap_openstack_float.env  ONAP

This process will take several minutes to spin up























