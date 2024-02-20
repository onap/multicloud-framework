..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

:orphan:

===============================
ONAP MultiCloud CSIT Test Guide
===============================

Clone integration repo
======================

::

    git clone http://gerrit.onap.org/r/integration

Add more contains
=================

The file 'setup.sh' under 'test/csit/plans/multicloud/functionality1' will
setup multicloud containers for CSIT test, add more containers in this file
if needed.

Add more tests
==============

The file 'testplan.txt' under 'test/csit/plans/multicloud/functionality1/'
specific the robot tests to be run.

The content of 'testplan.txt' will looks like following::

    # Test suites are relative paths under [integration.git]/test/csit/tests/.
    # Place the suites in run order.
    multicloud/provision/sanity_test_multivim.robot

When adding tests to file 'multicloud/provision/sanity_test_multivim.robot' ,
a 'verify-csit' job will be trigger for related patch, and related change will
be tested.
