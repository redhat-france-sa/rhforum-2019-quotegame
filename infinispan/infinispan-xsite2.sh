#docker run -p 11322:11222 -p 17200:7200 -it -v $(pwd):/user-config -e CONFIG_PATH="/user-config/config.yaml" infinispan/server:10.0.1.Final

docker run -p 11322:11222 -p 17200:7200 -it --entrypoint "/opt/infinispan/bin/server.sh" demoforum/infinispan-xsite:latest -b SITE_LOCAL \
    -Dapp.site=SITE2 -Djgroups.external.addr=docker.for.mac.localhost -Djgroups.initial.hosts=docker.for.mac.localhost[7200],docker.for.mac.localhost[17200]