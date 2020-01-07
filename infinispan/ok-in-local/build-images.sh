# Build images containing container-cache template definition for both local and xsite.
docker build -f Dockerfile.local . -t demoforum/infinispan-local
docker build -f Dockerfile.xsite . -t demoforum/infinispan-xsite

# Uncomment these last line if you want to push to your own repository.
#export CONTAINER_REGISTRY=quay.io/demoforum
#docker tag demoforum/infinispan-xsite:latest $CONTAINER_REGISTRY/infinispan-xsite:latest
#docker push $CONTAINER_REGISTRY/infinispan-xsite:latest