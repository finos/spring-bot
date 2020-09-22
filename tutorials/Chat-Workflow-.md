# Building A Chat Workflow demo application With Spring Boot

This tutorial will take you through the process of creating a new chat workflow application
and running it inside the Symphony container.

Let's go!

## 1. Create a New Spring Boot Project

Let's head over to `https://start.spring.io` and create a new app
project:

![New App Project](./chat-workflow/media/image01.png)

-   I've created a maven project, with actuator and devtools support
    built in.
-   When you click download, it will deliver a zip file which you can
    unpack in your file-system.

## 2. Import Into Your IDE

![Loaded in the IDE](./app/media/image02.png)

In this image, I'm downloading and unpacking Spring's zip file.  Next, I'm going to import the project
into Eclipse, but  If you are using a different IDE, substitute your step here.

Here is my project in eclipse:

![In eclipse](./app/media/image03.png)

-   If I run the project now, I should see Spring start, and then finish
    (as we haven\'t actually added any code).

## 3. Add The Spring-Boot-Starter-Symphony-Chat-Workflow Dependency

In `pom.xml` add this dependency:

```
<dependency>
	<groupId>com.github.deutschebank.symphony</groupId>
	<artifactId>chat-workflow</artifactId>
	<version>--latest version --</version>
</dependency>
```

Latest version: 

![Maven Central](https://img.shields.io/maven-central/v/com.github.deutschebank.symphony/symphony-java-client-parent)

It looks like this for me: 

![added dependency](./app/media/image04.png)