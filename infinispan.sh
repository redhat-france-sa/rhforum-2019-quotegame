# First version made for mono-cloud development.
#docker run -p 11222:11222 -it jboss/infinispan-server standalone
#docker run -p 11222:11222 -it jboss/infinispan-server:9.4.11.Final standalone
#docker run -p 11222:11222 -it infinispan/server:10.0.0.CR1-6

# Second attempts for multi-cloud development.
# 1. Default image run does not include a cache configuration named default
#docker run -p 11222:11222 -it -e USER="PJltZLXWy5" -e PASS="LDj3P1Z8Zy" infinispan/server:10.0.1.Final

# 2. Allow usage of default image with locally defined configuration.
#docker run -p 11222:11222 -it -v $(pwd)/infinispan:/user-config --entrypoint "/opt/infinispan/bin/server.sh" infinispan/server:10.0.1.Final -b SITE_LOCAL -c /user-config/infinispan-local.xml -Dinfinispan.server.config.path=/user-config

# 3. Use our custom image that already embeds everything.
docker run -p 11222:11222 -it --entrypoint "/opt/infinispan/bin/server.sh" demoforum/infinispan-local:latest -b SITE_LOCAL
