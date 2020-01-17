# Install traefikeectl locally
# curl -fL https://s3.amazonaws.com/traefikee/binaries/v1.2.1/traefikeectl/traefikeectl_v1.2.1_darwin_amd64.tar.gz | tar -xzf -

# Connect cluster 1 and retrieve LoadBalancer hostname for the infinispan-site1 service
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
TRAEFIKEE_LB_1=`oc get service traefikee-lb -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}' -n traefikee`

# Connect cluster 2 and retrieve LoadBalancer hostname for the infinispan-site2 service
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
TRAEFIKEE_LB_1=`oc get service traefikee-lb -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}'  -n traefikee`

# Connect to cluster representing site1 and apply CLI command
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
./traefikeectl connect --kubernetes --clustername=traefikee-site1
./traefikeectl deploy --clustername=traefikee-site1 \
    --defaultentrypoints=http,https \
    --entryPoints='Name:http Address::80' \
    --entryPoints='Name:https Address::443 TLS' \
    --logLevel=INFO \
    --kubernetes \
    --fallbackurls="http://$TRAEFIKEE_LB_1"

# Connect to cluster representing site2 and apply deploy CLI command
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
./traefikeectl connect --kubernetes --clustername=traefikee-site2
./traefikeectl deploy --clustername=traefikee-site2 \
    --defaultentrypoints=http,https \
    --entryPoints='Name:http Address::80' \
    --entryPoints='Name:https Address::443 TLS' \
    --logLevel=INFO \
    --kubernetes \
    --fallbackurls="http://$TRAEFIKEE_LB_2"