================================
ONAP MultiCloud Deployment Guide
================================

prepare docker environment
~~~~~~~~~~~~~~~~~~~~~~~~~~

Please refer to onap deployment in kubernetes website(https://wiki.onap.org/display/DW/ONAP+on+Kubernetes)
to setup kubernets cluster.


After installing kubectl and helm, execute "kubectl cluster-info" command to verify your kubernets cluster.

Clone oom project:  git clone http://gerrit.onap.org/r/oom
Source the setenv.bash script in /oom/kubernetes/oneclick/, it will set your helm list of components to start/delete
Run the one time config pod - which mounts the volume /dockerdata/ contained in the pod config-init.

.. code-block:: console

  $ cd  oom/kubernetes/config
  $ cp onap-parameters-sample.yaml onap-parameters.yaml
  $ ./createConfig.sh -n onap

wait for "onap" namespace created, pod "config-init" created

.. code-block:: console

  $ kubectl get namespaces
  +------------+------------+------+
  | NAME       |     STATUS |  AGE |
  +------------+------------+------+
  |default           Active    10d |
  |kube-public       Active    10d |
  |kube-system       Active    10d |
  |onap              Active    9d  |
  +------------+------------+------+

startup containers
~~~~~~~~~~~~~~~~~~

first run kube2msb container.

.. code-block:: console

 $ cd oom/kubernetes/oneclick
 $ ./createAll.bash -n onap -a kube2msb

run msb container

.. code-block:: console

  $ ./createAll.bash -n onap -a msb

run aai container

.. code-block:: console

  $ ./createAll.bash -n onap -a aai


Finally run multicloud container

.. code-block:: console

  $ ./createAll.bash -n onap -a multicloud

get all pods

.. code-block:: console

  $ kubectl get pods --all-namespaces
  +---------------+---------------------------------------+--------+-----------+----------+-----+
  |NAMESPACE      |  NAME                                 | READY  |   STATUS  |RESTARTS  | AGE |
  +---------------+---------------------------------------+--------+-----------+----------+-----+
  |kube-system       heapster-4285517626-2k4l1               1/1       Running   1          9d  |
  |kube-system       kube-dns-2514474280-mxh18               3/3       Running   3          9d  |
  |kube-system       kubernetes-dashboard-716739405-jl6mk    1/1       Running   1          9d  |
  |kube-system       monitoring-grafana-3552275057-hrpn5     1/1       Running   1          9d  |
  |kube-system       monitoring-influxdb-4110454889-t8tpv    1/1       Running   1          9d  |
  |kube-system       tiller-deploy-737598192-8q523           1/1       Running   1          9d  |
  |onap-aai          aai-resources-837807428-2t158           1/1       Running   0          2d  |
  |onap-aai          aai-service-3869033750-1nvg5            1/1       Running   0          2d  |
  |onap-aai          aai-traversal-50329389-gnsnk            1/1       Running   0          2d  |
  |onap-aai          data-router-2254557428-zwxx1            1/1       Running   0          2d  |
  |onap-aai          elasticsearch-622738319-sx6q1           1/1       Running   0          2d  |
  |onap-aai          gremlin-671060974-npsg5                 1/1       Running   0          2d  |
  |onap-aai          hbase-3690059193-2pjc5                  1/1       Running   0          2d  |
  |onap-aai          model-loader-service-849987455-w6vwn    1/1       Running   0          2d  |
  |onap-aai          search-data-service-4105978183-p1nnj    1/1       Running   0          2d  |
  |onap-aai          sparky-be-2696729089-mcjbw              1/1       Running   0          2d  |
  |onap-kube2msb     kube2msb-registrator-1600827891-1s3s4   1/1       Running   3          7d  |
  |onap-msb          msb-consul-3388279333-hbr16             1/1       Running   0          2d  |
  |onap-msb          msb-discovery-1109629174-t14q8          1/1       Running   0          2d  |
  |onap-msb          msb-eag-3969419634-2fdnr                1/1       Running   0          2d  |
  |onap-msb          msb-iag-1114772402-sjlww                1/1       Running   0          2d  |
  |onap-multicloud   framework-1225620501-9567n              1/1       Running   0          21h |
  |onap-multicloud   multicloud-vio-269945856-rl6w6          1/1       Running   0          21h |
  +---------------+---------------------------------------+--------+-----------+----------+-----+

get cluster-ip and port

.. code-block:: console

  $ kubectl get svc --all-namespaces
  +---------------+----------------------+---------------+-------------+-------------------------------------------------------------------+--------+
  | NAMESPACE     |      NAME            |  CLUSTER-IP   | EXTERNAL-IP |               PORT(S)                                             |  AGE   |
  +---------------+----------------------+---------------+-------------+-------------------------------------------------------------------+--------+
  |default           kubernetes             10.43.0.1       <none>        443/TCP                                                             10d   |
  |kube-system       heapster               10.43.96.134    <none>        80/TCP                                                              10d   |
  |kube-system       kube-dns               10.43.0.10      <none>        53/UDP,53/TCP                                                       10d   |
  |kube-system       kubernetes-dashboard   10.43.9.43      <none>        9090/TCP                                                            10d   |
  |kube-system       monitoring-grafana     10.43.210.16    <none>        80/TCP                                                              10d   |
  |kube-system       monitoring-influxdb    10.43.32.60     <none>        8086/TCP                                                            10d   |
  |kube-system       tiller-deploy          10.43.84.208    <none>        44134/TCP                                                           10d   |
  |onap-aai          aai-resources          None            <none>        8447/TCP,5005/TCP                                                   2d    |
  |onap-aai          aai-service            10.43.88.92     <nodes>       8443:30233/TCP,8080:30232/TCP                                       2d    |
  |onap-aai          aai-traversal          None            <none>        8446/TCP,5005/TCP                                                   2d    |
  |onap-aai          elasticsearch          None            <none>        9200/TCP                                                            2d    |
  |onap-aai          gremlin                None            <none>        8182/TCP                                                            2d    |
  |onap-aai          hbase                  None            <none>        2181/TCP,8080/TCP,8085/TCP,9090/TCP,16000/TCP,16010/TCP,16201/TCP   2d    |
  |onap-aai          model-loader-service   10.43.172.213   <nodes>       8443:30229/TCP,8080:30210/TCP                                       2d    |
  |onap-aai          search-data-service    None            <none>        9509/TCP                                                            2d    |
  |onap-aai          sparky-be              None            <none>        9517/TCP                                                            2d    |
  |onap-msb          msb-consul             10.43.41.203    <nodes>       8500:30500/TCP                                                      2d    |
  |onap-msb          msb-discovery          10.43.6.205     <nodes>       10081:30081/TCP                                                     2d    |
  |onap-msb          msb-eag                10.43.81.104    <nodes>       80:30082/TCP                                                        2d    |
  |onap-msb          msb-iag                10.43.188.78    <nodes>       80:30080/TCP                                                        2d    |
  |onap-multicloud   framework              10.43.97.54     <nodes>       9001:30291/TCP                                                      21h   |
  |onap-multicloud   multicloud-vio         10.43.230.197   <nodes>       9004:30294/TCP                                                      21h   |
  +---------------+----------------------+---------------+-------------+-------------------------------------------------------------------+--------+


Now msb,aai and multicloud container are online, navigate to http://msb_docker_host_ip:30081/iui/microservices/index.html,
you can see  multicloud endpoint have been registered.

No	Service Name	Version	NameSpace	Url	Protocol	Visualrange	Control
1	multicloud	v0		/api/multicloud/v0	REST	InSystem
2	multicloud-vio	v0		/api/multicloud-vio/v0	REST	InSystem

Then register vio information into AAI service with region name "vmware" and region id "vio"

.. code-block:: console

  $ curl -X PUT -H "Authorization: Basic QUFJOkFBSQ==" -H "Content-Type: application/json" -H "X-TransactionId:get_aai_subcr" \
      https://aai_resource_docker_host_ip:30233/aai/v01/cloud-infrastructure/cloud-regions/cloud-region/vmware/vio \
      -d "{
           "cloud-type": "vmware",
           "cloud-region-version": "4.0",
           "esr-system-info-list": {
           "esr-system-info": [
             {
              "esr-system-info-id": "123-456",
              "system-name": "vim-vio",
              "system-type": "vim",
              "type": "vim",
              "user-name": "admin",
              "password": "vmware",
              "service-url": "<keystone auth url>",
              "cloud-domain": "default",
              "default-tenant": "admin",
              "ssl-insecure": false
             }
           ]
          }
        }"
