
# Create a new namespace for ArgoCD components
oc create namespace argocd

# Apply the ArgoCD Install Manifest
oc -n argocd apply -f https://raw.githubusercontent.com/argoproj/argo-cd/v1.3.6/manifests/install.yaml

# Get the ArgoCD Server password
ARGOCD_SERVER_PASSWORD=$(oc -n argocd get pod -l "app.kubernetes.io/name=argocd-server" -o jsonpath='{.items[*].metadata.name}')

# Patch ArgoCD Server so no TLS is configured on the server (--insecure)
PATCH='{"spec":{"template":{"spec":{"$setElementOrder/containers":[{"name":"argocd-server"}],"containers":[{"command":["argocd-server","--insecure","--staticassets","/shared/app"],"name":"argocd-server"}]}}}}'
oc -n argocd patch deployment argocd-server -p $PATCH

# Expose the ArgoCD Server using an Edge OpenShift Route so TLS is used for incoming connections
oc -n argocd create route edge argocd-server --service=argocd-server --port=http --insecure-policy=Redirect


# Get ArgoCD Server Route Hostname
ARGOCD_ROUTE=$(oc -n argocd get route argocd-server -o jsonpath='{.spec.host}')

# Login with the current admin password
argocd --insecure --grpc-web login ${ARGOCD_ROUTE}:443 --username admin --password ${ARGOCD_SERVER_PASSWORD}

# Update admin's password
argocd --insecure --grpc-web --server ${ARGOCD_ROUTE}:443 account update-password --current-password ${ARGOCD_SERVER_PASSWORD} --new-password admin




# Re-login 
argocd --insecure --grpc-web login ${ARGOCD_ROUTE}:443 --username admin --password admin

# Defining contexts for cluster1 and cluster2
oc config set-context cluster1 --cluster=api-cluster-paris-a000-paris-a000-example-opentlc-com:6443 --user=opentlc-mgr/api-cluster-paris-a000-paris-a000-example-opentlc-com:6443 --namespace=quotegame

oc config set-context cluster2 --cluster=api-cluster-paris-4220-paris-4220-example-opentlc-com:6443 --user=opentlc-mgr/api-cluster-paris-4220-paris-4220-example-opentlc-com:6443 --namespace=quotegame

# Adding cluster 1 and cluster2
argocd --insecure --grpc-web cluster add cluster1
argocd --insecure --grpc-web cluster add cluster2


# Adding a repository
argocd repo add https://github.com/redhat-france-sa/rhforum-2019-quotegame.git

# Adding a project
argocd proj create quotegame --description 'Quotegame project'
argocd proj add-source quotegame https://github.com/redhat-france-sa/rhforum-2019-quotegame.git
argocd proj add-destination quotegame https://api.cluster-paris-a000.paris-a000.example.opentlc.com:6443 ''
argocd proj add-destination quotegame https://api.cluster-paris-4220.paris-4220.example.opentlc.com:6443 ''

# Create the application on cluster1
argocd app create --project quotegame --name cluster1-quotegame-prod --repo https://github.com/redhat-france-sa/rhforum-2019-quotegame.git --path argocd/manifests/multi-cloud/base --dest-server https://api.cluster-paris-a000.paris-a000.example.opentlc.com:6443 --dest-namespace quotegame-prod --revision multi-cloud

# Create the application on cluster2
argocd app create --project quotegame --name cluster2-quotegame-prod --repo https://github.com/redhat-france-sa/rhforum-2019-quotegame.git --path argocd/manifests/multi-cloud/base --dest-server https://api.cluster-paris-4220.paris-4220.example.opentlc.com:6443 --dest-namespace quotegame-prod --revision multi-cloud
