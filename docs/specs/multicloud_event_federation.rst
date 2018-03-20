.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 VMware, Inc.

=================
Event/Alert/Metrics Federation
=================

As a cloud mediation layer, Multicloud could be invoked by many projects, through this feature, Multicloud will provide
VM status/events check and also can customize the type of event which user would like to receive. There are some
kinds of VM status can be chosen: DELETE, PAUSE, POWER_OFF, REBUILD，SHUT_DOWN, SOFT_DELETE, etc.. In VMware VIO Plugin,
once any change of VM status is detected of a given type, Multicloud will catch the event and throw it to DMaaP.
Other projects can try this way of getting VM status messages in the future. Also, for other Multicloud plugin providers,
due to some issues, there will be rest apis for them to grab the VM status messages.


Use Cases
===================

In VIO, one typical use case is to allow VIO users to fetch messages from DMaaP, this will provide a easier way for fetching status of
VMs, it may drastically reduce the time of close loop, for other Multicloud plugin providers, Multicloud will provide a set of
rest apis to get them


Proposed change
===================

In VIO plugin:

The proposed change will include two parts: * listener: to listen the events of the status change of VM, for others it
will have rest apis to get the messages * publisher: to throw the event to DMaaP.The message we try to send is something like this:
{
    "state_description": "powering-off",
    "availability_zone": "nova",
    "terminated_at": "",
    "ephemeral_gb": 0,
    "instance_type_id": 5,
    "deleted_at": "",
    "reservation_id": "r-pvx3l6s2",
    "memory_mb": 2048,
    "display_name": "VM1",
    "hostname": "vm1",
    "state": "active",
    "progress": "",
    "launched_at": "2018-03-07T05:59:46.000000",
    "metadata": {},
    "node": "domain-c202.22bfc05c-da55-4ba6-ba93-08d9a067138e",
    "ramdisk_id": "",
    "access_ip_v6": null,
    "disk_gb": 20,
    "access_ip_v4": null,
    "kernel_id": "",
    "host": "compute01",
    "user_id": "aa90efa5c84c4084b39094da952e0bd1",
    "image_ref_url": "http://10.154.9.172:9292/images/207b9b7c-9450-4a95-852b-0d6d41f35d24",
    "cell_name": "",
    "root_gb": 20,
    "tenant_id": "943ecb804cdf4103976b8a02cea12fdb",
    "created_at": "2018-03-07 05:58:01+00:00",
    "instance_id": "4f398943-7d39-4119-8058-74768d6dfa52",
    "instance_type": "m1.small",
    "vcpus": 1,
    "image_meta": {
        "is_copying": "1",
        "container_format": "bare",
        "min_ram": "0",
        "vmware_disktype": "streamOptimized",
        "disk_format": "vmdk",
        "source_type": "url",
        "image_url": "https://cloud-images.ubuntu.com/releases/14.04/release/ubuntu-14.04-server-cloudimg-amd64-disk1.img",
        "vmware_adaptertype": "lsiLogic",
        "min_disk": "20",
        "base_image_ref": "207b9b7c-9450-4a95-852b-0d6d41f35d24"
    },
    "architecture": null,
    "os_type": null,
    "instance_flavor_id": "2"
}

The eventual work flow looks like as follows:

              +------------------+
              |                  |
              |   Multicloud     |
              |     Broker       |
              |                  |
              +---------+--------+
                        |
                        |
                        V
            +-----------------------+            +------------------+
            | Multicloud VIO Plugins|----------->| Dmaap            |
            |                       |   Event    |                  |
            +--------|-----^--------+            +------------------+
            Oslo     |     |
          Listener   |     |
                     V     |
            +----------------------+
            | VIO                  |
            +----------------------+


In Other Plugins:

Since the security rules of VIMs and network connectivity issues, other multicloud plugins won't be suitable for the
oslo notification listener, so we will provide rest apis for them, the specific implementation will be dicided by them

Input of <vim_id>/check_vim_status will be

::
  {
    "states": array,  // the set of VIM status which user wants to get
  }

Output of check_vim_status will be

::
  {
    "state_description": string  // VIM's state
    "launched_at": string // time of state change
  }

The work flow looks like as follows:

            +------------------+
            |                  |
            |   Multicloud     |
            |     Broker       |
            |                  |
            +---------+--------+
                      |
                      |
                      V
            +-----------------------+
            | Multicloud Plugins    |
            |                       |
            +--------|-----^--------+
            polling  |     |
        or other way |     |
                     V     |
            +----------------------+
            | Openstack            |
            +----------------------+
