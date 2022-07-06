# Demo Bot with Custom Help page

This bot provides an example of how you can override "baked-in" beans within Spring.  In this case, we are overriding the `HelpController` bean with our own, custom `CustomHelpController`.  

This is applicable to all the beans in Spring Bot.

You will need to provide settings for the `application.yml` file according to your own environment.

----
For instance, you can change below `default` Symphony help page -

![Symphony Standard Help Page](img/symphony_std_help.JPG)

With this custom Expandable Card help page -

![Symphony Custom Help Page](img/symphony_custom_help_expand.JPG)

Help page expanded

![Symphony Custom Expanded Help Page](img/symphony_custom_help_collapse.JPG)

----
And for MS Teams, you can change for Channel & Chat from -

![MS Teams Standard Channel Help Page](img/teams_std_channel_help.JPG) ![MS Teams Standard Chat Help Page](img/teams_std_chat_help.JPG)

To consistent AdaptiveCard across Teams Channel & Chat -

![MS Teams Custom Channel Help Page](img/teams_custom_channel_help.JPG) ![MS Teams Custom Chat Help Page](img/teams_custom_chat_help.JPG)