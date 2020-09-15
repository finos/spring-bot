[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![BuildStatus](https://img.shields.io/travis/deutschebank/symphony-java-client-parent)](https://travis-ci.org/deutschebank/symphony-java-client-parent)
![Version](https://img.shields.io/bintray/v/deutschebank/SymphonyPractice/symphony-java-client-parent)
[![Coverage](https://img.shields.io/codecov/c/github/deutschebank/symphony-java-client-parent)](https://codecov.io/gh/deutschebank/symphony-java-client-parent)
![License](https://img.shields.io/github/license/deutschebank/symphony-java-client-parent)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.deutschebank.symphony/symphony-java-client-parent)](https://search.maven.org/search?q=com.github.deutschebank.symphony)

## What This Is

This project is a set of libraries aimed at facilitating creating Bots and Apps for the [Symphony Chat Platform](https://symphony.com) using Java and [Spring Boot](https://spring.io/projects/spring-boot).

This project contains the following:

- Symphony Java Client Bindings
- Symphony Java Client Identity
- Symphony API Spring-Boot Starter
- Symphony App Spring-Boot Starter
- Symphony JSON Support
- Demo App 
- Demo Bot
- Tutorials for getting started
- Symphony FIX Support
- A Symphony Maven Build Notifier
- Symphony Shared Stream

Read further to see what each of these includes.

## Dependencies

The first four projects have dependencies on one another, expressed through this diagram.  If you are using the higher elements in the diagram, you'll automatically depend on the lower ones.  However, if you're building bots in Spring Boot (say), then you'll not need the top-most dependency. 

![Dependency Cake Diagram](cake.jpg)

## Symphony Java Client Bindings

This provides a JAX-RS standard, **Type-Safe** means of interacting with [Symphony](https://symphony.com) pods via REST apis.

`symphony-java-client-bindings` is a set of swagger-generated domain objects and interfaces that provide a Java API for interacting with Symphony's REST endpoints. 

- [Symphony REST Endpoint Documentation](https://rest-api.symphony.com/reference)
- [Symphony REST Endpoint Swagger Definitions](https://github.com/symphonyoss/symphony-api-spec)

This is used in preference to Symphony's own API as the configuration can be left to spring boot, and you can use Spring's starters for JAX-RS to build your application.

[View The Readme](bindings/README.md)

## Symphony Java Client Identity

`symphony-java-client-identity` provides a single abstraction of `SymphonyIdentity`, which encapsulates the data needed to authenticate with a symphony server.  That is:
 
 - Private Key
 - Certificates
 - Common Name
 - Email Address

This is a highly convenient abstraction which works with Spring Boot's use of [Jackson](https://github.com/FasterXML/jackson) and [YAML](https://yaml.org) to describe Bot and App identities in Java.

[View The Readme](identity/README.md)

## Symphony API Spring Boot Starter

Provides Spring-Based configuration and auto-wiring for Symphony APIs.  This is perfect if you are building a Symphony Bot using Spring Boot.

 - [View the Readme](symphony-api-spring-boot-starter/README.md)
 - [Read the Bot-Building Tutorial](tutorials/Spring-Boot-Bot.md)
 - [View Demo Bot Code](https://github.com/robmoffat/symphony-java-client-parent/tree/master/demos/demo-bot)

## Symphony App Spring Boot Starter

If you are building a Symphony App, there are multiple concerns around app security, networking, describing your application and handling user authentication.  

This provides you with an out-of-the-box way to get started and build an app with all of these concerns taken care of.

 - [View the Readme](symphony-app-spring-boot-starter/README.md)
 - [Read the App-Building Tutorial](tutorials/Spring-Boot-App.md)
 - [View Demo App Code](https://github.com/robmoffat/symphony-java-client-parent/tree/master/demos/demo-app)

## Symphony JSON Support

This is a utility that helps you serialize/deserialize JSON in a format suitable for the JSON data payload of a Symphony Message, using Jackson.

 - [View the Readme](entity-json/README.md)
 - [Read the Tutorial](tutorials/Entity-JSON.md)
 
## QuickFIX Support

This is a utility that helps you serialize/deserialize QuickFIX messages into JSON Format, as described [here](https://github.com/FIXTradingCommunity/fix-json-encoding-spec/blob/master/Encoding_FIX_using_JSON-User_Guide.md).

Although this is not reliant on Symphony code, it is adjacent tech and so might be useful for sending FIX payloads within Symphony.

 - [View the Readme](quickfix-json/README.md)
 
## Maven Build Reporter

Provides a maven extension that allows you to report build results into a Symphony chat room.  

 - [View the Readme](symphony-maven-build-reporter/README.md)
 
## Symphony Shared Stream

The purpose of this module is to provide a drop-in stream consumer for Symphony that allows a cluster of bot replicas to coordinate work.  That is, prevent the problem of two or more bots simultaneously consuming and processing the same Symphony message.

 - [View the Readme](shared-stream/README.md)

## License

This project is licensed under the [Apache 2.0 License](License).

## Releasing This Project (For Maintainers)

In order to do a release:

1. On Releaser's machine:

```
mvn test
mvn versions:set -DnewVersion=<our breaking change no>.<symphony-api-version>.<our-release-no> -DartifactId=*  -DgroupId=*
mvn -DskipTests -pl '!demos/demo-bot,!demos/demo-app' clean test source:jar javadoc:javadoc deploy 
mvn versions:set -DnewVersion=<our breaking change no>.<symphony-api-version>.<our-release-no+1>-SNAPSHOT -DartifactId=*  -DgroupId=*
# then push to git
```

2.  In Bintray:

Jcenter release is performed automatically.
To release to maven central: https://bintray.com/package/central/deutschebank/SymphonyPractice/symphony-java-client-parent#central
Enter details and click sync.

