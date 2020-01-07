# Use this script to batch generated all container images for the application.
export CONTAINER_REGISTRY=quay.io/demoforum

cd quotegame-model
mvn clean install

cd ../quotegame-ui
mvn clean install

cd ../quotegame-api
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f src/main/docker/Dockerfile.native -t demoforum/quotegame-api:multi-cloud .
docker tag demoforum/quotegame-api:multi-cloud $CONTAINER_REGISTRY/quotegame-api:multi-cloud
docker push $CONTAINER_REGISTRY/quotegame-api:multi-cloud

cd ../quotegame-processors
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f src/main/docker/Dockerfile.native -t demoforum/quotegame-processors:multi-cloud .
docker tag demoforum/quotegame-processors:multi-cloud $CONTAINER_REGISTRY/quotegame-processors:multi-cloud
docker push $CONTAINER_REGISTRY/quotegame-processors:multi-cloud

cd ../quotegame-rebalancer
# Camel Quarkus issue in native image: Camel cannot set kafka component attributes... Switch to JVM.
#mvn clean package -Pnative -Dnative-image.docker-build=true
#docker build -f src/main/docker/Dockerfile.native -t demoforum/quotegame-rebalancer:multi-cloud .
mvn clean package
docker build -f src/main/docker/Dockerfile.jvm -t demoforum/quotegame-rebalancer:multi-cloud .
docker tag demoforum/quotegame-rebalancer:multi-cloud $CONTAINER_REGISTRY/quotegame-rebalancer:multi-cloud
docker push $CONTAINER_REGISTRY/quotegame-rebalancer:multi-cloud

cd ../quotegame-priceupdater
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f src/main/docker/Dockerfile.native -t demoforum/quotegame-priceupdater:multi-cloud .
docker tag demoforum/quotegame-priceupdater:multi-cloud $CONTAINER_REGISTRY/quotegame-priceupdater:multi-cloud
docker push $CONTAINER_REGISTRY/quotegame-priceupdater:multi-cloud

cd ../quotegame-chaosmonkey
mvn clean package -Pnative -Dnative-image.docker-build=true
docker build -f src/main/docker/Dockerfile.native -t demoforum/quotegame-chaosmonkey:multi-cloud .
docker tag demoforum/quotegame-chaosmonkey:multi-cloud $CONTAINER_REGISTRY/quotegame-chaosmonkey:multi-cloud
docker push $CONTAINER_REGISTRY/quotegame-chaosmonkey:multi-cloud
