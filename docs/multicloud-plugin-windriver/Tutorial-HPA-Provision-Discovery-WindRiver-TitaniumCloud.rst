..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

Tutorial: Enable ONAP HPA Orchestation to Wind River Titanium Cloud
```````````````````````````````````````````````````````````````````


To fulfil the functional requirement of HPA enablement, MultiCloud plugin for
Wind River Titanium Cloud expects the administrator to provision the Titanium
Cloud instance conforming to certain conventions.

This tutorial demonstrates how to enable ONAP HPA orchestration to Wind River Titanium Cloud.

Architecture & Policies & Mappings
----------------------------------

Please refer to the link for more architecture details:

..
  https://wiki.onap.org/pages/viewpage.action?pageId=20874679

Please refer to the link for more Policies&Mappings details:

..
  https://wiki.onap.org/display/DW/HPA+Policies+and+Mappings

Provision Flavors
-----------------

configure openstack with proper flavors (with name prefixed by "onap." to carry HPA information to ONAP), example flavor:

.. code-block:: console

    nova flavor-create onap.hpa.medium 110 4096 0 6
    #cpu pining
    nova flavor-key onap.hpa.medium set hw:cpu_policy=dedicated
    nova flavor-key onap.hpa.medium set hw:cpu_thread_policy=prefer
    #cpu topology
    nova flavor-key onap.hpa.medium set hw:cpu_sockets=2
    nova flavor-key onap.hpa.medium set hw:cpu_cores=4
    nova flavor-key onap.hpa.medium set hw:cpu_threads=8
    #hugepage
    nova flavor-key onap.hpa.medium set hw:mem_page_size=large
    #numa
    nova flavor-key onap.hpa.medium set hw:numa_nodes=2    
    nova flavor-key onap.hpa.medium set hw:numa_cpus.0=0,1 hw:numa_cpus.1=2,3,4,5 hw:numa_mem.0=2048 hw:numa_mem.1=2048

Access configuration of Titanium Cloud Instance
-----------------------------------------------

collect following information for on-boarding this Cloud instance to ONAP:

.. code-block:: console

    your openstack project name
    your openstack user
    your openstack password
    your openstack keystone endpoint
    your openstack Region ID: e.g. RegionOne


On-board the Titanium Cloud instance
------------------------------------

Now you can onboard this Titanium Cloud instance, make sure the multicloud registration process is triggered.

See `Tutorial: Onboard instance of Wind River Titanium Cloud`
