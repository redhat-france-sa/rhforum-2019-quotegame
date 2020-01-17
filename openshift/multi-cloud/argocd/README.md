### Install ArgoCD on OpenShift cluster 1

Create a new namespace for ArgoCD components
```
$ oc create namespace argocd
namespace/argocd created
```

Apply the ArgoCD Install Manifest
```
$ oc -n argocd apply -f https://raw.githubusercontent.com/argoproj/argo-cd/v1.3.6/manifests/install.yaml
customresourcedefinition.apiextensions.k8s.io/applications.argoproj.io created
customresourcedefinition.apiextensions.k8s.io/appprojects.argoproj.io created
serviceaccount/argocd-application-controller created
serviceaccount/argocd-dex-server created
serviceaccount/argocd-server created
role.rbac.authorization.k8s.io/argocd-application-controller created
role.rbac.authorization.k8s.io/argocd-dex-server created
role.rbac.authorization.k8s.io/argocd-server created
clusterrole.rbac.authorization.k8s.io/argocd-application-controller created
clusterrole.rbac.authorization.k8s.io/argocd-server created
rolebinding.rbac.authorization.k8s.io/argocd-application-controller created
rolebinding.rbac.authorization.k8s.io/argocd-dex-server created
rolebinding.rbac.authorization.k8s.io/argocd-server created
clusterrolebinding.rbac.authorization.k8s.io/argocd-application-controller created
clusterrolebinding.rbac.authorization.k8s.io/argocd-server created
configmap/argocd-cm created
configmap/argocd-rbac-cm created
configmap/argocd-ssh-known-hosts-cm created
configmap/argocd-tls-certs-cm created
secret/argocd-secret created
service/argocd-dex-server created
service/argocd-metrics created
service/argocd-redis created
service/argocd-repo-server created
service/argocd-server-metrics created
service/argocd-server created
deployment.apps/argocd-application-controller created
deployment.apps/argocd-dex-server created
deployment.apps/argocd-redis created
deployment.apps/argocd-repo-server created
deployment.apps/argocd-server created
```

Get the ArgoCD Server password
```
$ ARGOCD_SERVER_PASSWORD=$(oc -n argocd get pod -l "app.kubernetes.io/name=argocd-server" -o jsonpath='{.items[*].metadata.name}')
```

Patch ArgoCD Server so no TLS is configured on the server (`--insecure`)
```
$ PATCH='{"spec":{"template":{"spec":{"$setElementOrder/containers":[{"name":"argocd-server"}],"containers":[{"command":["argocd-server","--insecure","--staticassets","/shared/app"],"name":"argocd-server"}]}}}}'
$ oc -n argocd patch deployment argocd-server -p $PATCH
deployment.extensions/argocd-server patched
```

Expose the ArgoCD Server using an Edge OpenShift Route so TLS is used for incoming connections
```
$ oc -n argocd create route edge argocd-server --service=argocd-server --port=http --insecure-policy=Redirect
route.route.openshift.io/argocd-server created
```

Get ArgoCD Server Route Hostname
```
$ ARGOCD_ROUTE=$(oc -n argocd get route argocd-server -o jsonpath='{.spec.host}')
```

Login with the current admin password
```
$ argocd --insecure --grpc-web login ${ARGOCD_ROUTE}:443 --username admin --password ${ARGOCD_SERVER_PASSWORD}
'admin' logged in successfully
````

Update admin's password
```
$ argocd --insecure --grpc-web --server ${ARGOCD_ROUTE}:443 account update-password --current-password ${ARGOCD_SERVER_PASSWORD} --new-password admin
Password updated
```

### Declaring clusters and applications

```
export CLUSTER_1=<>
export USER_1=<user_1>
export PASSWORD_1=<password_1>
export CLUSTER_2=<>
export USER_2=<user_2>
export PASSWORD_2=<password_2>
```

Re-login to ArgoCD if necessary
```
argocd --insecure --grpc-web login ${ARGOCD_ROUTE}:443 --username admin --password admin
```

Defining contexts for cluster1 and cluster2
```
$ oc config set-cluster cluster1 --server=${CLUSTER_1}
$ oc config set-credentials ${USER_1}/cluster1 --username=${USER_1} --password=${PASSWORD_1}
$ oc config set-context cluster1 --cluster=cluster1 --user=${USER_1}/cluster1 --namespace=quotegame
Context "cluster1" created.
```

```
$ oc config set-cluster cluster2 --server=${CLUSTER_2}
$ oc config set-credentials ${USER_2}/cluster2 --username=${USER_2} --password=${PASSWORD_2}
$ oc config set-context cluster2 --cluster=cluster2 --user=${USER_2}/cluster2 --namespace=quotegame
Context "cluster2" modified.
```

Adding cluster 1 
```
$ argocd --insecure --grpc-web cluster add cluster1
INFO[0000] ServiceAccount "argocd-manager" created in namespace "kube-system" 
INFO[0000] ClusterRole "argocd-manager-role" updated    
INFO[0000] ClusterRoleBinding "argocd-manager-role-binding" already exists 
Cluster 'cluster1' added
```

and cluster2...
```
$ argocd --insecure --grpc-web cluster add cluster2
INFO[0000] ServiceAccount "argocd-manager" created in namespace "kube-system" 
INFO[0000] ClusterRole "argocd-manager-role" updated    
INFO[0000] ClusterRoleBinding "argocd-manager-role-binding" already exists 
Cluster 'cluster2' added
```

Adding a repository
```
$ argocd repo add https://github.com/redhat-france-sa/rhforum-2019-quotegame.git
repository 'https://github.com/redhat-france-sa/rhforum-2019-quotegame.git' added
```

Adding a new project
```
$ argocd proj create quotegame --description 'Quotegame project'
$ argocd proj add-source quotegame https://github.com/redhat-france-sa/rhforum-2019-quotegame.git
$ argocd proj add-destination quotegame ${CLUSTER_1} quotegame-prod
$ argocd proj add-destination quotegame ${CLUSTER_2} quotegame-prod
```

Create the application on cluster1
```
argocd app create --project quotegame --name cluster1-quotegame-prod --repo https://github.com/redhat-france-sa/rhforum-2019-quotegame.git --path argocd/multi-cloud/overlays/cluster1 --dest-server ${CLUSTER_1} --dest-namespace quotegame-prod --revision multi-cloud
```

# Create the application on cluster2
```
argocd app create --project quotegame --name cluster2-quotegame-prod --repo https://github.com/redhat-france-sa/rhforum-2019-quotegame.git --path argocd/multi-cloud/overlays/cluster2 --dest-server ${CLUSTER_2} --dest-namespace quotegame-prod --revision multi-cloud
```

### Organize resources