[![FINOS - Incubating](https://cdn.jsdelivr.net/gh/finos/contrib-toolbox@master/images/badge-incubating.svg)](https://finosfoundation.atlassian.net/wiki/display/FINOS/Incubating)
![build](https://github.com/finos/spring-bot/workflows/sjt-build/badge.svg)
[![Coverage](https://img.shields.io/codecov/c/github/finos/symphony-java-toolkit)](https://app.codecov.io/gh/finos/symphony-java-toolkit)
![License](https://img.shields.io/github/license/finos/spring-bot)

|-----------------|-----------------------|
|Symphony Java Toolkit |[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=org.finos.symphony.toolkit)|
|Spring Bot            |(unreleased)      |

# Main Documentation

Please see the Github Pages site here:  [Spring Bot](https://finos.github.io/spring-bot)

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

_All bots are deployed into maven central as executable jars_.

## Poll Bot

A bot for running polls in a Symphony chat room.  Built using chat-workflow.

 - [View the README](tools/poll-bot/README.md)

## News (RSS) Bot

A bot for feeding news into a Symphony chat room.  Built using chat-workflow.

- [View the README](tools/rss-bot/README.md)


## Reminder Bot

Reads the contents of a room and creates reminders based on future events mentioned therein.

- [View the README](tools/reminder-bot/README.md)


# License

This project is licensed under the [Apache 2.0 License](LICENSE).


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
 

