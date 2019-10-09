
## Deploying the demo on OpenShift

### Preparing infrastructure services

For this demo, we need to have a sole OpenShift project/namespace with a Kafka broker installed.

Start creating a new project once logged into your OpenShift cluster, call it `quotegame-rhforum` for example. You'll also need to add privileged access to the default service account as Quarkus native images require that.

```
$ oc new-project quotegame-rhforum --display-name="RH Forum Quote game"
$ oc adm policy add-scc-to-user anyuid -z default -n quotegame-rhforum 
```

Now install the *AMQ Streams* operator (AMQ Streams is the Red Hat Kafka distribution) using the Operator Lifecycle Manager from the Admin console.

As an alternative, you can choose to deploy the upstream community operator that is called Strimzi using the following command line:

```
$ oc create -f https://operatorhub.io/installstrimzi-kafka-operator.yaml -n quotegame-rhforum
```

### Deploying infrastructure services

From now, we'll use the Kubernetes manifest placed into the `/openshift`subfolder of the Git repository.

Deploy a Kafka broker to the project:

```
$ oc create -f openshift/00-kafka.yaml -n quotegame-rhforum
```

Wait for a few minutes and you should get the following:

```
$ oc get pods
NAME                                            READY     STATUS    RESTARTS   AGE
amq-streams-cluster-operator-74896b74bb-64tr7   1/1       Running   1          4h
my-cluster-entity-operator-7777b975fc-nslvj     3/3       Running   0          16m
my-cluster-kafka-0                              2/2       Running   3          4h
my-cluster-kafka-1                              2/2       Running   3          4h
my-cluster-kafka-2                              2/2       Running   4          4h
my-cluster-zookeeper-0                          2/2       Running   2          4h
my-cluster-zookeeper-1                          2/2       Running   2          4h
my-cluster-zookeeper-2                          2/2       Running   2          4h
```

Deploy an Infinispan server to the project

```
$ oc create -f openshift/00-statefulset-infinispan.yaml -n quotegame-rhforum
```

Wait for a few minutes and you should get the following:

```
$ oc get pods
NAME                                            READY     STATUS    RESTARTS   AGE
amq-streams-cluster-operator-74896b74bb-64tr7   1/1       Running   1          4h
infinispan-0                                    1/1       Running   1          2h
my-cluster-entity-operator-7777b975fc-nslvj     3/3       Running   0          16m
my-cluster-kafka-0                              2/2       Running   3          4h
my-cluster-kafka-1                              2/2       Running   3          4h
my-cluster-kafka-2                              2/2       Running   4          4h
my-cluster-zookeeper-0                          2/2       Running   2          4h
my-cluster-zookeeper-1                          2/2       Running   2          4h
my-cluster-zookeeper-2                          2/2       Running   2          4h
```

### Deploying application pods

Start deploying the 2 components that are the API server and the order processors.

```
$ oc create -f openshift/01-deployment-api.yaml -n quotegame-rhforum
$ oc create -f openshift/01-deployment-processors.yaml -n quotegame-rhforum
```

Once this is done, you may start interacting with the application using the route attached to the `quotegame-api`. 

```
$ oc get route/quotegame-api -n quotegame-rhforum
NAME            HOST/PORT                                                       PATH      SERVICES        PORT       TERMINATION   WILDCARD
quotegame-api   quotegame-api-quotegame-rhforum.apps.ocp42.ocp42.openshift.fr             quotegame-api   8080-tcp                 None
```

You can register as a user and start buying and selling actions from your portfolio. But for now the value of each symbol remains flat as we are using the `base` version of the `quotegame-processors` container image.

If you want some changes, you may invoke our *Chaos Monkey* by deploying the `quotegame-chaosmonkey` container image with:

```
$ oc create -f openshift/01-deployment-chaosmonkey.yaml -n quotegame-rhforum
```

Monkey will start introducing random variations of both quotes so that the game start being a little more interesting! The variation parameters are setup into the `application.properties` file of the module and are rather self-explanatory:

```
%kube.chaos.frequency=30s
%kube.chaos.symbols=TYR,CYB
%kube.chaos.variation.min=20
%kube.chaos.variation.max=40
```

To check the Horizontal Pod Autoscaler that has been configured onto the API component, you can start simulating load using:

```
$ oc run web-load --rm --attach --restart='Never' --image=jordi/ab -n 80000 -c 20 http://quotegame-api:8080/api/quote/TYR -n quotegame-rhforum
```


### Updating application pods

```
$ oc replace -f openshift/02-deployment-processors-latest.yaml -n quotegame-rhforum
```