# Install skupper locally
# curl -fL https://github.com/skupperproject/skupper-cli/releases/download/0.0.2/skupper-cli-0.0.2-mac-amd64.tgz | tar -xzf -

# Connected to cluster representing site1
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
./skupper init
./skupper status
./skupper connection-token site1-secret.yml

# Connected to cluster representing site2
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
./skupper init
./skupper status
./skupper connection-token site2-secret.yml
./skupper connect site1-secret.yml
./skupper status --list-connectors

# Connected to cluster representing site1
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
./skupper connect site2-secret.yml
./skupper status --list-connectors