
# Symphony Maven Build Reporter

## Purpose

This is a plugin for maven that will allow your project to report build status into Symphony.  You can configure it to report into specific chat-rooms, or to specific users. 

![Example Output](example.png)

## Installation

Add the following to your `pom.xml` file in maven:

```
    <build>
       
        ...
      
        <extensions>
            <extension>
                <groupId>com.db.symphonyp</groupId>
                <artifactId>maven-symphony-reporter</artifactId>
                <version>1.0.24</version>
            </extension>
        </extensions> 
                 
    </build>
</project>
```

## Configuration

You need to configure 4 things in order for the build reporter to work:

1.  Recipients (either rooms or individuals to receive the notifications)
2.  Identity (the identity of the build reporter bot)
3.  Build Details (such as the CI URL)
4.  Symphony Endpoints (your pod details and proxy information).

### Recipients

These can be configured 3 ways:

#### 1. As A Property

A comma-separated list of email addresses or Symphony stream Ids, like so:

```

<properties>
    ...
    <symphony.reporter.recipients>
            robert.moffat@db.com, StQv5mK1u+06afIwrhtN1n///pPiNy8tdA==
    </symphony.reporter.recipients>
</properties>

```

#### 2. ciManagement Section

You can define the recipients in `<ciManagement>`.  You can create a `<notifier>` with type `symphony`:

```
    <ciManagement> 
        <system>teamcity</system>
        <url>https://some.path.to/your.build.html</url>
        <notifiers>
            <notifier>
                <type>symphony</type>
                <sendOnSuccess>true</sendOnSuccess>
                <sendOnFailure>true</sendOnFailure>
                <configuration>
                    <recipients>robert.moffat@db.com,StQv5mK1u+06afIwrhtN1n///pPiNy8tdA==</recipients>
                </configuration>
            </notifier>
        </notifiers>
        
    </ciManagement>
```

#### 3. Developers

You can define the developers section in your project.  If neither of the other two options are set, this will be used.

```
...
  <developers>
    <developer>
      <id>jdoe</id>
      <name>John Doe</name>
      <email>jdoe@example.com</email>
      
      ...
      
    </developer>
  </developers>
  ...
```

You should set these anyway, as they are reported in the build results.

### Identity

Identity configuration has sensitive data, such as private keys and therefore should not be committed in source control.
One of recommended ways to store identity information is to keep in in maven.settings of your local workstation.

See example below:


```xml
<profiles>
    <profile>
        <id>symphony</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <build-reporter-identity>
                         {
                            "privateKey": "-----BEGIN PRIVATE KEY-----\ ... blah ... =\n-----END PRIVATE KEY-----",
                            "email": "symphony.practice.bot1@list.db.com",
                            "commonName": "symphony.practice.bot1",
                            "certificateChain": [ ]     # if using cert-based authentication
                         }
            </build-reporter-identity>
        </properties>
    </profile>
</profiles>
``` 

Having done this, you can refer to this variable within your project's `pom.xml` file like below (this is taken from the [Demo App](../demos/demo-app/pom.xml).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project ...>
    <modelVersion>4.0.0</modelVersion>
      
    ... 

    <name>Symphony Demo App</name>
    <description>Demo App Using Spring Boot Starter</description>

    <properties>
        <symphony.reporter.identity>${build-reporter-identity}</symphony.reporter.identity>
        ... 
    </properties>

```

**NB:** This JSON format is defined by the [Symphony Identity Module](../identity/README.md).  


### Build Details

These improve the message that Symphony posts, to make it more useful to readers.

#### Build URL

This is set in the `ciManagement` section, and gives you a link to click on to get to the build page:


```
    <ciManagement> 
        <system>teamcity</system>
        <url>https://some.url/to.your.ci.build.html</url>
        
        ...
        
```

#### Hash Tags

You can add some hash-tags to the Symphony message.  There are two ways to do this:

##### 1. As A Property

A comma-separated list of hash-tags (without the hash), like so:

```

<properties>
    ...
    <symphony.reporter.hashtags>
            maven, build
    </symphony.reporter.hashtags>
</properties>

```

##### 2. ciManagement Section

You can define the hashtags in `<ciManagement>`.  You can create a `<notifier>` with type `symphony`:

```
    <ciManagement> 
        <system>teamcity</system>
        <url>...</url>
        <notifiers>
            <notifier>
                <type>symphony</type>
                <sendOnSuccess>true</sendOnSuccess>
                <sendOnFailure>true</sendOnFailure>
                <configuration>
                    <recipients>robert.moffat@db.com,StQv5mK1u+06afIwrhtN1n///pPiNy8tdA==</recipients>
                    <hashtags>build,maven</hashtags>
                </configuration>
            </notifier>
        </notifiers>
        
    </ciManagement>
```

### Symphony Endpoints

The build reporter needs to know how to access the pod.  In order to do this, the following URLs should be set:

#### 1. RSA-Based Login

Add properties (either in `pom.xml` or `settings.xml`) like so:

```xml
<properties>
  <symphony.reporter.agent.url>https://<youragent>/agent</symphony.reporter.agent.url>
  <symphony.reporter.pod.url>https://<yourpod>/pod</symphony.reporter.pod.url>
  <symphony.reporter.login.url>https://<yourpod>/login</symphony.reporter.login.url>
  <symphony.reporter.relay.url>https://<yourpod>/relay</symphony.reporter.relay.url>
</properties>
```

#### 2. Cert-Based Login

```xml
Add properties (either in `pom.xml` or `settings.xml`) like so:

```xml
<properties>
  <symphony.reporter.agent.url>https://<youragent>/agent</symphony.reporter.agent.url>
  <symphony.reporter.pod.url>https://<yourpod>/pod</symphony.reporter.pod.url>
  <symphony.reporter.sessionauth.url>https://<yourpod>/sessionauth</symphony.reporter.sessionauth.url>
  <symphony.reporter.keyauth.url>https://<your key manager>/keyauth</symphony.reporter.keyauth.url>
</properties>
```

#### Proxies

You can set a list of proxies which will be used by all of the endpoints in turn.  By supplying a list, you can run the same builds in different environments, and they should find the right proxy settings and just work.

```xml
<properties>
  <symphony.reporter.proxies>proxy1,proxy2,none</symphony.reporter.proxies>
</properties>
```

**NB:** Note `none` can be used to try without a proxy.

# Example

The [Demo App](../demos/demo-app) uses the build reporter to write a build result to Symphony.  You can see how it's configured there.

