.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware, Inc.


=================
Image Service
=================

Because Multicloud provides a cloud mediation layer supporting multiple clouds.
It's necessary to
introduces some function enhancements in it. Image Service could let user
upload/download images
in a convinient way just by using Multicloud.


Problem Description
===================

The original functions which Multicloud possesses are to use urls to upload
images, while in this
spec we intend to upload images as raw file which means it has to store a copy
in Multicloud then
upload the images to the backend openstack. So this spec is to extend
multicloud to support
download/upload images as raw file rather than a through a url


Use Cases
===================

One typical use case is to allow users to upload/download images by Multicloud


Proposed change
===================

The proposed change mainly means introducing glance python APIs to enable
multicloud support openstack image service. This feature needs two changes:
Upload API to import an image to backend OpenStack and the image that just
imported can be queried from MultiCloud. Download API to download an image
from backend Openstack and the image can be downloaded from MultiCloud.

The eventual work flow looks like as follows:::

             user request to upload image
                        |
                        V
              +------------------+
              |                  |
              |  image file(iso, |
              |   vmdk... )      |
              |                  |
              +---------+--------+
                        |
                        |
                        |
            +-----------|----------+
            | multicloud|          |
            |           V          |
            | +------------------+ |
            | | image service API| |
            | +---------+--------+ |
            +-----------|----------+
                        | glance
                        |
                        V
            +----------------------+
            | openstack            |
            +----------------------+

The APIs look like this:

upload:

Input of /{vimid}/{tenantid}/images/file  will be

::

  required: image file
  {
    "imageType": string,  // image type: ami, ari, aki, vhd, vhdx, vmdk, raw, qcow2, vdi, iso
    "containerFormat": string,  // image container format: ami, ari, aki, bare, ovf, ova, docker
    "visibility": string,  // public, private, shared, or community
    "properties": arrary // list of properties
  }

Output of upload_image will be

::

  "responses": {
    "201": {
        "description": "upload successfully",
    },
    "404": {
        "description": "the vim id or tenant UUID is wrong"
    },
    "500": {
        "description": "the vim image is not accessable"
    }

download:

Input of /{vimid}/{tenantid}/images/file/{imageid}  will be

::

  {
    "imagepath": string,  // the path of the downloaded image
    "properties": arrary // list of properties
  }

Output of download_image will be

::

  "responses": {
    "200": {
        "description": "download successfully",
    },
    "404": {
        "description": "the vim id or tenant UUID is wrong"
    },
    "500": {
        "description": "the vim image is not accessable"
    }
