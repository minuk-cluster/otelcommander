# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.4/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.4/gradle-plugin/packaging-oci-image.html)
* [Coroutines section of the Spring Framework Documentation](https://docs.spring.io/spring/docs/6.1.13/spring-framework-reference/languages.html#coroutines)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.3.4/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers MongoDB Module Reference Guide](https://java.testcontainers.org/modules/databases/mongodb/)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#web.reactive)
* [Spring Data Reactive MongoDB](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#data.nosql.mongodb)
* [OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
* [Testcontainers](https://java.testcontainers.org/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.3.4/reference/htmlsingle/index.html#actuator)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [Declarative REST calls with Spring Cloud OpenFeign sample](https://github.com/spring-cloud-samples/feign-eureka)

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.3.4/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`mongo:latest`](https://hub.docker.com/_/mongo)

Please review the tags of the used images and set them to the same as you're running in production.

