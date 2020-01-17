

### Demonstration scenario

#### Cluster failure

First login to `http://quotegame.redhat.containous.tech` and check the color of the screen and the cluster you're on (it may be green/EU or orange/US) depending on your location. Let call it `W1` for Window 1.

Open another browser window and connect to the other cluster using the OpenShift direct Route (`http://quotegame-api-quotegame.apps.cluster-paris-c03a.paris-c03a.example.opentlc.com/` for example). Let call it `W2` for Window 2.

On `W1`, connect as a user and buy a stock action. Do the same thing with same user on `W2`. Buy an action on `W1` and check the synchronisation of the portfolio on `W2`.

Now, simulate a cluster outage by removing the ping TraefikEE on the first cluster:

```
$ oc delete ingress/ping -n traefikee
ingress.extensions "ping" deleted
```

Then buy a new stock action on `W2`. After few seconds, synchronisation should have occured on the other way on `W1` thus because the Javascript part that was calling the service has been redirected to `W2` cluster API.

Now hit refresh on the `W1` brower and the header should change its color.

You could now relaunch the cluster by recreating the ping service:

```
$ cat <<EOF | oc create -n traefikee -f -
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: ping
  namespace: traefikee
  annotations:
    kubernetes.io/ingress.class: 'traefik'
spec:
  rules:
    - http:
        paths:
          - path: "/ping"
            backend:
              serviceName: traefikee-ping
              servicePort: 4545
EOF
```

Wait a few seconds before refreshing your browser `W1` and it should swicth back to first color.

#### API failure

```
$ oc scale deployment/quotegame-api --replicas=0 -n quotegame
deployment.extensions/quotegame-api scaled
```


#### PriceUpdater failure


