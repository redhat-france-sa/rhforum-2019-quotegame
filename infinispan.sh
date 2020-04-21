#!/bin/sh
docker run -p 11222:11222 -v "$PWD/infinispan/:/opt/infinispan/server/conf/" --entrypoint "/opt/infinispan/bin/server.sh" -it infinispan/server:10.1
