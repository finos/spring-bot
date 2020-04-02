# Troubleshooting

Here are clues to solving common issues:

 - `PKIX Pathbuilding Failed`:  This means your client certificate for the bot is somehow ill-formed, doesn't have a matching account on the symphony side, or doesn't have it's root/intermediate cert loaded in Symphony.
 
 - **Long Pause Connecting**:  Usually a proxy issue.
 
 - `UnrecognizedPropertyException: Unrecognized field "xxx"`: A classpath issue, probably you have `jersey-media-json-jackson` included somewhere, don't use that.
 
 - `No message body reader has been found for class ...` : Check your stream Id doesn't have spaces/new lines in it.