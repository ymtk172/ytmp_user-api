# user-api

See [Gist](https://gist.github.com/ymtk172/4a6b4b8a45da38627b63ddf0c3ef08b5)

# Cloud Native Buildpack

First, set `docker repository url` and others in environment variables.
This `pom.xml` supports below.

```shell
export USER_API_DOCKER_REPOSITORY=
export HTTP_PROXY=
export HTTPS_PROXY=
```

Then, build image.

```shell
./mvnw spring-boot:build-image
```

Run.

```shell
winpty docker run -it -p9081:9081 user-api:0.0.2-SNAPSHOT
```
