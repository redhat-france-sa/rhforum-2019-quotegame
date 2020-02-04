
# Connect cluster 1 and retrieve LoadBalancer hostname for the infinispan-site1 service
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
INFINISPAN_1=`oc get service infinispan-site1 -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}' -n quotegame`

# Connect cluster 2 and retrieve LoadBalancer hostname for the infinispan-site2 service
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
INFINISPAN_2=`oc get service infinispan-site2 -o=jsonpath='{.status.loadBalancer.ingress[0].hostname}' -n quotegame`


# Connect cluster 2 and retrieve LoadBalancer hostname for the infinispan-site2 service
# Change update strategy to force redeploy of all pods after next change
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
oc set env statefulset/infinispan EXTERNAL_ADDR=$INFINISPAN_1 -n quotegame
oc patch statefulset/infinispan -p '{"spec":{"updateStrategy":{"type":"RollingUpdate"}}}' -n quotegame
oc set env statefulset/infinispan INITIAL_HOSTS=$INFINISPAN_1[7900],$INFINISPAN_2[7900] -n quotegame

# Connect cluster 2 and retrieve LoadBalancer hostname for the infinispan-site2 service
# Change update strategy to force redeploy of all pods after next change
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
oc set env statefulset/infinispan EXTERNAL_ADDR=$INFINISPAN_2 -n quotegame
oc patch statefulset/infinispan -p '{"spec":{"updateStrategy":{"type":"RollingUpdate"}}}' -n quotegame
oc set env statefulset/infinispan INITIAL_HOSTS=$INFINISPAN_1[7900],$INFINISPAN_2[7900] -n quotegame