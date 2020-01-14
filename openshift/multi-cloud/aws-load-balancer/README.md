
```
$ oc new-project quotegame
Now using project "quotegame" on server "https://api.cluster-paris-fb92.paris-fb92.example.opentlc.com:6443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-25-centos7~https://github.com/sclorg/ruby-ex.git

to build a new example application in Ruby.
```

```
$ oc create -f 00-kafka.yaml -n quotegame
kafka.kafka.strimzi.io/my-cluster created
```

```
$ oc get kafka
NAME         DESIRED KAFKA REPLICAS   DESIRED ZK REPLICAS
my-cluster   3                        3
```

```
$ oc get statefulsets
NAME                   READY     AGE
my-cluster-kafka       3/3       72s
my-cluster-zookeeper   3/3       2m3s
```

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

```
$ oc get pods | grep mirror
my-mirror-maker-mirror-maker-84b58884d4-766nt   1/1       Running   0          12m
```

```
./01-infinispan-discovery.sh 
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

### Deploy application

```
$ oc create -f 01-deployment-api-xsite1.yaml
configmap/quotegame-api-config created
deployment.extensions/quotegame-api created
service/quotegame-api created
route.route.openshift.io/quotegame-api created
horizontalpodautoscaler.autoscaling/quotegame-api-hpa created
```

```
$ oc create -f 01-deployment-api-xsite2.yaml
configmap/quotegame-api-config created
deployment.extensions/quotegame-api created
service/quotegame-api created
route.route.openshift.io/quotegame-api created
horizontalpodautoscaler.autoscaling/quotegame-api-hpa created
```

```
$ oc create -f 01-deployment-processors.yaml
deployment.extensions/quotegame-processors created
service/quotegame-processors created
route.route.openshift.io/quotegame-processors created
```

```
$ oc create -f 01-deployment-priceupdater-xsite1.yaml
configmap/quotegame-priceupdater-config created
deployment.extensions/quotegame-priceupdater created
service/quotegame-priceupdater created
route.route.openshift.io/quotegame-priceupdater created
```

```
$ oc create -f 01-deployment-priceupdater-xsite2.yaml
configmap/quotegame-priceupdater-config created
deployment.extensions/quotegame-priceupdater created
service/quotegame-priceupdater created
route.route.openshift.io/quotegame-priceupdater created
```


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


```
$ oc create -f 03-deployment-rebalancer.yaml
deployment.extensions/quotegame-rebalancer created
service/quotegame-rebalancer created
route.route.openshift.io/quotegame-rebalancer created
```