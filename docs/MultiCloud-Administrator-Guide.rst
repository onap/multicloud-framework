===================================
ONAP MultiCloud Administrator Guide
===================================

The guide for MultiCloud Administrator.


Configuration
=============

Multicloud doesn’t have any configuration file for now.

Administration
==============

From MSB
--------

After Multicloud is up and running, administrator can check Multicloud
services from MSB. Go to MSB UI page, administrator should see several icons
with name that starts with multicloud.

.. image:: ./images/msb-icons.png
    :alt: Multicloud icons in MSB
    :width: 975
    :height: 293
    :align: center

The icon named Multicloud is the main framework of Multicloud services. And
other icons are the plugin for corresponding backend cloud. For example,
multilcloud-vio is the plugin for VMware Integrated OpenStack.

Administrator can manage Multicloud from MSB UI page. By clicking the icon
named multicloud, there will be available api URL in the bottom of MSB UI
page. After filling required fields, and clicking `Try it out!`, administrator
can perform GET/POST/PUT/DELETE over Multicloud.

From CLI
--------

Besides the MSB UI page, Administrator could manage Multicloud from command
line interface(CLI). Multicloud’s CLI is the same as OpenStack’s CLI, and
therefore, administrator can use OpenStack Client to manage Multicloud.
To make OpenStack Client work with Multicloud, administrator needs to set the
environment variables of operation system. An example of environment variables
is list as below:

::

    OS_AUTH_URL=http://<msb-ip>:80/api/multicloud/v0/<vim_info>/identity/v3
    OS_PROJECT_ID=<project id in backend OpenStack>
    OS_PROJECT_NAME=<project name in backend OpenStack>
    OS_USER_DOMAIN_NAME=<domain name in backend OpenStack>
    OS_USERNAME=<administrator username in backend OpenStack>
    OS_PASSWORD=<password of administrator in backend OpenStack>
    OS_REGION_NAME=<region name in backend OpenStack>
    OS_INTERFACE=internal
    OS_IDENTITY_API_VERSION=3

<msb-ip> in OS_AUTH_URL is the IP address of MSB. <vim-info> is composed of
cloud_type and cloud_region_id. These two attributes are information of cloud
from A&AI. Other environment variables listed above are some information from
Multicloud’s backend OpenStack.
After exporting above variables into operation system, administrator can use
OpenStack Client to mange Multicloud. For example:

::

    nova list

will list the virtual machine in Multicloud’s backend OpenStack.

