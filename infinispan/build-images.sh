# Build images containing container-cache template definition for both local and xsite.
docker build -f Dockerfile.local . -t demoforum/infinispan-local
docker build -f Dockerfile.xsite . -t demoforum/infinispan-xsite