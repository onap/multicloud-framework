.. This work is licensed under a Creative Commons Attribution 4.0
.. International License.  http://creativecommons.org/licenses/by/4.0
.. Copyright (c) 2017-2018 Wind River Systems, Inc.

Tutorial: VESagent configuration and Testing
````````````````````````````````````````````

VESagent is a FCAPS relaying service offered by MultiCloud Plugin for Wind
River Titanium Cloud. It allows user to monitor specified VM status and report
VES collector with onset or abate fault event "Fault_MultiCloud_VMFailure"

VESagent provisoning APIs
-------------------------

### assume OOM deployment as below endpoints:

*   OOM k8s Node IP, e.g. 10.12.5.184
*   OOM k8s Node port for multicloud-titaniumcloud POD: 30294
*   On-boarded cloud region with {cloud-owner}/{cloud-region-id} : CloudOwner/pod01
*   VES collector endpoint: 10.12.6.79:8081



.. code-block:: console

    #!/bin/bash
    export MC_EP_IP=10.12.5.184
    export MC_EP_PORT=30294

    export MC_EPv0=http://$OPENO_IP:$MC_EP_PORT/api/multicloud-titaniumcloud/v0/CloudOwner_pod01
    export MC_EPv1=http://$OPENO_IP:$MC_EP_PORT/api/multicloud-titaniumcloud/v1/CloudOwner/pod01



1. Setup VESagent backlogs
^^^^^^^^^^^^^^^^^^^^^^^^^^

** Option 1: monitor all VMs of a tenant**

.. code-block:: console

    curl -v -s -H "Content-Type: application/json" -d '{"vesagent_config": \
         {"backlogs":[ {"domain":"fault","type":"vm","tenant":"VIM"}],\
         "poll_interval_default":10,"ves_subscription":\
         {"username":"admin","password":"admin","endpoint":"http://10.12.6.79:8081/eventListener/v5"}}}'\
          -X POST  $MC_EPv0/vesagent

** Option 2: monitor specified VMs**

.. code-block:: console

    ### zdfw1lb01dns01, zdfw1lb01dns02
    curl -v -s -H "Content-Type: application/json" -d '{"vesagent_config":\
         {"backlogs":[ {"source":"zdfw1lb01dns01", "domain":"fault","type":"vm","tenant":"VIM"},\
          {"source":"zdfw1lb01dns02", "domain":"fault","type":"vm","tenant":"VIM"}],
         "poll_interval_default":10,"ves_subscription":\
         {"username":"admin","password":"admin","endpoint":"http://10.12.6.79:8081/eventListener/v5"}}}' \
         -X POST  $MC_EPv0/vesagent

2. Dump the VESagent backlogs
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    curl -v -s -H "Content-Type: application/json" -X GET  $MC_EPv0/vesagent

3. Delete the VESagent backlogs
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    curl -v -s -H "Content-Type: application/json" -X DELETE  $MC_EPv0/vesagent


VESagent exercises
------------------

Step 1: Monitor the DMaaP events
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Subscribe to and keep polling DMaaP Topic: "unauthenticated.SEC_FAULT_OUTPUT" with curl command

.. code-block:: console

    curl -X GET \
          'http://$DMAAP_IP:3904/events/unauthenticated.SEC_FAULT_OUTPUT/EVENT-LISTENER-POSTMAN/304?timeout=6000&limit=10&filter=' \
          -H 'Cache-Control: no-cache' \
          -H 'Content-Type: application/json' \
          -H 'Postman-Token: 4e2e3589-d742-48c7-8d48-d1b3577df259' \
          -H 'X-FromAppId: 121' \
          -H 'X-TransactionId: 9999'


Step 2: Setup VESagent backlog
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: console

    ### zdfw1lb01dns01, zdfw1lb01dns02
    curl -v -s -H "Content-Type: application/json" -d '{"vesagent_config":\
         {"backlogs":[ {"source":"zdfw1lb01dns01", "domain":"fault","type":"vm","tenant":"VIM"}],\
         "poll_interval_default":10,"ves_subscription":\
         {"username":"admin","password":"admin","endpoint":"http://10.12.6.79:8081/eventListener/v5"}}}' \
         -X POST  $MC_EPv0/vesagent

Step 3: Simulate the Faults
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Manually stop the monitored VMs,e.g. VM with name 'zdfw1lb01dns01',

Step 4: Observe DMaaP event: "Fault_MultiCloud_VMFailure"
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Poll the subscribed DMaaP topic "unauthenticated.SEC_FAULT_OUTPUT" with curl command,
you should be able to observe the following VES fault event generated from DMaaP:

.. code-block:: console

    [

        "{\"event\":{\"commonEventHeader\":{\"startEpochMicrosec\":1537233558255872,\"sourceId\":\"8e606aa7-39c8-4df7-b2f4-1f6785b9f682\",\"eventId\":\"a236f561-f0fa-48a3-96cd-3a61ccfdf421\",\"reportingEntityId\":\"CloudOwner_pod01\",\"internalHeaderFields\":{\"collectorTimeStamp\":\"Tue, 09 18 2018 01:19:19 GMT\"},\"eventType\":\"\",\"priority\":\"High\",\"version\":3,\"reportingEntityName\":\"CloudOwner_pod01\",\"sequence\":0,\"domain\":\"fault\",\"lastEpochMicrosec\":1537233558255872,\"eventName\":\"Fault_MultiCloud_VMFailure\",\"sourceName\":\"zdfw1lb01dns01\"},\"faultFields\":{\"eventSeverity\":\"CRITICAL\",\"alarmCondition\":\"Guest_Os_Failure\",\"faultFieldsVersion\":2,\"specificProblem\":\"Fault_MultiCloud_VMFailure\",\"alarmInterfaceA\":\"aaaa\",\"alarmAdditionalInformation\":[{\"name\":\"objectType\",\"value\":\"VIM\"},{\"name\":\"eventTime\",\"value\":\"2018-09-18 01:19:18.255937\"}],\"eventSourceType\":\"virtualMachine\",\"vfStatus\":\"Active\"}}}",

    ]


Step 5: Simulate the Recovery
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Manually restart the stopped VM 'zdfw1lb01dns01'


Step 6: Observe DMaaP event: "Fault_MultiCloud_VMFailureCleared"
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    [
        "{\"event\":{\"commonEventHeader\":{\"startEpochMicrosec\":1537233558255872,\"sourceId\":\"8e606aa7-39c8-4df7-b2f4-1f6785b9f682\",\"eventId\":\"a236f561-f0fa-48a3-96cd-3a61ccfdf421\",\"reportingEntityId\":\"CloudOwner_pod01\",\"internalHeaderFields\":{\"collectorTimeStamp\":\"Tue, 09 18 2018 01:19:31 GMT\"},\"eventType\":\"\",\"priority\":\"Normal\",\"version\":3,\"reportingEntityName\":\"CloudOwner_pod01\",\"sequence\":1,\"domain\":\"fault\",\"lastEpochMicrosec\":1537233570150714,\"eventName\":\"Fault_MultiCloud_VMFailureCleared\",\"sourceName\":\"zdfw1lb01dns01\"},\"faultFields\":{\"eventSeverity\":\"NORMAL\",\"alarmCondition\":\"Vm_Restart\",\"faultFieldsVersion\":2,\"specificProblem\":\"Fault_MultiCloud_VMFailure\",\"alarmInterfaceA\":\"aaaa\",\"alarmAdditionalInformation\":[{\"name\":\"objectType\",\"value\":\"VIM\"},{\"name\":\"eventTime\",\"value\":\"2018-09-18 01:19:30.150736\"}],\"eventSourceType\":\"virtualMachine\",\"vfStatus\":\"Active\"}}}"

    ]
