[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=org.finos.symphony.toolkit)

# Jackson JSON QuickFIX Module

This is a small [Jackson](https://github.com/FasterXML/jackson) plugin to allow you to serialize objects into JSON, as described by [the FIX JSON encoding specification](https://github.com/FIXTradingCommunity/fix-json-encoding-spec/blob/master/Encoding_FIX_using_JSON-User_Guide.md)

This is a stand-alone jar, and doesn't require Symphony or any of the other components described in this project.

## Installing

Maven coordinates are:

```
	<groupId>org.finos.symphony.toolkit</groupId>
	<artifactId>jackson-quickfix-module</artifactId>
	<version>find latest in artifiactory</version>
```

## Configuring Jackson

Build an `ObjectMapper` instance in Jackson like this:

```
	ObjectMapper om = new ObjectMapper();
	om.registerModule(new QuickfixjModule());
```


## Serializing Example

This example from the test:

```java
		Map<String, Object> in = new HashMap<>();
		in.put("object001", createComplexFixMessage());
		String out = om.writeValueAsString(in);
```

Creates the following FIX JSON output:

```json
{
	"object001": {
		"Header": {
			"BeginString": "FIX.5.0",
			"MsgType": "6",
			"SenderCompID": "Deutsche Bank",
			"TargetCompID": "Credit Suisse"
		},
		"Body": {
			"IOIID": "638d5f83-ab40-4c3e-aebe-15bc2620996f",
			"IOIQty": "1000",
			"IOITransType": "N",
			"Side": "B",
			"Symbol": "MSFT",
			"Text": "Place My Order!"
		},
		"Trailer": {
			
		}
	}
}
```

## Deserializing Example

works pretty much the same way:

```java
	MarketDataSnapshotFullRefresh o = om.readValue(in, MarketDataSnapshotFullRefresh.class);
```


This will deserialize a FIX JSON object into a `MarketDataSnapshotFullRefresh` object.

## Dictionaries

FIX.5.0sp2 is included on the classpath by default.  If you supply a different fix version to this, it will try to deserialize with the supplied version if the classes are on the classpath, otherwise it will attempt to use the latest, FIX.5.0sp2 version.



