#docker run -p 11222:11222 -it jboss/infinispan-server standalone
#docker run -p 11222:11222 -it jboss/infinispan-server:9.4.11.Final standalone
#docker run -p 11222:11222 -it infinispan/server:10.0.0.CR1-6

#docker run -p 11222:11222 -it -e USER="PJltZLXWy5" -e PASS="LDj3P1Z8Zy" infinispan/server:10.0.1.Final
docker run -p 11222:11222 -it -v $(pwd)/infinispan:/user-config --entrypoint "/opt/infinispan/bin/server.sh" infinispan/server:10.0.1.Final -b SITE_LOCAL -c /user-config/infinispan-local.xml -Dinfinispan.server.config.path=/user-config
#docker run -p 11222:11222 -it --entrypoint "/opt/infinispan/bin/server.sh" demoforum/infinispan-local:latest -b SITE_LOCAL
