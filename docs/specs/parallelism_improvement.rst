..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

:orphan:

===============================================
Parallelism improvement of Multi Cloud Services
===============================================


Problem Description
===================

Multi-Cloud runs Django by using Django's built-in webserver currently.
According to Django Document[Django_Document]_, this mode should not be used
in production. This mode has not gone through security audits or performance
tests, and should only be used in development. From test on local computer,
this mode can only handle ONE API request at one time. This can not meet the
performance requirement.

.. [Django_Document] https://docs.djangoproject.com/en/dev/ref/django-admin/#runserver

Although security and scalability might be improved as the side effect of
resolving the performance issue, this spec will only focus on how to improve
the parallelism(performance) of current MultiCloud API framework.

Possible Solutions
==================

Solution 1
----------

Django is a mature framework. And it has its own way to improve parallelism.
Instead of running Django's build-in webserver, Django APP can be deployed in
some dedicated web server. Django’s primary deployment platform is
WSGI[django_deploy]_,
the Python standard for web servers and applications.

.. [django_deploy] https://docs.djangoproject.com/en/2.0/howto/deployment/wsgi/


But on the other side, Danjgo is very huge. And Django is a black box if one
doesn't have good knowledge of it. Adding feature based on Django may be
time-consuming. For example, the unit test[unit_test]_ of Multi-Cloud can't use
regular python test library because of Django. The unit test has to base on
Django's test framework. When we want to improve the parallelism of Multi-Cloud
services, we need to find out how Django can implement it, instead of using
some common method.

.. [unit_test] https://gerrit.onap.org/r/#/c/8909/

Besides, Django's code pattern is too much like web code. And, most famous use
cases of Django are web UI. Current code of Multi-Cloud puts many logic in
files named `views.py`, but actually there is no view to expose. It is
confusing.

The benefit of this solution is that most current code needs no change.

Solution 2
----------

Given the fact that Django has shortcomings to move on, this solution propose
to use a alternative framework. Eventlet[Eventlet]_ with Pecan[Pecan]_ will be
the idea web framework in this case, because it is lightweight, lean and widely
used.

.. [Eventlet] http://eventlet.net/doc/modules/wsgi.html

.. [Pecan] https://pecan.readthedocs.io/en/latest/

For example, most OpenStack projects use such framework. This framework is so
thin that it can provide flexibility for future architecture design.

However, it needs to change existing code of API exposing.


Performance Test Comparison
===========================

Test Environment
----------------

Apache Benchmark is used as test tool. It is shipped with Ubuntu, if you
don’t find it, just run “sudo apt install -y apache2-utils”

2 Virtual Machine with Ubuntu1604. Virtual Machines are hosted in a multi-core
hardware server. One VM is for Apache Benchmark. This VM is 1 CPU core, 8G mem.
The other VM is for Multicloud. The VM is 4 CPU core, 6G mem.

Test Command
~~~~~~~~~~~~

`ab  -n <num of total requests> -c <concurrency level> http://<IP:port>/api/multicloud/v0/vim_types`

Test result
-----------

It should be noted that data may vary in different test run, but overall result
is similar as below.

100 requests, concurrency level 1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Command:  `ab  -n 100 -c 1 http://<IP:port>/api/multicloud/v0/vim_types`
Result::

  Django runserver: total takes 0.512 seconds, all requests success
  Django+uwsgi: totally takes 0.671 seconds, all requests success.
  Pecan+eventlet:  totally takes 0.149 seconds, all requests success.

10000 requests, concurrency level 100
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Command:  `ab  -n 10000 -c 100 http://<IP:port>/api/multicloud/v0/vim_types`
Result::

  Django runserver: total takes 85.326 seconds, all requests success
  Django+uwsgi: totally takes 3.808 seconds, all requests success.
  Pecan+eventlet:  totally takes 3.181 seconds, all requests success.

100000 requests, concurrency level 1000
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Command:  `ab  -n 100000 -c 1000 http://<IP:port>/api/multicloud/v0/vim_types`
Result::

  Django runserver: Apache Benchmark quit because it reports timeout after
  running a random portion of all requests.
  Django+uwsgi: totally takes 37.316 seconds, about 32% requests fail. I see
  some error says that tcp socket open too many.
  Pecan+eventlet:  totally takes 35.315 seconds, all requests success.

Proposed Change
===============

Given the test result above, this spec proposes to use solution 2. Based on
the consideration of Elastic API exposure[jira_workitem]_, Multi-Cloud will
provide a new way to expose its API. That is to say, existing code of API
exposing needs rewrite in [jira_workitem]_. So the disadvantage of solution
2 doesn't exist.

.. [jira_workitem] https://jira.onap.org/browse/MULTICLOUD-152

To define a clear scope of this spec, VoLTE is the use case that will be used
to perform test to this spec. All functionality that VoLTE needed should be
implemented in this spec and [jira_workitem]_.

Backward compatibility
----------------------

This spec will NOT change current API. This spec will NOT replace the current
API framework in R2, nor will switch to new API framework in R2. Instead,
this spec will provide a configuration option, named `web_framework`,  to make
sure use case and functionalities not be broken. Default value of the
configuration will BE `django`, which will still run current Django API
framework. An alternative value is `pecan`, which will run the API framework
proposed in this spec. So users don't care about the change won't be
affected.

WSGI Server
-----------

No matter what API framework will be used, a WSGI Server needs to be provided.
This spec will use Eventlet WSGI server. API framework will be run as an
application in WSGI server.

Multi processes framework
-------------------------

This spec proposes to run Multi-Cloud API server in multiple processes mode.
Multi-process can provide parallel API handlers. So, when multiple API
requests come to Multi-Cloud, they can be handled simultaneously. On the other
hand, different processes can effectively isolate different API request. So
that, one API request will not affect another.

Managing multiple processes could be overwhelming difficult and sometimes
dangerous. Some mature library could be used to reduce related work here, for
example oslo.service[oslo_service]_. Since oslo is used by all OpenStack
projects for many releases, and oslo project is actively updated, it can be
seen as a stable library.

.. [oslo_service] https://github.com/openstack/oslo.service

Number of processes
~~~~~~~~~~~~~~~~~~~

To best utilize multi-core CPU, the number of processes will be set to the
number of CPU cores by default.

Shared socket file
~~~~~~~~~~~~~~~~~~

To make multiple processes work together and provide a unified port number,
multiple processes need to share a socket file. To achieve this, a bootstrap
process will be started and will initialize the socket file. Other processes
can be forked from this bootstrap process.

Work Items
==========

#. Add WSGI server.
#. Run Pecan application in WSGI server.
#. Add multiple processes support.
#. Update deploy script to support new API framework.

