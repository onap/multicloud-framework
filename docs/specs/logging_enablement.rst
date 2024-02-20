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
MDC `MDC_Document`_ , also it dose't propagate transaction-ID in REST headers
which is critical to tracing request.
There are 4 python containers in oom project need to configure filebeat
container for shipping logs.

.. _MDC_Document: https://wiki.onap.org/display/DW/ONAP+Application+Logging+Guidelines+v1.1#ONAPApplicationLoggingGuidelinesv1.1-MDCs

In addition the current logging is very difficult to understand behavior
and performance.


Proposed Change
===============

The proposed change will include three parts.

Filebeat container
------------------

Logging architecture `Log_Architecture`_ use Filebeat collects logs from
multi-vim containers and ships them to the
centralized logging stack. To enable this feature it need to add Filebeat
container in multi-vim pod that was
deployed by OOM, as well Yaml file will be used to configure Filebeat.

.. _Log_Architecture: https://wiki.onap.org/display/DW/Logging+Architecture

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

Supporting Python3 version
--------------------------
Right now, this library only has be used in Python2 version. Python2 will not been
maintained after 2020, besides part of ONAP project have used python3 version.
It's be better to support Python2 and Python3 version


Markers
-------
Markers can be used to characterize log entries. They allow message that has
a specific meaning to be cheaply and easily identified in logger output, without
inherently unreliable schemes like scanning for magic strings in the text of each
log message.
Onap logging requires the emission of markers reporting entry, exit and invocation
as the execution if requests pass between ONAP components. This information is used
to generate a call graph.
Useful and commonplace, See https://stackoverflow.com/questions/4165558/best-practices-for-using-markers-in-slf4j-logback


colored terminal output
-----------------------
As we known, in log4j coloring is supported. It would be better to render logging messages in colors.
Bash colors refer: https://en.wikipedia.org/wiki/ANSI_escape_code#Colors

The standard colors(nosupport windows) list as below:
+---------------------+-------------------------+------------------------+
| **Text color**      | **Hightlight color**    |  **Text Attribute**    |
+=====================+==================================================+
|  Black              |   Black                 |   Normal               |
+---------------------+--------------------------------------------------+
|  Red                |   Red                   |   Bold                 |
+---------------------+--------------------------------------------------+
|  Green              |   Green                 |   Underline            |
+---------------------+--------------------------------------------------+
|  Yellow             |   Yellow                |   Blink                |
+------------------------------------------------------------------------+
|  Blue               |   Blue                  |   Invert               |
+------------------------------------------------------------------------+
|  Purple             |   Purple                |   Hide                 |
+------------------------------------------------------------------------+
|  Cyan               |   Cyan                  |                        |
+------------------------------------------------------------------------+
|  White              |   White                 |                        |
+------------------------------------------------------------------------+


Test
====

#. Unit tests with tox
#. CSIT tests, verify marker label in logging message
