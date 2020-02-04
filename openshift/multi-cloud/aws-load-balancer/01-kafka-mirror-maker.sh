
# Connect cluster 1 and retrieve route hostname for the my-cluster-kafka-bootstrap route
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
KAFKABOOTSTRAP_1=`oc get route my-cluster-kafka-bootstrap -o=jsonpath='{.spec.host}' -n quotegame`
oc extract -n quotegame secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > site1-ca.crt

# Connect cluster 2 and retrieve route hostname for the my-cluster-kafka-bootstrap route
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
KAFKABOOTSTRAP_2=`oc get route my-cluster-kafka-bootstrap -o=jsonpath='{.spec.host}' -n quotegame`
oc extract -n quotegame secret/my-cluster-cluster-ca-cert --keys=ca.crt --to=- > site2-ca.crt

# Create a KafkaMirroMaker for quotegame-workingmemory from site2 to site1
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
oc create -n quotegame secret generic my-cluster-site2-cluster-ca-cert --from-file=ca.crt=site2-ca.crt
cat <<EOF | oc create -n quotegame -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaMirrorMaker
metadata:
  name: my-mirror-maker
spec:
  version: 2.3.0
  replicas: 1
  consumer:
    bootstrapServers: $KAFKABOOTSTRAP_2:443
    groupId: 'my-mirror-maker-site1'
    tls:
      trustedCertificates:
        - secretName: my-cluster-site2-cluster-ca-cert
          certificate: ca.crt
  producer:
    bootstrapServers: my-cluster-kafka-bootstrap:9092
  whitelist: 'quotegame-workingmemory-site2'
EOF
    
# Create a KafkaMirroMaker for quotegame-workingmemory from site1 to site2
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
oc create -n quotegame secret generic my-cluster-site1-cluster-ca-cert --from-file=ca.crt=site1-ca.crt
cat <<EOF | oc create -n quotegame -f -
apiVersion: kafka.strimzi.io/v1beta1
kind: KafkaMirrorMaker
metadata:
  name: my-mirror-maker
spec:
  version: 2.3.0
  replicas: 1
  consumer:
    bootstrapServers: $KAFKABOOTSTRAP_1:443
    groupId: 'my-mirror-maker-site2'
    tls:
      trustedCertificates:
        - secretName: my-cluster-site1-cluster-ca-cert
          certificate: ca.crt
  producer:
    bootstrapServers: my-cluster-kafka-bootstrap:9092
  whitelist: 'quotegame-workingmemory-site1'
EOF