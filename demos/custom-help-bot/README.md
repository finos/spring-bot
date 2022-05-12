# Demo Bot with Custom Help page

This bot provides an example of how you can override "baked-in" beans within Spring.  In this case, we are overriding the `HelpController` bean with our own, custom `CustomHelpController`.  

This is applicable to all the beans in Spring Bot.

You will need to provide settings for the `application.yml` file according to your own environment.  Since this bot only supports Symphony help pages, you should be sure to make sure you don't use the teams profile.  In your `application.yml`, include:

```
spring.profiles.active: symphony
```
