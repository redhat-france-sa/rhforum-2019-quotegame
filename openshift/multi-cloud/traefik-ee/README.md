## Deploying Traefik EE as a front Geo Load-Balancer

### What it Traefik EE? Why using it?

TraefikEE is a production-grade, distributed, and highly available edge routing solution built on top of the open source Traefik.

TraefikEE provides 2 interesting features for our use-case:
* Geographical steering of incoming requests: integration with CloudFlare alllows the routing of incoming requests to nearest cluster,
* Cross-site failover: if an incoming request on cluster 1 cannot be delivered to application (because of pod not being there for example), request is redirected to the cluster 2.

### Deploying Traefik EE Operator

Trafik EE is in the process of becoming a certified Operator onto OpenShift. For now, Operator is available for Kubernetes upstream version and can be installed through Operator Lifecycle Manager or diretly from GitHub. We'll use this latter method as this is a work-in-progress.

Like some other commands, start settings your environment before executing the `00-traefik-init.sh` script. For this special case, you'll need a Traefik EE license key. You may want to ask for a trial one [here](https://containo.us/traefikee/).

```
$ export TRAEFIKEE_LICENSE_KEY=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

$ export CLUSTER_1=https://api.cluster-01.mydomain.com:6443
$ export USER_1=<user1>
$ export PASSWORD_1=<password1>

$ export CLUSTER_2=https://api.cluster-02.mydomain.com:6443
$ export USER_2=<user2>
$ export PASSWORD_2=<password2>

$ ./00-traefik-init.sh

Login successful.

You have access to 53 projects, the list has been suppressed. You can list all projects with 'oc projects'

Using project "quotegame".
Already on project "traefikee" on server "https://https://api.cluster-01.mydomain.com:6443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-25-centos7~https://github.com/sclorg/ruby-ex.git

to build a new example application in Ruby.
secret/license created
customresourcedefinition.apiextensions.k8s.io/traefikees.containo.us configured
serviceaccount/traefikee-operator created
clusterrole.rbac.authorization.k8s.io/traefikee-operator configured
clusterrolebinding.rbac.authorization.k8s.io/traefikee-operator configured
securitycontextconstraints.security.openshift.io/traefikee-scc created
scc "anyuid" added to: ["system:serviceaccount:traefikee:default"]
deployment.apps/traefikee-operator created

[...]
```

Wait for aminute to have the operator deployed. You should end up with something like this on both clusters:

```
NAME                                   READY     STATUS    RESTARTS   AGE
traefikee-operator-5d898985d7-6ljwg    2/2       Running   0          2m
```

### Deploying Traefik Load-Balancers with cross-site failover

Now we need to create `Traefikee` custom resources on both clusters in order to deploy the Loab-Balancers.

On cluster 1, execute this command:

```
$ oc create -f 01-traefikee-xsite1.yaml
traefikee.containo.us/traefikee created
```

On cluster 2, execute this command:

```
$ oc create -f 01-traefikee-xsite2.yaml
traefikee.containo.us/traefikee created
```

Waith for a few minutes to have everything deployed. You should end up with something like that on both clusters:

```
NAME                                   READY     STATUS    RESTARTS   AGE
traefikee-control-node-0               1/1       Running   0          9m
traefikee-data-node-548fdfccc9-fvpsd   1/1       Running   2          9m
traefikee-data-node-548fdfccc9-rt757   1/1       Running   0          9m
traefikee-operator-5d898985d7-6ljwg    2/2       Running   0          10m
```

To enable Geo Load-Balancing, we need to create a new `Ingress` on both clusters in order to access a `ping` service. This `ping` service will be used by CloudFlare to detect if a cluster is up or down.

On both clusters, execute this command:

```
$ oc create -f 01-ingress-traefik.yaml
service/traefikee-ping created
ingress.extensions/ping created
```

Now check the services availables on both clusters:

```
$ oc get services
NAME                         TYPE           CLUSTER-IP       EXTERNAL-IP                                                              PORT(S)                      AGE
traefikee-api                ClusterIP      172.30.2.213     <none>                                                                   8080/TCP                     11m
traefikee-control-nodes      ClusterIP      172.30.191.44    <none>                                                                   4242/TCP                     11m
traefikee-lb                 LoadBalancer   172.30.161.41    a491821a8322311eab3080ab7a810a32-840339824.us-east-1.elb.amazonaws.com   80:31297/TCP,443:30623/TCP   72m
traefikee-operator-metrics   ClusterIP      172.30.241.122   <none>                                                                   8686/TCP,8383/TCP            12m
traefikee-ping               ClusterIP      172.30.144.141   <none>                                                                   4545/TCP                     9m
```

You'll see we have created `Services` of type `LoadBalancer`. This host names will be used to configure the deployment of Load-Balancers.

Deployment should now be done using the `traefikeectl` command line tool. You'il find instructions on how to set it up [here](https://docs.containo.us/references/cli/traefikeectl/). The first line of the next `02-traefik-deploy.sh` script may also be uncommented for using a locally downloaded version of the CLI.

Just execute this script that will take care of retrieving AWS LoadBalancer URLs and create TraefikEE deploiyments:

```
$ ./02-traefik-deploy.sh

Connecting to Kubernetes API...ok
Retrieving TraefikEE Control credentials...ok
Removing cluster credentials from platform...ok
  > Credentials saved in "/Users/lbroudou/.config/traefikee/traefikee-site1", please make sure to keep them safe as they can never be retrieved again.
✔ Successfully gained access to the cluster. You can now use other traefikeectl commands.
Forwarding TraefikEE Control API port...ok
Connecting to TraefikEE Control API...ok
Deploying configuration...ok

Connecting to Kubernetes API...ok
Retrieving TraefikEE Control credentials...ok
Removing cluster credentials from platform...ok
  > Credentials saved in "/Users/lbroudou/.config/traefikee/traefikee-site2", please make sure to keep them safe as they can never be retrieved again.
✔ Successfully gained access to the cluster. You can now use other traefikeectl commands.
Forwarding TraefikEE Control API port...ok
Connecting to TraefikEE Control API...ok
Deploying configuration...ok
```

### Reaching our application using Geo Load-Balancer

TODO: Configuring a CloudFlare account with AWS LoadBalancers host names reaching the `ping` ingress on both clusters.

Finally, as TraefikEE deals with Kubernetes `Ingress` we should create such objects on both clusters. Contrary to already existing OpenShift Routes that are using the cluster default addressing scheme, ingresses will be created with the same globally-reachable URL. Here it will be `quotegame.redhat.containo.us` in our example.

On both clusters, run the following command and then check the ingress presence:

```
$ oc create -f 03-ingress-api.yaml -n quotegame
ingress.extensions/quotegame created

$ oc get ingress -n quotegame
$ NAME        HOSTS                          ADDRESS   PORTS     AGE
quotegame   quotegame.redhat.containo.us             80        26m
```

### Viewing a Load-Balancer metrics

In order to access TraefikEE management console, you may start a local port-forwarding session:

```
$ kubectl port-forward -n traefikee traefikee-control-node-0 8081:8080
```

and then accessing the console using `127.0.0.1:8180` in your favorite browser.

### Testing it

#### Cross-site failover

Scale to 0 the `quotegame-api` deployment on cluster 1 and see requests routed to the TraefikEE Ingress on cluster 2.

#### Geo failover

Delete the `ping` ingress on cluster 1 and check that requests are routed on cluster 2 by CloudFlare.