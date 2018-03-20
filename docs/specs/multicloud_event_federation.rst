..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=================
Event/Alert/Metrics Federation
=================

As a cloud mediation layer, Multicloud could be invoked by many projects, through Olso notification listener, Multicloud will
provide VM status/events check and also can customize the type of event which user would like to subscribe. There are six
kinds of VM status can be chosen: DELETE, PAUSE, POWER_OFF, REBUILD，SHUT_DOWN, SOFT_DELETE, etc.. Once any change of VM status
is detected of the given type, Multicloud will catch the event and throw it to DMaaP. APPC and VFC can no longer poll the
 Nova by cdp pal but just listen to DMaaP to get the notification of VM status.



Problem Description
===================

This spec is to extend multicloud to support publishing and subscribing events/notification through DMaaP


Use Cases
===================

One typical use case is to allow users to pub/sub messages just by DMaaP instead of CDP PAL or other stuff, Can be
integrated with APPC and VFC instead of CDP poll, may drastically reduce the time of close loop


Proposed change
===================
The proposed change will include two parts: * listener: to listen the events of the status change of VM * publisher: to
throw the event to DMaaP. This feature must be dicussed with APPC team which hasn't been done yet.The message we try to send
is something like this:
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
