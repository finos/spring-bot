# Troubleshooting

Here are clues to solving common issues:

 - `PKIX Pathbuilding Failed` or `certificate unknown`: Usually indicates a problem with your trust store.  That is, your bot's server doesn't recognise the certificates provided by the symphony pod, key manager or agent servers.  
 
 - `NotFoundException` (404):  Could be that your bot is configured for certificate authentication, but you're only providing a private key, or vice-versa.
 
 - `bad_certificate` / `certificate_unknown` :  This means your client certificate for the bot is somehow ill-formed, expired, doesn't have a matching account on the symphony side, or doesn't have it's root/intermediate cert loaded in Symphony.
 
 - **Long Pause Connecting, then failure**:  Usually a proxy issue.
 
 - `UnrecognizedPropertyException: Unrecognized field "xxx"`: A classpath issue, probably you have `jersey-media-json-jackson` included somewhere, don't use that.
 
 - `No message body reader has been found for class ...` : Check your stream Id doesn't have spaces/new lines in it.
 
 - `The bean 'templateEngine',... could not be registered. `: This happens when you have included the Teams Spring Boot Starter but have disabled it with `spring.profiles.active`.  This is because the Teams starter brings in Thymeleaf, as does the Symphony starter, and they collide.  When Teams Spring Boot Starter is enabled, it renames the Teams Thymeleaf `templateEngine` to avoid this happening.  Don't include the Teams Spring Boot Starter unless you're going to enable it.