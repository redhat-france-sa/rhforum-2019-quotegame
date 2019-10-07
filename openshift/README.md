

## Building container images on OpenShift

On the OpenShift side, you may want to create a generic Source-to-image binary build configuration. This build will used the local `Dockerfile.native` that will be injected during the build. We do that by patching the BuildConfig once created:

```
$ oc new-build --binary --name=quotegame-api -l app=quotegame-api
$ oc patch bc/quotegame-api -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'
```

If you didn’t delete the generated native executable in `/target`directory, you can start the OpenShift build:

```
$ oc start-build quotegame-api --from-dir=. --follow 
```
