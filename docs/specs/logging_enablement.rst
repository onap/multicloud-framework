..
 This work is licensed under a Creative Commons Attribution 4.0
 International License.

=================
Multi-Vim logging
=================

The purpose of logging is to generate machine-readable, indexable output logs
and support to trace
requests through sub-component, it need to ship logs to logging enhancement
project a centralized
logging analysis system capturing diagnostic information.



Problem Description
===================

So far the logging of multi-vim is not able to support customer configuration,
handler context specific logging like
MDC[MDC_Document]_, also it dose't propagate transaction-ID in REST headers
which is critical to tracing request.
There are 4 python containers in oom project need to configure filebeat
container for shipping logs.

.. [MDC_Document] https://wiki.onap.org/display/DW/ONAP+Application+Logging+Guidelines+v1.1#ONAPApplicationLoggingGuidelinesv1.1-MDCs

In addition the current logging is very difficult to understand behavior
and performance.


Proposed Change
===============

The proposed change will include three parts.

Filebeat container
------------------

Logging architecture[Log_Architecture]_ use Filebeat collects logs from
multi-vim containers and ships them to the
centralized logging stack. To enable this feature it need to add Filebeat
container in multi-vim pod that was
deployed by OOM, as well Yaml file will be used to configure Filebeat.

.. [Log_Architecture] https://wiki.onap.org/display/DW/Logging+Architecture

Tracing ID
----------

ONAP logging uses a global unique "RequestID"[RequestID_Document]_ in logging
to track the processing of each request
across all the components, multi-vim will receive this id from http header
by vary "X-TransactionID", then record it
in logs.
Meanwhile single component should generate a InvocationID that records the
relationship between RequestID
and InvocationID for proper tracing. So Mulit-vim will set unique InvocationID
for each single request,also output it in logs.

.. [RequestID_Document] https://wiki.onap.org/pages/viewpage.action?pageId=20087036#ONAPApplicationLoggingGuidelinesv1.2(Beijing)-MDC-RequestID


python AOP logging library
--------------------------

Currently logging enhancement project just has java AOP logging library, For
multi-vim which based on python need
a python version. The basic feature of AOP logging library could provide
customer configuration include retention
policy、output location、text output format、message level and so on, support
MDC context specific logging, able to
change configuration at runtime, and make logging quite fast.























