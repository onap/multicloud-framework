..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

===============================================
Parallelism improvement of Multi Cloud Services
===============================================

To comply with Scalability, Security, Stability and Performance(S3P), current
API framework needs improvements.

Problem Description
===================

There are several problems of current Multi-Cloud API framework.

#. Multi-Cloud runs Django by using Django's built-in webserver currently.
According to Django Document[Django_Document]_, this mode should not be used
in production. This mode has not gone through security audits or performance
tests, and should only be used in development. From test on local computer,
this mode can only handle ONE API request at one time.

.. [Django_Document] https://docs.djangoproject.com/en/dev/ref/django-admin/#runserver

#. Multi-Cloud is built on Django now. Django is a good web framework, it is
also very huge. Django is a black box if one doesn't have good knowledge of it.
Adding feature based on Django may be time-consuming. For example, the unit test[unit_test]_
of Multi-Cloud can't use regular python test library because of Django. The unit
test has to base on Django's test framework. This is just one example, but when
we want to improve the parallelism of Multi-Cloud services, we need to find out
how Django can implement it, instead of using some common method.

.. [unit_test] https://gerrit.onap.org/r/#/c/8909/

#. Django's code pattern is too much like web code. Current code of Multi-Cloud
put many logic in files named `views.py`, but actually there is no view to expose.
It is confusing.

Possible Solutions
==================

Solution 1
----------

Django is a mature framework. And it has its own way to improve parallelism.
Instead of running Django's build-in webserver, Django APP can be deployed in
some dedicated web server. Django’s primary deployment platform is WSGI[django_deploy]_,
the Python standard for web servers and applications.

.. [django_deploy] https://docs.djangoproject.com/en/2.0/howto/deployment/wsgi/

This solution can resolve the first problem mentioned above, but the remaining 2
problems still exist. However, this solution only need to change how we run
Django, most current code needs no change.

Solution 2
----------

Use a new framework. Pecan[Pecan]_ will be the idea web framework in this case.

.. [Pecan] https://pecan.readthedocs.io/en/latest/

It is lightweight, lean and fast. And it is widely used, for example, most
OpenStack projects use Pecan as its API framework. This solution can resolve
all the problems mentioned above. However, it needs to rewrite existing code.


Proposed Change
===============

This spec proposes to use solution 2. Based on the consideration of Elastic
API exposure[jira_workitem]_, Multi-Cloud will provide a new way to expose
its API. That is to say, existing code needs rewrite. So the disadvantage of
solution 2 doesn't exist.

.. [jira_workitem] https://jira.onap.org/browse/MULTICLOUD-152

Backward compatibility
----------------------

This spec will NOT replace the current API framework in R2, nor will switch to
new API framework in R2. Instead, this spec will provide a configuration
option, named `web_framework`,  to make sure use case and functionalities not
be broken. Default value of the configuration will BE `django`, which will
still run current Django API framework. An alternative value is `pecan`, which
will run the API framework proposed in this spec. So that users don't care about
the change won't be affected.

WSGI Server
-----------

No matter what API framework will be used, a WSGI[WSGI]_ Server needs to be
provided. API framework will be run as an application in WSGI server.

.. [WSGI] http://eventlet.net/doc/modules/wsgi.html

Multi processes framework
-------------------------

This spec proposes to run Multi-Cloud API server in multiple processes mode.
Multi-process can provide a parallel API handler. So, when multiple API
requests come to Multi-Cloud, they can be handled simultaneously. On the other
hand, different processes can effectively isolate different API request. So
that, one API request will not affect another.

Managing multiple processes could be overwhelming difficult and sometimes
dangerous. Some mature library could be used to reduce related work here, for
example oslo.service[oslo_service]_.

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

