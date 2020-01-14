## Deployment instructions on AWS

So for deploying the application in Multicloud mode, you shoud at least have... 2 clusters! ;-)

For realizing step-by-step deployment by applying command, we recommend having 3 different terminals opened:
* A first terminal opened on `cluster 1` and with the `oc` tool logged on this context,
* A second terminal opened on `cluster 2` and with the `oc` tool logged on this context,
* A third terminal that is not logged to any particular cluster and that will we used to execute command that implies modifications on both clusters?

On this third terminal, you'll need to set 6 different environment variables:
* `CLUSTER_1`, `USER_1` and `PASSWORD_1` with the cluster API URL, user and password of the `cluster 1`,
* `CLUSTER_2`, `USER_2` and `PASSWORD_2` with the cluster API URL, user and password of the `cluster 2`.

For each and every major step of the process, we'll tell you:
* The commands that should be runned on **both clusters** which means you'll have to execute them on `cluster 1` AND on `cluster 2` terminal,
* The commands that should be runned on **terminal on cluster 1** (or 2) which means you'll have to execute them ONLY on `cluster 1` (or 2),
* The commands that are **globals** which means that you should run them from the third terminal. These are mainly shell scripts that realized dynamic operations on both clusters by logging to each other in sequence.

Fair enough? Let's go!

### Deploy infrastructure components

#### On both clusters

Let's start creating a new project called `quotegame` on each cluster:
```
$ oc new-project quotegame
Now using project "quotegame" on server "https://api.cluster-paris-fb92.paris-fb92.example.opentlc.com:6443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-25-centos7~https://github.com/sclorg/ruby-ex.git

to build a new example application in Ruby.
```

As a pre-requisites, you have to install the *AMQ Streams* operator (AMQ Streams is the Red Hat Kafka distribution) using the Operator Lifecycle Manager from the Admin console. Install it globally for each namespace in the cluster or locally to the `quotegame` namespace.

As an alternative, you can choose to deploy the upstream community operator that is called Strimzi using the following command line:
```
$ oc create -f https://operatorhub.io/installstrimzi-kafka-operator.yaml -n quotegame-rhforum
```

Now just create a new Kafka broker on both clusters:
```
$ oc create -f 00-kafka.yaml -n quotegame
kafka.kafka.strimzi.io/my-cluster created
```

You can check your broker is created:
```
$ oc get kafka
NAME         DESIRED KAFKA REPLICAS   DESIRED ZK REPLICAS
my-cluster   3                        3
```

And that Operator has intanciated 2 statefulsets after some seconds:
```
$ oc get statefulsets
NAME                   READY     AGE
my-cluster-kafka       3/3       72s
my-cluster-zookeeper   3/3       2m3s
```

And 6 runnings pods after some minutes:
```
$ oc get pods
NAME                                         READY     STATUS    RESTARTS   AGE
my-cluster-entity-operator-d6dbbcddf-2scnl   3/3       Running   0          39s
my-cluster-kafka-0                           2/2       Running   0          2m13s
my-cluster-kafka-1                           2/2       Running   2          2m13s
my-cluster-kafka-2                           2/2       Running   0          2m13s
my-cluster-zookeeper-0                       2/2       Running   0          3m4s
my-cluster-zookeeper-1                       2/2       Running   0          3m4s
my-cluster-zookeeper-2                       2/2       Running   0          3m4s
```

#### From terminal on cluster 1

On first cluster, deploy a new statefulset for the Infinispan in-memory DataGrid:

```
$ oc create -f 00-statefulset-infinispan-xsite1-lb.yaml
statefulset.apps/infinispan created
service/infinispan created
service/infinispan-site1 created
service/infinispan-ping created
service/infinispan-headless created
```

Check pod (or pods) are running after a minute:
```
$ oc get pods | grep infinispan
infinispan-0                                 1/1       Running   0          62s
```

#### From terminal on cluster 2

```
$ oc create -f 00-statefulset-infinispan-xsite1-lb.yaml
statefulset.apps/infinispan created
service/infinispan created
service/infinispan-site1 created
service/infinispan-ping created
service/infinispan-headless created
```

```
$ oc get pods | grep infinispan
infinispan-0                                 1/1       Running   0          62s
```

#### From the global terminal

Our first global manipulation is for creating new Kafka `MirrorMakers` that will be in charge of replicating some messages between clusters. For remote communication setup, we have to retrieve certificates created in each cluster and register them in the other one. Then, we'll create `MirrorMaker` objects referencing the secrets holding this certs:

```
$ ./01-kafka-mirror-maker.sh
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
# ca.crt
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
# ca.crt
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
secret/my-cluster-site2-cluster-ca-cert created
kafkamirrormaker.kafka.strimzi.io/my-mirror-maker created
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
secret/my-cluster-site1-cluster-ca-cert created
kafkamirrormaker.kafka.strimzi.io/my-mirror-maker created
```

After some minutes, you'd should have `my-mirror-maker` pod running on both clusters:
```
$ oc get pods | grep mirror
my-mirror-maker-mirror-maker-84b58884d4-766nt   1/1       Running   0          12m
```

Then, we'll have to setup the discovery mechanisms for the Infinispan DataGrid. The next shell script takes care of retrieving the AWS LoadBalancer hostnames created for accessing each cluster and reconfigure the 2 sites for discovering each other. Statefulsets are then updated and a rolling update of pods occurs:
```
$ ./01-infinispan-discovery.sh 
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
statefulset.apps/infinispan updated
statefulset.apps/infinispan patched
statefulset.apps/infinispan updated
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
statefulset.apps/infinispan updated
statefulset.apps/infinispan patched
statefulset.apps/infinispan updated
```

To check that Infinispan clusters are well configured after restart, you can run the following command, looking for site discovery on each pod. Here's the below the output oo command on `cluster 1` that is called `SITE1` on the Infinispan side:
```
$ oc logs infinispan-0 | grep "Received new x-site view"
16:12:45,598 INFO  [org.infinispan.XSITE] (jgroups-5,infinispan-0-32990) ISPN000439: Received new x-site view: [SITE1]
16:13:33,706 INFO  [org.infinispan.XSITE] (jgroups-7,infinispan-0-32990) ISPN000439: Received new x-site view: [SITE1, SITE2]
```

We can see that `SITE2` has joined the cross-sites view of the grid.

### Deploy application components

Now we can start deploying the different application components.

#### On both clusters

On both clusters, start by deploying the `quotegame-processors` component. In Mutlicloud variant of the application, the component is responsible for processing `Order` messages from Kafka and computing a User's portfolio content (number of quotes and total amount) before updating the DataGrid.
```
$ oc create -f 01-deployment-processors.yaml
deployment.extensions/quotegame-processors created
service/quotegame-processors created
route.route.openshift.io/quotegame-processors created
```

#### From terminal on cluster 1

On `cluster 1` we are going to deploy the `quotegame-api` compoent. This component is in charge of holding the UI and the API used by end-users. This deployment is specific to `cluster 1` because we include a specific header color and environment info to easily view we're on `cluster 1` during tests:
```
$ oc create -f 01-deployment-api-xsite1.yaml
configmap/quotegame-api-config created
deployment.extensions/quotegame-api created
service/quotegame-api created
route.route.openshift.io/quotegame-api created
horizontalpodautoscaler.autoscaling/quotegame-api-hpa created
```

Then we deploy the `quotegame-priceupdater` component on `cluster 1`. This component is responsible for computing the prices of quotes. The deployment is specific to a site because it should publosh snapshots of its calculations on a cluster dependant topic that is mirrored on the other cluster. It also receives snapshots from other cluster on a specific cluster.
```
$ oc create -f 01-deployment-priceupdater-xsite1.yaml
configmap/quotegame-priceupdater-config created
deployment.extensions/quotegame-priceupdater created
service/quotegame-priceupdater created
route.route.openshift.io/quotegame-priceupdater created
```

#### From terminal on cluster 2

Process with same components than previous section on `cluster 2`, for the `quotegame-api`:
```
$ oc create -f 01-deployment-api-xsite2.yaml
configmap/quotegame-api-config created
deployment.extensions/quotegame-api created
service/quotegame-api created
route.route.openshift.io/quotegame-api created
horizontalpodautoscaler.autoscaling/quotegame-api-hpa created
```

And for the `quotegame-priceupdater`:
```
$ oc create -f 01-deployment-priceupdater-xsite2.yaml
configmap/quotegame-priceupdater-config created
deployment.extensions/quotegame-priceupdater created
service/quotegame-priceupdater created
route.route.openshift.io/quotegame-priceupdater created
```

#### From the global terminal

Finally we have to prepare the deployment of the component called `quotegame-rebalancer`. As stated in its name, this component takes care of routing `Order` messages to the master `quotegame-priceupdater` instance and rebalance to a new instance in case of unavailability. Rebalancers on each clusters should have same configuration: first they should route to `cluster 1`, then to `cluster 2`. However the network route for accessing clusters are differents depending on source cluster. 

This next command retrieves the OpenShift `Routes` used on both clusters and produce a `ConfigMap` for rebalancer on each cluster:
```
$ ./02-configmap-rebalancer.sh
Login successful.

You have access to 54 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Login successful.

You have access to 54 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Error from server (NotFound): configmaps "quotegame-rebalancer-config" not found
configmap/quotegame-rebalancer-config created
Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Error from server (NotFound): configmaps "quotegame-rebalancer-config" not found
configmap/quotegame-rebalancer-config created
```

### Finalize application deployment

#### On both clusters

Final step: on both cluster, we have to deploy the `quotegame-rebalancer` component. Each component will use the locally defined `ConfigMap` from previous step:
```
$ oc create -f 03-deployment-rebalancer.yaml
deployment.extensions/quotegame-rebalancer created
service/quotegame-rebalancer created
route.route.openshift.io/quotegame-rebalancer created
```