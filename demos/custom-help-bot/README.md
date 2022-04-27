# Demo Bot with Custom Help page

This provides a very simple bot in Symphony, which is covered in the [Tutorial](https://finos.github.io/spring-bot).
If you don't want to go through the tutorial and assemble the code yourself, you can download this instead and build it on your desktop.

You will need to provide settings for the `application.yml` file according to your own environment.
This will act as Bootstrap Bot code.

<br/>

### Overriding Framework provided functionality
This bot shows how you can override framework provided functionality, like `default Help page` with your own custom implementation. This might be esp. helpful in situations where you have that 1 usecase that framework is not supporting completely or as per your need.

Also, you can see in this example how we are playing with Spring Profile via `@Profile` to enable functionality for a specific Chat Platform (in this case Symphony).
