# Troubleshooting

Here are clues to solving common issues:

 - `PKIX Pathbuilding Failed`: Usually indicates a problem with your trust store.  That is, your server doesn't recognise the certificates provided by the symphony pod, key manager or agent servers.  See [Trust Store Configuration](symphony-api-spring-boot-starter/README.md#step-5-trust-store-configuration-optional)
 
 - `NotFoundException` (404):  Could be that your bot is configured for certificate authentication, but you're only providing a private key, or vice-versa.
 
 - '':  This means your client certificate for the bot is somehow ill-formed, doesn't have a matching account on the symphony side, or doesn't have it's root/intermediate cert loaded in Symphony.
 
 - **Long Pause Connecting, then failure**:  Usually a proxy issue.
 
 - `UnrecognizedPropertyException: Unrecognized field "xxx"`: A classpath issue, probably you have `jersey-media-json-jackson` included somewhere, don't use that.
 
 - `No message body reader has been found for class ...` : Check your stream Id doesn't have spaces/new lines in it.
 
 - `No qualifying bean of type 'com.symphony.api.id.SymphonyIdentity'`: This means you have not correctly either created a test identity (see [Test Identities](identity/README.md)) or you haven't defined `symphony.bot.identity` in your `application.yml` (see [symphony-api-spring-boot-starter](symphony-api-spring-boot-starter/README.md))
