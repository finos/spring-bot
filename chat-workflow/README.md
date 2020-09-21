[![Maven Central](https://img.shields.io/maven-central/v/com.github.deutschebank.symphony/symphony-java-client-parent)](https://search.maven.org/search?q=com.github.deutschebank.symphony)

# Spring Boot Starter for Chat Workflow

This module provides a one-stop-shop for constructing work-flows in Java.  It is intended that beyond _configuration_ (see below), the developer will not have to understand _any_ Symphony APIs to build initial proof-of-concept work-flows and have them run in Symphony.

## Installation

1.  You will need a bean exposed in Spring implementing the interface `com.github.deutschebank.symphony.workflow.Workflow`.

2.  You will need to configure the `application.yml` for a bot as described in [Spring Boot Starter for Symphony Api](../symphony-api-spring-boot-starter/README.md).

3.  Add the following dependency in your application:

```xml
<dependency>
  <groupId>com.github.deutschebank.symphony</groupId>
  <artifactId>chat-workflow</artifactId>
  <version>--see above--</version>
</dependency>
```

NB:  Chat workflow will bring in the other Spring Boot Starters transitively, so you don't need to specify those.

4.  Add the dependency for a JAX-RS implementation, as described [here](../symphony-api-spring-boot-starter/README.md).

## Overview

### What Exactly is a Work-Flow?



A _workflow_ here is a sequence of interactions carried out by a group of users, coordinated with a bot.  For example, an expenses workflow might look like this:

- User A _raises_ a claim containing some line-item expenses.
- The claim passes to User B to approve.
- User C is required to process the claim and transfer A some money.  

This could be easily converted into some 











 
## Tutorial

 - [Read the Bot-Building Tutorial](tutorials/Spring-Boot-Bot.md)

