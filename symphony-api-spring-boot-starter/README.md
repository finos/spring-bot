[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=org.finos.symphony.toolkit)

# Spring Boot Starter for Symphony Api / Bot Creation

This module provides Spring-Boot Starter for Symphony endpoints.  It is expected that you'll want to define details like your agent url and keymanager url in Spring, and have this "just work" with Symphony APIs.  This package provides for that, using `symphony-java-client-bindings` definitions of Symphony APIs to call.

## Use Case 1:   A Single Bot Using Symphony APIs

If you are writing a bot, the chances are that the bot will communicate with a single Agent, KeyManager and Pod.  By default, the starter will allow you to autowire Symphony APIs into your spring project.

### Step 1:  Add The Starter

Add the starter to your `pom.xml`:s

```
<dependency>
  <groupId>org.finos.symphony.toolkit</groupId>
  <artifactId>symphony-api-spring-boot-starter</artifactId>
  <version> -- see above -- </version>
</dependency>
```

### Step 2: Choose JAX-RS Implementation

Out-of-the-box, this project includes support for Apache CXF and Jersey.  You should include one of these in your project.

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

#### Jersey

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
				<version>...</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
```

### Step 3: Pod Configuration


You should some configuration like the following to your `application.yml`:

```
symphony:
  apis:
    - id: 4543; #some unique pod identifier, optional if you only configure ones
      pod:
        url: https://<your pod>.symphony.com/pod
        proxy:
          host: <your proxy host>
          user: <optional>
          password: <optional>
          port: <default 8080>
      sessionauth: 
        url: https://<your pod>-api.symphony.com:443/sessionauth
        proxy:
          ...
      keyauth: 
        url: https://<your key manager>:8444/keyauth
      agent:
        url: https://<your agent>:8444/agent
```

If you configure multiple pods, then by default, spring will autowire connections to the first pod in the list.  You can override this behaviour by setting the id of the pod in the property `symphony.bot.pod-to-use`.  This is helpful for switching between pods in test/prod environments, for example.

#### Proxies

As shown above, you can configure a single proxy for a single endpoint using the `proxy` attribute.  However, it's also possible to specify _multiple_ proxies, and have the starter choose one that works.  For example:

```
symphony:
  apis:
    - id: 4543; #some unique pod identifier, optional if you only configure ones
      pod:
        url: https://<your pod>.symphony.com/pod
        test-url: https://example.com
        proxies:
          - host: proxy1.com
            port: 8080
          - host: proxy2.com
            port: 8090
          - host: <none>
     ...
```

In this example, there are three possible proxies:  `proxy1.com`, `proxy2.com` and `<none>` (meaning no proxy).  Spring will try each proxy in turn, attempting to connect to the `test-url`.  If no `test-url` is provided, the `url` is used.  If no proxy connects successfully, a fatal error is created and the configuration stops.

**Note**:  All proxies are configured _per-endpoint_.  This may mean some repeated declarations, but this caters for some unusual Symphony topologies.

### Step 4: Identity Configuration

You can either reference a p12 file which contains your bot's identity, or use PEMs embedded in your spring `application.yml` or `application.properties`.

With a p12:

```
symphony.bot:
  identity:
    email: robski.moff@example.com
    commonName: bob
    password: abc123
    location: classpath:/example.p12
```

With pems:

```
symphony.bot:
  identity:
    email: robski.moff@example.com
    privateKey:  "-----BEGIN PRIVATE KEY-----\nMIIE...g3x6A==\n-----END PRIVATE KEY-----"
    commonName: bob
    certificates:
      - "-----BEGIN CERTIFICATE-----\nMIIC7z...xBXKt4=\n-----END CERTIFICATE-----" 

```

You can also encode a pem like this (this is supported by YAML):

```
symphony.bot:
  identity:
    certificates:
        - |
            -----BEGIN CERTIFICATE-----
            MIIDB...
            ...2e4=
            -----END CERTIFICATE-----
       
```

This will be loaded into a bean called `botIdentity` of class `SymphonyIdentity` within your application context.

You can control whether the bot is authenticated with RSA or certificates using `symphony.bot.auth-with=RSA|CERT`.

### Step 5: Trust Store Configuration (Optional)

If you want to add a trust store for the bot to use, configure it in the YAML like this:

```
symphony: 
  trust-store:
    location:  classpath:/some-file.crt
    type: PEMS / JKS / PKCS12
    password: optional
    
```

Where `location` is a spring resource location, starting with `classpath:` or `file:`, and `type` is the type of file, one of `PEMS`, `JKS` or `PKCS12`.
If you don't supply the trust store, Java's main one is used instead.

### Step 6: Dependency Injection

You can now build a really simple spring component like so:

```
@Component
public class SomeComponent {


   @Autowire
   MessagesApi api;  


	public void postMessage(String s) throws Exception {
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>"+s+"</messageML>" , null, null, null, null, null);
	}
}
```

**Note**: By default, session and key manager tokens are handled for you in the created beans, hence those parameters are `null`.  For more information on how this works, check out the `bindings` project.

## Use Case 2:  Multiple Identities / Multiple Pods

In the configuration, you can list the connection details to multiple pods, and define separate identities for bots on different pods.  You might want to do this when:
 - The same production bot instance is used in multiple Symphony pods (this is actually the norm for Symphony apps).
 - You want multiple different bot identities within the same pod.  

In order to get this to work, autowire the `ApiInstanceFactory` bean within your application.   This has the following method: 

```
public interface ApiInstanceFactory {

	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}
```

This allows you to get an `ApiInstance` for a given pod/id combination.  Note that the implementation of the `ApiInstanceFactory` is responsible for the `TokenManager`.  The `DefaultApiInstanceFactory` will provide automatic token management.

Once you have an `ApiInstance`, you can request an api for the pod or agent using that interface.  e.g.

```
	SystemApi systemApi = apiInstance.getAgentApi(SystemApi.class);
	api.v2HealthCheckGet(null, null);
```


## Health Endpoints

For either use-case 1 or 2, whenever Apis are requested, they become health-checked by spring, affecting the overall application health (provided by Spring Actuator's health endpoints, at `/health`by default.  An example might include this:s

```
UP {diskSpace=UP {total=75159826432, free=13292236800, threshold=10485760}, symphony-api-<hostname>-<podid>=UP {podConnectivity=true, keyManagerConnectivity=true, encryptDecryptSuccess=true, agentServiceUser=true, ceServiceUser=false, podConnectivityError=null, keyManagerConnectivityError=null, encryptDecryptError=null, podVersion=1.54.2, agentVersion=2.54.3, agentServiceUserError=null, ceServiceUserError=Ceservice authentication credentials missing or misconfigured}}
```
 
## Tutorial

 - [Read the Bot-Building Tutorial](tutorials/Spring-Boot-Bot.md)

