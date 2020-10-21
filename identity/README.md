[![Maven Central](https://img.shields.io/maven-central/v/com.github.deutschebank.symphony/symphony-java-toolkit)](https://search.maven.org/search?q=com.github.deutschebank.symphony)

## What This Is

`symphony-java-client-identity` provides a single abstraction of `SymphonyIdentity`, which encapsulates the data needed to authenticate with a symphony server.  That is:
 
 - Private Key
 - Certificates
 - Common Name
 - Email Address

It can return `KeyManager` implementations which can be used with any HTTP Connection process calling Symphony Rest Endpoints.

**The main purpose of this is to supply credentials to unit tests without the security risk of checking in certificates/passwords into the project.**

If you are using one of the spring boot starters, this library is automatically used as a dependency.

## Maven Coordinates

Maven Coordinates for this project are:

```
<dependency>
	<groupId>com.github.deutschebank.symphony</groupId>
	<artifactId>symphony-java-client-identity</artifactId>
	<version>-- see above for version --</version>
	<scope>compile</scope>
</dependency>
```

## Formats Supported

 - `PemSymphonyIdentity`: can be constructed with PEM private key and (optionally) certificate strings.
 - `P12SymphonyIdentity`: can be constructed with a P12 (PKCS12) file, which is more normal in Java.
 
## Serialization

You can serialize/deserialize `SymphonyIdentity`s using [Jackson](https://github.com/FasterXML/jackson) like so:
 
```java
		SymphonyIdentity in;
		ObjectMapper om = new ObjectMapper();
		String json1 = om.writeValueAsString(in);
```

## Test Identity

One of the main purposes of this library is to encapsulate the logic required to provide a Symphony identity during CI testing.  In order to do this, you can call:

```
	TestIdentityProvider.getTestIdentity()
```

This will return a `SymphonyIdentity` instance for use with a unit test.  This will be resolved in one of the following ways:

1.  If you have a System property called `symphony-test-identity` set (containing a JSON string), this will be deserialized into an object and returned.
2.  If you have a file called `symphony-test-identity.json` in the current directory or any parent directory above that, this will be loaded and deserialized. (Useful for running tests in the IDE).

## Providing Test Identity With CI

1.  Add the following to your Maven `settings.xml` (or equivalent):

```
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    ...
    <profile>
      <id>symphony-test-profile</id>
      <properties>
          <symphony-test-identity>
				{
				   "privateKey": "...",
				   "email": "...",
				   "commonName":"..",
				   "chain": [
				     "-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----",
				     "-----BEGIN CERTIFICATE-----\n...\n-----END CERTIFICATE-----"
				  ]
				}
        </symphony-test-identity>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    ...
    <activeProfile>symphony-test-profile</activeProfile>
  </activeProfiles>
</settings>
```

2.  Add the following plugin to your `pom.xml`.  This will add the system property at test execution time:

```
<build>
	<plugins>
		
		...
		
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-surefire-plugin</artifactId>
			<version>3.0.0-M3</version>
			<configuration>
				<systemPropertyVariables>
					<symphony-test-identity>${symphony-test-identity}</symphony-test-identity>
				</systemPropertyVariables>
			</configuration>
		</plugin>	
	</plugins>
</build>
```