# Set this variable to Operator version
TRAEFIKEE_VERSION=v0.2.0

# Connected to cluster representing site1
oc login $CLUSTER_1 -u $USER_1 -p $PASSWORD_1
oc new-project traefikee
oc create -n traefikee secret generic license --from-literal=license=${TRAEFIKEE_LICENSE_KEY}

oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/crds/containo.us_traefikees_crd.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/service_account.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/role.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/role_binding.yaml

# Apply OpenShift specific part (only on master branch for now)
oc apply -f https://github.com/containous/traefikee-operator/raw/master/deploy/traefikee-scc.yaml
oc adm policy add-scc-to-user anyuid -z default -n traefikee

# Download, adapt and install operator
curl -fL https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/operator.yaml -o operator.yaml
sed -i "" 's|{{REPLACE_IMAGE}}|containous/traefikee-operator:debug|g' operator.yaml
oc apply -f operator.yaml -n traefikee

# Connected to cluster representing site2
oc login $CLUSTER_2 -u $USER_2 -p $PASSWORD_2
oc new-project traefikee
oc create -n traefikee secret generic license --from-literal=license=${TRAEFIKEE_LICENSE_KEY}

oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/crds/containo.us_traefikees_crd.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/service_account.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/role.yaml
oc apply -f https://github.com/containous/traefikee-operator/raw/$TRAEFIKEE_VERSION/deploy/role_binding.yaml

# Apply OpenShift specific part (only on master branch for now)
oc apply -f https://github.com/containous/traefikee-operator/raw/master/deploy/traefikee-scc.yaml
oc adm policy add-scc-to-user anyuid -z default -n traefikee

# Install operator
oc apply -f operator.yaml -n traefikee
