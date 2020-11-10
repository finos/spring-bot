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

## 4. Add Toolkit Dependencies

In pom.xml add these dependencies (please check maven badge below for latest versions)

```
<dependency>
  <groupId>org.finos.symphony.toolkit</groupId>
  <artifactId>symphony-api-spring-boot-starter</artifactId>
  <version>--check below--</version>
  <scope>compile</scope>
</dependency>
<dependency>
  <groupId>org.finos.symphony.toolkit</groupId>
  <artifactId>shared-stream-spring-boot-starter</artifactId>
  <version>--check below--</version>
  <scope>compile</scope>
</dependency>
```
Latest version: 

![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)

The `symphony-api-spring-boot-starter` gives us auto-wiring of various Symphony API beans, and the 
`shared-stream-spring-boot-starter` handles the stream of events from Symphony, giving us error-handling
and, if we deploy in a cluster, leader election for the instances of the bot so that we don't get multiple instances handling the 
same messages.


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

## 6. Bot Configuration

In order to complete this step, you'll need to have a bot created for you in the Symphony admin console.  You'll need the bot common name, email
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

## 7. Pod Configuration

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
    
## 8.  Room Configuration

We're going to create a room that the bot posts in when it starts up.

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

## 9. Object Mapper 

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

## 10.  Functionality! 

Let's modify the `DemoApplication` class so that it:

 - Writes a greeting into the room (configured above) as the bot starts.
 - Echoes back anything written to the bot.
 

### Handle the greeting:

```
@SpringBootApplication
@Configuration
public class DemoApplication {
    
  @Autowired
  MessagesApi messagesApi;                                              (1)
  
  @Value("${room}")
  String streamId;                                                      (2)

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void doSomethingAfterStartup() {                               (3)
      System.out.println("hello world, I have just started up");
      messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>sometestmessage</messageML>", null, null, null, null, null);
  }
  
  // rest of class below
```

Notes:

- At (1) we are auto-wiring one of the Symphony REST APIs.  - The apis are provided by the [symphony-java-client-bindings](../bindings/README.md) project.
- At (2) we are getting the stream ID of the room we configured
- (3) is automatically called when the application starts, and it writes the message to Symphony using the API we auto-wired.


![Test Message](bot/media/image11.png)

### Handle the Echo  

To handle the echo, we're going to implement a `StreamEventConsumer` bean.  `shared-streams-spring-boot-starter` requires that you have one of these defined in order that it will work.

```
@SpringBootApplication
@Configuration
public class DemoApplication {


  ... // as before
  
  @Autowired
  SymphonyIdentity id;                                                                      (1)
  
  @Bean
  public StreamEventConsumer consumer() {
    return event -> {
      V4MessageSent ms = event.getPayload().getMessageSent();                               (2)
      if ((ms != null) && (!ms.getMessage().getUser().getEmail().equals(id.getEmail()))) {  (3)
        
        // echo the message back
        messagesApi.v4StreamSidMessageCreatePost(null,                                      (4)
          ms.getMessage().getStream().getStreamId(),    // reply to the room the message came from
          ms.getMessage().getMessage(),                 // reply with original content
        null, null, null, null, null);  
        
      }
      
    };
  }
}

```
Notes:

- At (1), the bot's ID is autowired.  This is a bean exposed by the [Symphony API Spring Boot Starter](../symphony-api-spring-boot-starter/README.md) containing the id of your bot.
- The ID is a `SymphonyIdentity` object, defined by the [symphony-java-client-identity](../identity/README.md) project.
- At (2) we are telling our StreamEventConsumer to only care about `V4MessageSent` events.
- At (3) we check that the sender is not the bot itself (otherwise you'll get infinite echoing of messages)
- At (4), we are calling the same method as we used on startup.  This is the method we are calling from Symphony: [Create Message v4](https://developers.symphony.com/restapi/reference#create-message-v4)
- [Symphony-Java-Client-Bindings](../bindings/README.md) will handle **sessionTokens** and **keyManagerTokens**, so you can leave those parameters blank.

## 11. Run It

![Running the code](bot/media/image7.png)

At this point, you should be able to start your app, and the autowiring
should complete successfully.

When you run now, you should see your messages echoed back to you:

![Echoes](bot/media/image12.png)

## For Bonus Points

-   Add Spring-Boot-Starter-Web to your application and view the /health
    and /metrics endpoints as you go.  This will show you the state of
    the bot, the agent and the pod, and details of the api calls you
    have made.
