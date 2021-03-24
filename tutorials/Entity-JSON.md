
# Working With JSON In Symphony

A message in Symphony has a variety of different "payloads":

1. It has a `message` payload, which is converted into HTML to display in my Symphony client.
2. It has an `attachments` payload, through which I can send images, or other attachments into a chat, 
3. and it has a `data` payload, through which JSON can be sent.  
 
You can see all of these in the [MessagesApi.v4StreamSidMessageCreatePost]() signature.

Without special work, this JSON payload is generally invisible to the user, however it's useful for various things:

- **bot-to-bot communication**: As well as sending a human-readable message, you can also send data which other bots might read and process.

- **data entities**: within the `MessageML` payload, you can refer to elements from the `data` payload, and using a [Symphony App]() have a client-side renderer that turns these into HTML when it sees them.

- **cashtags/hashtags and mentions**: whenever you place these in a message, the data payload will be updated to contain details of those tags.  Specifically, you can grab the Symphony `userid` from the data payload.

## Structured Objects Format

To bring some order to all of these different `data` payload use-cases, Symphony employs a special format for the JSON, called **Structured Objects**.

It looks like this:

```

{
  "thing1" : {
    "type" : "<some type identifier>",
    "version" : "<optional version number>",
    ... 
    // all the various properties of thing1
  },
  "thing2" : {
    "type" : "<some type identifier>",
    "version" : "<optional version number>",
    ... 
    // all the various properties of thing2
  }
} 
```

... pretty simple eh?  Just a top-level JSON map, where each _value_ in the map is also a map, containing two properties: _type_ and _version_.  

Let's look at a real-life example.  If I send this message to my bot:

![First Message](json/message.png)

It'll get content in both the `message` payload and the `data` payload:

### Message Payload

```html
<div data-format="PresentationML" data-version="2.0" class="wysiwyg">
  <p>
    <span class="entity" data-entity-id="0">@Rob Moffat</span>
    is late for work
  </p>
</div>
```

### Data Payload

```
{
  "0": {
    "type": "com.symphony.user.mention",
    "id" : [
      {
        "type: "com.symphony.user.userId",
        "value": "38764327324234"
      }
    ]
  }
}
```

The main thing to spot here is that the `data-entity-id` in the `message` payload matches up to the `"0"` key in the `data` payload.  

Also of note is how (for some reason) the contents of the `mention` json seems to contain a list of ids, although in this case there is only a single mention.

Finally, you can see that the `data` and the `message` have different _contents_.  If I want to know the `userId`, I _have_ to parse the data.

## Some Code

With the [Symphony Entity JSON](../libs/entity-json/README.md) project, you can easily parse this JSON into Java Objects.   The project contains a Jackson module which understands Structured Objects format.

In addition, it contains classes such as `Mention` and `UserID` to deserialize hashtags, cashtags and mentions.  So, you can write this:

```java
  ObjectMapper om = ObjectMapperFactory.initialize(ObjectMapperFactory.basicSymphonyVersionSpace());
  EntityJson ej = om.readValue("<some json>", EntityJson.class);
  Mention m = (Mention) ej.get("0");
  m.getId().get(0).getValue());	// returns "38764327324234"
```

Likewise, you can serialize java objects _back_ to JSON, to send back to the client in the same way.  

## Customization And Versions

You may have noticed that we've not covered the `version` attribute of the Structured Objects specification.  

It's possible to configure for each class you want to serialize:

 - The version number that will be _written_ when serialization occurs.
 - The version range that is allowed when deserialization occurs.
 
This is all described further in the [Readme](../entity-json/README.md).

## Conclusion

As with the rest of the libraries in this project, hopefully this will provide an easier way to consume Symphony code within your Java project.  Not everyone will need this, if you do, it will save many hours effort parsing and encoding your own JSON payloads.

Good luck bot-building!