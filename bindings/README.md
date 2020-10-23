[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=com.github.deutschebank.symphony)[![Javadocs](https://img.shields.io/badge/Javadocs-symphony--java--client--bindings-green)](https://javadoc.io/doc/com.github.deutschebank.symphony/symphony-java-client-bindings/latest/index.html)


## What This Is

This provides a JAX-RS standard, **Type-Safe** means of interacting with [Symphony](https://symphony.com) pods via REST apis.

`symphony-java-client-bindings` is a set of swagger-generated domain objects and interfaces that provide a Java API for interacting with Symphony's REST endpoints. 

- [Symphony REST Endpoint Documentation](https://rest-api.symphony.com/reference)
- [Symphony REST Endpoint Swagger Definitions](https://github.com/symphonyoss/symphony-api-spec)
- [Javadocs](https://javadoc.io/doc/com.github.deutschebank.symphony/symphony-java-client-bindings/latest/index.html)

Since these interfaces are mainly generated, there isn't a lot of code checked in here.  

## Maven Central Coordinates

```
<dependency>
	<groupId>com.github.deutschebank.symphony</groupId>
	<artifactId>symphony-java-client-bindings</artifactId>
	<version>-- see above for version --</version>
	<scope>compile</scope>
</dependency>
```

## Design Decisions

There are alternatives to using this package, such as:

 - [Symphony API Client Java](https://github.com/SymphonyPlatformSolutions/symphony-api-client-java) - Authored by Symphony.  Quite new.
 - [Symphony Java Client](https://github.com/symphonyoss/symphony-java-client) - Older, unsupported.
 
So why do we need a new one?  Some reasons:

- **Configuration**: These libraries all try to specify configuration formats.  However, in many complex corporate environments involving multiple proxies, firewalls and DMZs, the configuration is often too complex to express in the JSON files that they require.  This package leaves configuration up to JAX-RS apis.
- **Code Generation**: The above libraries all rely on hand-maintained code to marshall/unmarshall REST, and the code is checked in.  Here it is swagger generated and not human-maintained (this project only contributes 8 hand-written classes).
- **Interface Consistency**.  Because the interfaces are generated this has the advantage that they _exactly_ match the REST endpoints [documented by Symphony](https://rest-api.symphony.com/reference).
- **Apache CXF** or **Jersey**: Since the apis use Jax-RS specification, you can use any compatible implementation.  Tests exist for these two popular offerings.  Other JAX-RS implementations (e.g. RestEASY) are available, but you may have to work around the limitations of multipart support in the standard (see below).
 
## Instantiating APIs

The simplest way to instantiate any Symphony API is via the `ApiBuilder` interface and it's subclasses, `JerseyApiBuilder` and `CXFApiBuilder` like so:

```java
MessagesApi messagesApi = apiBuilder.getApi(MessagesApi.class);
```

You can instantiate one of the builders in the following way:

```java
ApiBuilder apiBuilder = new JerseyApiBuilder(<pod endpoint url>);
ApiBuilder apiBuilder = new CXFApiBuilder(<pod endpoint url>);
```

The apis themselves are held in four packages, `com.symphony.api.pod`, `com.symphony.api.agent`,  `com.symphony.api.authenticator` and `com.symphony.api.login`.  

[View Javadoc APIs Here](https://javadoc.io/doc/com.github.deutschebank.symphony/symphony-java-client-bindings).

If you are trying to find the right class given the Symphony endpoint, the search box on this page is very helpful.

## Choosing An Implementation

This library doesn't transitively include either Apache CXF or Jersey.  It is up to you which JAX-RS implementation you use, and you will need to import the classes to support this into you project like so (maven example included):

### Apache CXF

```xml
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-rs-client</artifactId>
			<version>3.3.1 (or later)</version>
			<scope>compile</scope>
		</dependency>
```

### Jersey


```xml
		<dependency>
			<groupId>org.glassfish.jersey.core</groupId>
			<artifactId>jersey-client</artifactId>
			<version>2.28</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.glassfish.jersey.inject</groupId>
			<artifactId>jersey-hk2</artifactId>
			<version>2.28</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
			<groupId>org.glassfish.jersey.connectors</groupId>
			<artifactId>jersey-apache-connector</artifactId>
			<version>2.28</version>
			<scope>compile</scope>
		</dependency>
		<dependency>
		    <groupId>org.glassfish.jersey.media</groupId>
		    <artifactId>jersey-media-multipart</artifactId>
		    <version>2.28</version>
			<scope>compile</scope>
		</dependency>
```

**NB:**  Do not include (either directly or transitively) `jersey-media-json-jackson`.  This screws up forward-compatibility for message reading, and in any case is not recommended anymore by the Jersey/Jackson teams.

## Proxies

Both Apache CXF and Jersey support proxies.  You can either configure your JAX-RS `Client` yourself, or set the proxy inside the `ApiBuilder` implementation by calling `setProxy`.

## Authentication

Both [Certificate Based Authentication](https://rest-api.symphony.com/reference#session-authenticate) and [RSA Based Authentication](https://developers.symphony.com/restapi/reference#rsa-session-authenticate) are tested.

### Certificate-Based Authentication

You can get a Symphony Session Token for a bot with the `sessionauth` endpoint as shown below.  Note you will need a Java `KeyManager` implementation containing the certificate chain and private key for the bot identity for this to work.  Jersey code is shown here but it will work the same if you swap `JerseyApiBuilder` for `CXFApiBuilder`.

```java
	JerseyApiBuilder b = new JerseyApiBuilder("https://<your pod>-api.symphony.com:443/sessionauth", <key manager>);	
	b.setProxyDetails(...);
	Token t = b.getApi(AuthenticationApi.class).v1AuthenticatePost()
```

You can get a Symphony Key Manager token in the same way:

```java
	JerseyApiBuilder b = new JerseyApiBuilder("https://<your key manager host>:8444/keyauth", <key manager>);	
	Token t = b.getApi(AuthenticationApi.class).v1AuthenticatePost()
```

### RSA Authentication

RSA Authentication is done against the pod, using JWT Tokens.  JWTTokens can be generated from a private key via `JWTToken` class which is supplied to help you.  You can get a Symphony Session Token like so:

```java
	String jwt = JWTHelper.createSignedJwt(<bot service account id>, <expiry time>, <private key>);
	JerseyApiBuilder b = new JerseyApiBuilder("https://<your pod>.symphony.com/login");	
	b.setProxyDetails(...);
	AuthenticationApi sessionApi = s.getRSASessionAuthApi();  //nb.  com.symphony.api.login.AuthenticationApi
	AuthenticateRequest req = new AuthenticateRequest();
	req.setToken(jwt);
	Token done = sessionApi.pubkeyAuthenticatePost(req);
```

And a Key Manager token like so.  Note that instead of calling the key manager directly, you _call via the pod relay_:

```java
	String jwt = JWTHelper.createSignedJwt(<bot service account id>, <expiry time>, <private key>);
	JerseyApiBuilder b = new JerseyApiBuilder("https://<your pod>.symphony.com/relay");	
	b.setProxyDetails(...);
	AuthenticationApi sessionApi = b.getApi(AuthenticationApi.class);  // nb: com.symphony.api.login.AuthenticationApi
	AuthenticateRequest req = new AuthenticateRequest();
	req.setToken(jwt);
	Token done = sessionApi.pubkeyAuthenticatePost(req);
```

## Token Management (Posting Messages)

Most of the [REST Endpoints in the Symphony Spec](https://rest-api.symphony.com/reference) require either a key-manager token or a session token.  

It can be cumbersome to manage these parameters and pass them around, so the `TokenManager` class can take care of this.  It decorates the api instances to supply the tokens needed, and also to retry in the event of the tokens expiring.

You can instantiate and use a `TokenManager` like so:

```java
	TokenManager tm = new TokenManager(() -> sessionAuthApi.v1AuthenticatePost(), () -> keyAuthApi.v1AuthenticatePost()); 
	ConfigurableApiBuilder apiBuilder = ... // construct jersey or cxf api here
	apiBuilder.setWrappers(new ApiWrapper[] { tm });
```

This will automatically be used to decorate apis returned by the builder.  This means you can make the following call, for example:

```java
		MessagesApi messageAPi = apiBuilder.getApi(MessagesApi.class);

		messageAPi.v4StreamSidMessageCreatePost(null, "1+rc4tMrxupuRV5OA3f09X___pXvTHdQdA==",
				"<messageML>Trigger Listener.</messageML>", null, null, null, null, null);
```

Note that in this example, normally `v4StreamSidMessageCreatePost` would take both the key manager token and the session token, but since we are using the token manager, neither need to be supplied in the call.

## StreamHelp

One interesting design decision is that Symphony use Stream IDs such as `y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA==` to refer to their chat rooms.  However, at the same time, they also use these Stream IDs as path elements within their REST API.  This is a problem, as `=`, `+` and `/` are not allowed within path elements.  The class `StreamHelp` provides a utility method that converts from the unsafe to the safe form of the StreamID.

`StreamHelp` can be placed into the `apiBuilder` much like the `TokenManager`, e.g.:

```java
  apiBuilder.setWrappers(new ApiWrapper[] { tm, new StreamHelp() });
```

In this way, all REST calls will automatically have their stream IDs made safe.
				
## Attachments

Sadly, JAX-RS has not standardized multipart REST calls (yet - this will come).  In the meantime, we are forced to supply  implementations for Jersey and CXF to make these work, as these are used in the Symphony APIs `DlpPoliciesAndDictionaryManagementApi` and `MessagesApi`.  (The latter is commonly used, as you need it for posting in Symphony.

 - `ContentDispositionMultipartProvider`:  this allows Apache CXF to correctly call these APIs and send multipart messages in a format Symphony can understand.
 - `MultipartWebResourceFactory`: This enhances Jersey's API implementation to support multipart messages.
 
These classes are automatically used when you instantiate APIs via `JerseyApiBuilder` or `CXFApiBuilder`.

## Streams (Consuming Events)

The usual workflow of a bot is to listen to messages and respond to them.  Ideally, this means you want to be programming via callbacks.  	`symphony-client-bindings-java` provides a helper class, `Streams`, so that you can consume events from Symphony in a stream fashion.  

Here is an example:

```java
   JerseyApiBuilder apiBuilder = new JerseyApiBuilder("https://<agent host>:8444/agent");     (1)
   apiBuilder.setTokenManager(...);
   DatafeedApi dfApi = apiBuilder.getAgentApi(DatafeedApi.class);    (2)  
   Datafeed df = dfApi.v4DatafeedCreatePost(null, null);             (3)

   Streams.createWorker(
      () -> dfApi.v4DatafeedIdReadGet(df.getId(), null, null, 100),  (4)
      (e) -> e.printStackTrace())                                    (5) 
         .stream()                                                   (6)
         .filter(e -> e.getType().equals("MESSAGESENT"))             (7)
         .map(e -> e.getPayload().getMessageSent().getMessage())                
         .forEach(m -> onRoomMessage(m));                            (8)                         

```

1.  This constructs the apiBuilder to return agent APIs.
2.  This gives back the [DatafeedAPI](https://rest-api.symphony.com/reference#create-messagesevents-stream-v4)
3.  Returns the Datafeed.  This is an ID you use to subscribe to messages in Symphony.
4.  Here, we are constructing the Stream Worker.  It takes 2 parameters.  The first is the api call we are going to make to get events.  Here, we are reading [from the datafeed](https://rest-api.symphony.com/reference#read-messagesevents-stream-v4).  Note that we are using the Token Manager described above so that we don't need to worry about session/key manager tokens in our code.
5.  Here, you handle any errors that occur.  In this example, we are simply printing them to the output.  (Don't do this in production).
6.  The `Streams.Worker` instance has a `stream()` method, returning a regular Java8 stream.  In this case, it will be returning Symphony Events.
7.  Here, we are filtering to just find "MESSAGESENT" events.  [There are other events](https://rest-api.symphony.com/reference#read-messagesevents-stream-v4).
8.  We extract the message from each event.
9.  Then, we perform the callback to process the message.

This workflow is perhaps more complex than using other Symphony clients, but it has the following advantages:

- No Threads are created by the client directly (Jersey/CXF will probably create some though).
- Error handling is explicit.  You get to decide what to do in the event of failures.
- Use of modern Java8 streams idiom.
- No opinion on which logging mechanism you are going to use for reporting errors.

## FAQ

#### Which Class Contains The Method I Need To Call?

Let's say you know the Symphony API you want to call.  How should you find the Java interface, and the method on it? Sadly, this is not very clear from the swagger definitions! 

You should be able to check the [Javadocs](https://javadoc.io/doc/com.github.deutschebank.symphony/symphony-java-client-bindings/latest/index.html) to find this out - but be prepared to use the search function a lot. 