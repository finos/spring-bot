[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=org.finos.symphony.toolkit)

# What This Is

`entity-json` is a utility that helps you serialize/deserialize JSON in a format suitable for the JSON `data` payload of a Symphony Message, using [Jackson](https://github.com/FasterXML/jackson).

This data payload is used in a variety of ways in Symphony, such as:

 - Giving details of hashtags/cashtags/mentions
 - Embedded objects which will be rendered in the Symphony client.
 - Template information, which will be converted in the Symphony Agent using FreeMarker templates.

## Hashtag Example

Let's say I type the message `#hello` following into Symphony:

![Hello](images/hello.png)

What does this look like in terms of data being sent about?

The `MessageML` will look like this:

```html
 <div data-format="PresentationML" data-version="2.0" class="wysiwyg">
    <p>
        <span class="entity" data-entity-id="0">#hello</span> 
    </p>
 </div>
```

As you can see, there is an attribute `data-entity-id`, which is referencing the value `0`.  This is contained in the JSON `data` payload for this message:

```json
{
  "0" : {
    "type" : "org.symphonyoss.taxonomy",
    "version" : "1.0",
    "id" :[ 
      {
        "type"  : "org.symphonyoss.taxonomy.hashtag",
        "value" : "hello"
      }
    ]
  }
}
```

## Security Example

You can send much more complex objects with this same JSON format.  Symphony describes the JSON format as [Structured Objects](https://developers.symphony.com/symphony-developer/docs/objects).  On that page is the example of sending some securities:

```html
<messageML>
  Hello <mention email="user@music.org" />. Here is an important message with an
  <div class="entity" data-entity-id="object001" /> 
  included.
</messageML>
```

Then it refers to an object called `object001` within the JSON payload, which might look something like this:

```
{
    "object001":
    {
        "type":     "org.symphonyoss.fin.thing",
        "version":  "1.0",
        "data": "somedata"
        [
            {
                "type":     "org.symphonyoss.fin.security.id.ticker",
                "value":    "IBM"
            },
            {
                "type":     "org.symphonyoss.fin.security.id.isin",
                "value":    "US0378331005"
            },
            {
                "type":     "org.symphonyoss.fin.security.id.cusip",
                "value":    "037833100"
            }
        ]
    }
}
```

These objects may require a special [Symphony App](https://symphony.com/resource/app-directory/) to be installed on the client, in order that they are correctly rendered for the user.  

That is, the app will receive a Javascript callback in order to render some HTML for the Entities.

## Maven Central Coordinates

```
<dependency>
    <groupId>org.finos.symphony.toolkit</groupId>
    <artifactId>entity-json</artifactId>
    <version>-- see above for version --</version>
    <scope>compile</scope>
</dependency>
```

## Configuration

Essentially, this package is just providing configuration for Jackson.  You can do this as follows:

```
    ObjectMapper om = ObjectMapperFactory.initialize(<version space details>);
```

This is essentially configuring Jackson to use Symphony's scheme for setting the `type` and `version` details for each object, instead of Jackson's built-in use of a `@class` attribute. 

## Type Name Conversion

`type` is converted into a Java FQCN by upper-casing the first character after the last period. e.g.


|Symphony Structured Object `type`|Java Class Name                      |
|-----------------------------------|-------------------------------------|
|`org.symphonyoss.taxonomy`         |`org.symphonyoss.Taxonomy`|
|`org.symphonyoss.fin.security.id.ticker`|`org.symphonyoss.fin.security.id.Ticker`|


You can see some examples of these classes in `src/test/java`.

## Version Spaces

In order that Jackson knows which version to give each class, we pass `ObjectMapperFactory` some `VersionSpace` objects in the constructor:

```
ObjectMapper om = ObjectMapperFactory.initialize(
     new VersionSpace("com.myproject.myjsonobjects", "1.0"));
```

`VersionSpace` takes 2+ arguments:
 - Package Prefix
 - Write Version
 - 0+ Read version matchers
 
### Default Version Spaces

To be able to automatically deserialize `Mention`, `UserId`, `Hashtag` etc, you can create your list of `VersionSpace`s like this:

```java
	VersionSpace[] vs = ObjectMapperFactory.extendedSymphonyVersionSpace(
				// your own version spaces
		);
	om = ObjectMapperFactory.initialize(vs);
```

By using `extendedSymphonyVersionSpace` it will add the `VersionSpace`s of the symphony built-ins to your own defintions.


### When Writing

- When writing JSON, `VersionSpace`s are applied in priority order, so if multiple package-prefixes match, then the write-version of the earliest match is used.
- If you supply an empty-string `""` write version, no `version` attribute is added to the JSON (like the `org.symphonyoss.fin.security.id.ticker` example, above)

### Reading 

If you want to support back or forward compatibility, you can supply some read versions.  e.g.

```
// * is supported as 'any number'
VersionSpace vs = new VersionSpace("com.myproject.myobjects1.", "2.0", "1.*");
...

// this is a basic regex match.
VersionSpace vs2 = new VersionSpace("com.myproject.myobjects2.", "3.0", "1.[0-4]", "2.2");

// you could do this, but it would be insecure - the user could attempt to load any class via json
VersionSpace vs3 = new VersionSpace("", "1.0", "*");
```

## Examples

Please take a look at the `/src/test/java` code to see all of this working.  





