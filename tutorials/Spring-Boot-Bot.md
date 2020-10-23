# Building A Bot in Java with Spring Boot

This guide is based on a presentation I've written, creating a
fully-functional Java bot, using the [Symphony API Spring Boot Starter ](../symphony-api-spring-boot-starter/README.md) component.  

By following this, you will end up with a fully functioning bot which
will "echo back" what you tell it in a given room.

Apart from Step 1, which is dependent on your Symphony Admin team, this should take
around 1 hour to complete, if you don't run into any other problems.

**NB: **: You can view [all of the code for this project here](../demos/demo-bot).  In fact, some of the code is a later version there, 
so it's better than relying on the screenshots here.

## 1. Create a New Bot Account

This guide assumes you have a working bot account, and a log-in to a symphony pod, so you can talk with your bot.

Talk to your Symphony Support team about getting these set up.

## 2. Create a Spring Boot Project

![New Boot Project](bot/media/image1.png)

-   The best way to do this is via the website, `https://start.spring.io`

-   As you can see, I've created a maven project, with developer tools.

-   When you click download, it will deliver a zip file which you can
    unpack in your filesystem

## 3. Import Into Your IDE

![Importing into eclipse](bot/media/image2.png)

I am going to use eclipse, as shown above and below.  If you are using
a different IDE, substitute your step here.

Here is my project in eclipse:

![In eclipse](bot/media/image3.png)

-   If I run the project now, I should see Spring start, and then shut down
    (as we haven't actually added any code).

## 4. Add The Symphony-Api-Spring-Boot-Starter Dependency

In pom.xml add this dependency (please check artifactory for later
versions)

![Adding the starter](bot/media/image4.png)

```
<dependency>
  <groupId>com.github.deutschebank.symphony</groupId>
  <artifactId>symphony-api-spring-boot-starter</artifactId>
  <version>--check below--</version>
  <scope>compile</scope>
</dependency>
```
Latest version: 

![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)


5.  Add Jax-RS Dependency
-------------------------

JAX-RS provides the code to call REST endpoints.  You can either add Jersey, or CXF.  

Both are detailed on this page under Step 2: [Choose a JAX-RS Implementation.](../symphony-api-spring-boot-starter/README.md#Step-2-Choose-JAX-RS-Implementation)


#### CXF

Include this dependency:

```
<dependency>
  <groupId>org.apache.cxf</groupId>
  <artifactId>cxf-rt-rs-client</artifactId>
  <version>...</version>
  <scope>compile</scope>
</dependency>
```

Include this:

```
<dependency>
  <groupId>org.glassfish.jersey.core</groupId>
  <artifactId>jersey-common</artifactId>
</dependency>

<dependency>
  <groupId>org.glassfish.jersey.core</groupId>
  <artifactId>jersey-client</artifactId>
</dependency>

<dependency>
  <groupId>org.glassfish.jersey.inject</groupId>
  <artifactId>jersey-hk2</artifactId>
</dependency>

<dependency>
  <groupId>org.glassfish.jersey.connectors</groupId>
  <artifactId>jersey-apache-connector</artifactId>
</dependency>

<dependency>
  <groupId>org.glassfish.jersey.media</groupId>
  <artifactId>jersey-media-multipart</artifactId>
</dependency>
```

You can use the Jersey BOM to avoid specifying version numbers like so:

```
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.glassfish.jersey</groupId>
      <artifactId>jersey-bom</artifactId>
      <version>-- see project shields --</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

## 6. Rebuild your Project

```
mvn eclipse:eclipse -DdownloadSources
```

-   Then, refresh in eclipse.

## 7.  Autowiring! 

If the dependencies loaded correctly, you should now be able to auto-wire some beans:

![Spring Auto-wiring](bot/media/image5.png)

- Here, I have auto-wired the `messagesApi`, `datafeedApi` and the `symphonyIdentity`.  
- The apis are provided by the [symphony-java-client-bindings](../bindings/README.md) project
- The ID is a bean exposed by the [Symphony API Spring Boot Starter](../symphony-api-spring-boot-starter/README.md) containing the id of your bot.
- The ID is a `SymphonyIdentity` object, defined by the [symphony-java-client-identity](../identity/README.md) project.

What happens if we run now?

![Disaster](bot/media/image6.png)

Disaster!  We now need to configure our application..

## 8. Bot Configuration

If you have completed step 1, you should have the bot common name, email
address and private key (and possibly also a certificate).

Create an **application.yml** file and add them like so:

```
symphony:
  bot:
    identity:
      email: <your bot's email address>
      commonName: <your bot's name>
      privateKey: 
        -----BEGIN RSA PRIVATE KEY-----
        MIIJKQ...
        ...neY
        -----END RSA PRIVATE KEY-----
      certificates:
      - |
        -----BEGIN CERTIFICATE-----
        MIIG...
        ...IIU6A==
        -----END CERTIFICATE-----
      - |
        -----BEGIN CERTIFICATE-----
        MIIFv...
        ...sN0=
        -----END CERTIFICATE-----
```
 
### Some Notes

-   I am using YAML\'s block format here to allow me to paste the keys
    and certificates in.  There are many ways to format this in YAML,
    you can enclose in a string on a single line if you want to.

-   I am providing the entire certificate chain from the certificate
    bundle provided back in the PKI process, but I actually think you
    can get away with just the first one.

-   You can use a p12 file if you want to.  Review the docs in [Symphony API Spring
    Boot Starter](../symphony-api-spring-boot-starter/README.md) for details.

-   Column formatting is very important in yaml!  Make sure everything
    lines up.

## 9. Pod Configuration

Add the pod information to you **application.yml** too:

```
symphony:
  apis:
  - id: dbtest\
    pod:
      url: https://<your pod>.symphony.com/pod
      proxy:
        host: <yourproxy, optional>
    sessionauth:
      url: https://<your pod>-api.symphony.com:443/sessionauth
      proxy:
        host: <yourproxy, optional>
    keyauth: 
       url: https://<your key manager>/keyauth
    agent:
       url: https://<your agent>/agent
    relay:
       url:  https://<your pod>.symphony.com/relay
    login: 
       url:  https://<your pod>.symphony.com/login
```


### Some Notes

-   This configuration is also described in [Symphony API Spring
    Boot Starter](../symphony-api-spring-boot-starter/README.md)

-   You can have separate proxy entries for each of the main
    endpoints, **pod, sessionauth, keyauth, relay, login** and **agent.**

## 10. Object Mapper 

Out-of-the-box, Spring Boot contains Jackson for doing marshalling to
JSON, however without `spring-boot-starter-web`, it doesn\'t expose a bean
for this.  Since we\'re going to need to marshal to JSON for calling
symphony APIs, we need to provide one:

```
@Configuration
public class Config {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
```
## 11. Run It

![Running the code](bot/media/image7.png)

At this point, you should be able to start your app, and the autowiring
should complete successfully.

- You'll get some information on the screen about the bot you\'ve
autowired.
- Nothing else will happen, as we've not written much in the way of functionality.

## 12.  Create A Chat Room

You'll need a room for you and the bot to talk in.

- Create a chat room and add the bot

![Chat room](bot/media/image8.png)

You'll need to know the chat room stream ID, which you can find by
clicking the time next to your name:

![Stream Id](bot/media/image9.png)

The stream ID (or conversation ID) appears on the right.

```
StQv5mK1u+06afIwrhtN1n///pPiNy8tdA==
```

This should be wired into the `application.yml` file:

```
#set the room for the bot to post in.  Bot (and you) should be members of this room
room: StQv5mK1u+06afIwrhtN1n///pPiNy8tdA==
```

**NB:** For weird Symphony reasons about URL encoding, the `+`s get turned into `-`s, and the `/`s get turned into `_`s.  It's documented [here](https://stackoverflow.com/questions/47874229/symphony-embedded-chat-module-not-working).  This conversion is now automatically handled by `StreamsIDHelp` for you when you use the starter.

## 13. Send A Message

We're going to add an event listener, so that the bot will send an
event when the room is created.


Add this code into your `DemoApplication` class:

```
	@Value("${room}")
	String streamId;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
	    System.out.println("hello world, I have just started up");
	    messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>sometestmessage</messageML>", null, null, null, null, null);
```

When you start the application now, you'll see the test message in
Symphony:

![Test Message](bot/media/image11.png)

### Some Notes

-   This is the method we are calling from
    Symphony: [Create Message v4](https://developers.symphony.com/restapi/reference#create-message-v4)

-   [Symphony-Java-Client-Bindings](../bindings/README.md)
    will handle **sessionTokens** and **keyManagerTokens**, so you can
    leave those parameters blank.

## 14. Listening For Messages

We're going to add code to *listen *to messages sent to the bot.

to do this, add the following in your **doSomethingAfterStartup**
method:

```
 		// create a datafeed
	    Datafeed df = datafeedApi.v4DatafeedCreatePost(null, null);
	    
	    Streams.createWorker(() -> datafeedApi.v4DatafeedIdReadGet(df.getId(), null, null, 50), e -> LOG.error("Problem with Symphony!", e))
	    	.stream()
	    	.filter(e -> e.getType().equals("MESSAGESENT"))
	    	.map(e -> e.getPayload().getMessageSent().getMessage())
	    	.filter(m -> !m.getUser().getEmail().equals(id.getEmail()))
	    	.forEach(m -> messagesApi.v4StreamSidMessageCreatePost(null, streamId, m.getMessage(), null, null, null, null, null));

```

When you run now, you should see your messages echoed back to you:

![Echoes](bot/media/image12.png)

### Some Notes

-   Streams is a class from
    [Symphony-Java-Client-Bindings](../bindings/README.md). 
    You can read about it there.  It allows you to create a stream from
    symphony REST calls, handling tokens and so on. The first parameter
    is the call to get some events from the stream, the second parameter
    is a function to handle exceptions.  You do what you want here.

-   It returns a stream.  We are filtering this so that we only get the
    "MESSAGESENT" event, and then pulling the message out.

-   We ignore messages from ourselves (otherwise, an infinite loop
    ensues)

-   At the end, we write the message back as it came in. 

## For Bonus Points

-   Add Spring-Boot-Starter-Web to your application and view the /health
    and /metrics endpoints as you go.  This will show you the state of
    the bot, the agent and the pod, and details of the api calls you
    have made.
