[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
[![codecov](https://codecov.io/gh/finos/spring-bot/branch/spring-bot-develop/graph/badge.svg?token=CSa0aaqYD0)](https://codecov.io/gh/finos/spring-bot)
![License](https://img.shields.io/github/license/finos/spring-bot)

|Version          |Number                 |Status                  |
|-----------------|-----------------------|------------------------|
|Spring Bot            |(unreleased)      |![build](https://github.com/finos/spring-bot/workflows/sjt-build/badge.svg)
|Symphony Java Toolkit |[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=org.finos.symphony.toolkit)|![build](https://github.com/finos/spring-bot/workflows/sjt-build/badge.svg?branch=symphony-java-toolkit-master)|

![Spring Bot Logo](logo.png)

# Spring Bot

Spring Bot is a set of [Spring Boot](https://spring.io/projects/spring-boot) starters designed for building chat bots in Java & Kotlin. 

## What Is It?

 - Spring Bot provides a simple annotation-based way to create chat bots. 
 - It currently supports Symphony and Microsoft Teams.
 - It allows you to build your chatbot using annotations, binding messages to methods in a similar way to [Spring Web MVC](https://docs.spring.io/spring-framework/docs/6.0.x/reference/html/web.html#spring-web)
 
## Why Is This Important?

- Like it or not, most of us find ourselves working in environments with _multiple chat platforms_.  As bot developers, we need to be able to reach users irrespective of which platform they are on.
- Developers of chat-bots are not well-supported with Open-Source tooling.  Too much effort gets spent on low-level message-handling, which could easily be done by a library.
- Efforts such as the Microsoft Bot Framework support multiple chat platforms at the expense of locking you into the Azure ecosystem.

## How Does It Work?
 
This is best described on our microsite [springbot.finos.org](https://springbot.finos.org).  This takes you through the process of building several example bots from scratch.

## What Are The Benefits?

### ✔️ Build Bots that run on multiple platforms

> By using Spring Bot's abstractions, you can target both Symphony and Microsoft Teams.  Depending on demand (or contributions) further platforms may be supported in the future.  In the tutorials we show how to build a [claim processing bot](https://springbot.finos.org/getting-started.html) which runs a workflow concurrently on Teams and Symphony.

### ✔️Focus On Business Logic

> Spring Bot handles marshalling from/to the underlying message formats on your platform.  

### ✔️ Advanced Templating

> Templates are automatically generated for any Java objects (POJOs) that you want to send to the chat platform.  This allows users to read/edit objects and send them back to your bot.  Additionally, templates are fully customizable on a per-platform basis, currently using Freemarker and Thymeleaf.   

### ✔️Annotations Make It Super-Easy

> As the [tutorials](https://springbot.finos.org) show, you just drop a few annotations into your application to enable it to read and send chat messages.  

### ✔️ A Mature Ecosystem 

> By building bots in a mature ecosystem like Java, you avoid all of the usual issues with no-code and low-code environments.  Java is a mature language with every conceivable library, a wealth of Stack-Overflow QA's and a convincing DevOps/Deployment story (Java runs anywhere).  

### ✔️ Open Source

> Most low-code / no-code platforms are a trap:  they are looking to lock-in customers and revenue streams by persuading you to build your business with them as a dependency.  Spring Bot is open-source so this is literally a non-issue.


## Plenty Of Example Bots To Learn From

This project contains various example bots that were (originally) written at Deutsche Bank in response to real use-cases.  However, we felt they were general enough to warrant Open-Sourcing and including as code to work from.  

- 👍 All of these bots work on Microsoft Teams and Symphony, and are deployed into maven central as executable jars.

### 🤖 Poll Bot 


A bot for running polls in a Symphony chat room.  

 - [View the README](tools/poll-bot/README.md)
 - _ 👍 Production Ready_
 

### 🤖 News (RSS) Bot 

A bot for feeding news into a Symphony chat room. 

 - [View the README](tools/rss-bot/README.md)
 - _ 👍 Production Ready_


### 🤖 Reminder Bot

Reads the contents of a room and creates reminders based on future events mentioned therein.

 - [View the README](tools/reminder-bot/README.md)
 - _ 👍 Production Ready_


## Plenty of Tutorials / Demo Code

Inside the `/demos` directory you can see three example bots:

- `demo-bot` : The "Hello World" of Spring Bot 
- `todo-bot` : Runs a simple todo-list inside a chat room on Teams or Symphony.
- `claim-bot`: An example of an expense-claim workflow running across Teams and Symphony concurrently.

Tutorials for building these are found here:  [Spring Bot](https://springbot.finos.org)

# Libraries

## Chat Workflow

Chat Workflow provides interfaces, annotations and utilities for expressing bot workflows in Spring.

The Chat Workflow Symphony Starter implements this for symphony.  It is intended that beyond _configuration_ the developer will not have to understand _any_ Symphony APIs to build work-flows and have them run in Symphony.

 - [View the README](libs/chat-workflow/README.md)
 - [View the Documentation Site](https://finos.github.io/spring-bot) 
 
## Chat Workflow Testing

Some useful functionality for testing your bots without having to integrate with Teams / Symphony. 

## Entity JSON 

This is a utility that helps you serialize/deserialize JSON in a JSON format which contains enough information about java types to reconstruct objects.

 - [View the README](libs/entity-json/README.md)

## Symphony BDK Chat Workflow Spring Boot Starter

- Allows Spring Bot to expose bots on Symphony.  (see tutorial)

## Teams Chat Workflow Spring Boot Starter

- Allows Spring Bot to expose bots on Microsoft Teams.  (see tutorial)

## Symphony Entities

- Java classes that can reconstruct Symphony classes such as HashTags, CashTags, Users etc.

# Tools


# License

Copyright 2022 FINOS

Distributed under the Apache License, Version 2.0.

SPDX-License-Identifier: Apache-2.0


# Branches

There are two main branches:

`spring-bot-master`:  new code for the multi-platform Symphony / Microsoft Teams bot builder
`symphony-java-toolkit-master`: legacy code for when this project only supported symphony.

# Releasing This Project (For Maintainers)

In order to do a release:

1.  Changes.md

- Update this file to include details of the release.

2. On Releaser's machine:

```
mvn clean test -P symphony-ci
mvn versions:set -DnewVersion=<our breaking change no>.<minor version no>.<our-patch-version-no> -DartifactId=\*  -DgroupId=\*
mvn -DskipTests clean test source:jar javadoc:javadoc deploy -P symphony-release
mvn versions:set -DnewVersion=<our breaking change no>.<symphony-api-version>.<our-release-no+1>-SNAPSHOT -DartifactId=\*  -DgroupId=\*
# then push to git
```

3.  On `oss.sonatype.org`

- Close the Staging Repository
- Release it.

4.  Perform release on github with same number
 

