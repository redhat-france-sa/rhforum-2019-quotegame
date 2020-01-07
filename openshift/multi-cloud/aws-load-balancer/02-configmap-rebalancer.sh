
# Connect cluster 1 and retrieve route hostname for the quotegame-priceupdater component
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
PRICEUPDATER_1=`oc get route quotegame-priceupdater -o=jsonpath='{.spec.host}'`

# Connect cluster 1 and retrieve route hostname for the quotegame-priceupdater component
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
PRICEUPDATER_2=`oc get route quotegame-priceupdater -o=jsonpath='{.spec.host}'`

# Create a config map for quotegame-rebalancer with the 2 endpoints
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
oc delete cm quotegame-rebalancer-config -n quotegame 
cat <<EOF | oc create -n quotegame -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: quotegame-rebalancer-config
data:
  application.properties: |-
    %kube.priceUpdater.endpoints: netty-http:http://$PRICEUPDATER_1/api/order,netty-http:http://$PRICEUPDATER_2/api/order
EOF

# Create a config map for quotegame-rebalancer with the 2 endpoints
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
oc delete cm quotegame-rebalancer-config -n quotegame 
cat <<EOF | oc create -n quotegame -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: quotegame-rebalancer-config
data:
  application.properties: |-
    %kube.priceUpdater.endpoints: netty-http:http://$PRICEUPDATER_1/api/order,netty-http:http://$PRICEUPDATER_2/api/order
EOF