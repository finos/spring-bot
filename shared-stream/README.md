[![Maven Central](https://img.shields.io/maven-central/v/org.finos.symphony.toolkit/symphony-java-toolkit)](https://search.maven.org/search?q=com.github.deutschebank.symphony)[![Javadocs](https://img.shields.io/badge/Javadocs-symphony--shared--stream-green)](https://javadoc.io/doc/com.github.deutschebank.symphony/symphony-shared-stream/latest/index.html)

# Symphony Shared Stream

The purpose of this module is to provide a drop-in stream consumer for Symphony that allows a cluster of bot replicas to coordinate work.  That is, prevent the problem of two or more bots simultaneously consuming and processing the same Symphony message.

## Installation

1.  A Coordination Room created on Symphony (You'll need the Stream ID).   This allows the bots to let each other know of their presence.
2.  Your application implements the interface `StreamEventConsumer`, through which it receives `V4Event`s from the Symphony DataFeed.  If you are running using Spring, just create a `Bean` that implements this interface.
3.  Add the following dependency in your application:

```xml
<dependency>
  <groupId>com.github.deutschebank.symphony</groupId>
  <artifactId>symphony-shared-stream</artifactId>
  <version>--see above--</version>
</dependency>
```

### With Spring Boot

This project is primarily intended for use with Spring Boot, and so can be dropped in as a starter.  `SharedStreamConfig` contains all the Spring-specific configuration.

Here's a breakdown of the configuration space:

```
symphony.stream:
  coordination-stream-id:  REQUIRED. this is the room where coordination messages will be written.  Contains details about who is a member of the cluster, and the current leader (REQUIRED)
  
  # the rest of these are optional
  environment-identifier:  'test' is default.  A textual identity for the cluster (e.g. 'test' or 'prod') allowing multiple clusters to potentially use the same room.
  participant-write-interval-millis: one day by default.  All cluster members must "report in" to the coordination-stream-id room within this time interval to be considered part of the cluster.
  algorithm: BULLY or MAJORITY.  Choose bully for cluster sizes of 2.  Bully is default.  (See below for details)
  endpoint-scheme: http or https.  Defaults to http.
  endpoint-path:  This defaults to "/symphony-api/cluster-communication"
  endpint-host-and-port:  This is generally calculated, but you can override it if you want, although will be different for each cluster member. These 3 properties together are the address which cluster messages can be sent on.  Uses the hostname of the machine, and the `server.port` port-number if declared or 8080 if not. 
  timeout-ms: Defaults to 5000.  The leader must respond to other cluster members with a ping message in this time, otherwise they'll try to elect a new leader.
  start-immediately:  true by default.  Whether or not the Symphony datafeed should be created and consumed as part of the SymphonyStreamHandler creation.
```

### Without Spring Boot

You can create all of the components without using Spring Boot if you want to, optionally replacing any of them with alternative implementations.  Here is a breakdown of what each interface is doing:


|Interface |Purpose         |
|----------|----------------|
|`SymphonyStreamHandler`|Provides a robust wrapper around the Symphony `DataFeed` convention, along with logging of exceptions.|
|`SharedLog`|Provides a way for cluster members to communicate their existence to one another|
|`SymphonyRoomSharedLog`|Implementation of the above, via a Symphony room (set up in advance by you)|
|`SymphonyLeaderEventFilter`|Filters events from the `SymphonyStreamHandler` so that only the "leader" processes them (the leader being the cluster member who last wrote a leader message to the `SymphonySharedLog` |
|`ClusterMember`|Handles deciding who, out of a number of cluster members (determined from the `SharedLog`), is the leader.  |


## Underlying Assumptions

1. "The Cluster" is the collection of running bot instances with the same Cluster Identifier.  It may consist of one or more bots.

2.  All processing of messages is done by a single bot within the cluster, the "Leader".

3.  If a bot can talk to Symphony, it will also be able to communicate via HTTP to other bots in the cluster.  This is essential for bots to tell each other about their "liveness".

4.  Message ordering is assumed to be critical. This means that if a bot begins processing (i.e. takes over leadership), it will only consider messages going forward from that point onwards.  This means that potentially messages might get ignored by the cluster in the period between a leader dying and a new leader being elected.  Why is this?  Since there could be "side effects" of processing a message, and the leader may have part-way processed the messages, it's not possible for a new leader to just "pick up" the work of an old leader and carry on.  If this is not the desired behaviour, then some kind of distributed transaction layer will be needed.

## Cluster Basic Algorithm

The existing cluster algorithm is leader-election from the RAFT algorithm, which works as follows:

1.  **Startup.**  When a bot starts up, it writes a message to the Coordinating Room giving details of its existence to the other bots. If the bot can't write to this room at this time, an exception is thrown and startup will fail.

2.  **Determining Leadership.**  Bot instances advertise their leadership of the cluster within the Coordinating Room.   Since all bots consume the [Symphony Datafeed](), once a Bot instance receives a message that it is the leader, it will begin processing the messages in the Datafeed.  

3.  **Suppression of Elections.**  The Leader routinely pings the other bot instances forming the cluster.  This will be done on any new stream event from Symphony, or every 5 seconds, whichever is the greater.  

4.  **Timeout.**  Non-leader bots _expect_ to receive pings within a given period of time.  This is semi-random to that each bot has a different tolerance to leader-lateness.  

5.  **New Election Condition. ** Should the leader fail to ping the other bot instances, they assume that either it is a) dead or b) disconnected from Symphony, and therefore begin electing a new leader.  One bot proposes an election to all the other bots.

6.  **Voting.** Leader-election is based on RAFT.  That is, the bot proposing the new election votes for themselves, and then propositions other bots for votes.  Each bot has one vote in any given election.  (Elections are numbered).  If a bot receives back n/2+1 or more votes (where n is the size of the cluster) then they become the new leader.

7.  **Election.** Once a new leader has been elected, they post a message in the Coordinating Room to say that is the case.  If the old leader is processing messages at this point, they will now be aware that they are _not_ the leader, and give way to allow the new leader to take over.

8. **Participation Notification.** Bots advertise their HTTP endpoints in the Coordinating Room every 24 hours.  This means a new member of the cluster can review the room messages in order to decide how many other bots are in the cluster, how to talk to them and who the current leader is.

9. **Algorithms.** There are two algorithms supplied for choosing a leader of the cluster.  BULLY and MAJORITY.  Simply, MAJORITY works where there is an odd-sized cluster.  BULLY is appropriate if the cluster is only 2 members in size - in this case one will be primary (the bully) and one will be backup, handing control to the bully when it is present in the cluster.

## MAJORITY Algorithm: Failure Scenarios

1.  **Leader Dies.  **  In this event, other bots in the cluster will try to elect a new leader.   Their "timeouts" are staggered to avoid all the bots attempting this at the same time.  It may take a couple of elections for the bots to agree on a new leader.  The new leader message is posted in the Coordinating Room, and then the new leader will "suppress" the other bots with ping messages.

2. **Leader has no network connectivity.** In this case, the leader will neither receive stream messages from Symphony, or emit pings to the other cluster members.  Presently, the remaining members of the cluster will elect a new leader, as above.  Some Symphony events are likely to be unprocessed while the election occurs.

3. **Leader can't connect to Symphony.** In this case, the leader will miss stream events.  The other bots in the cluster will notice the absence of ping messages relating to these events, and after _timeout_ begin a leader election.  Some Symphony events are likely to be unprocessed while the election occurs.

4. **Leader connects to Symphony, but not other bots.** In this event, the leader will probably perform it's duties of replying to messages, but other bots won't receive the pings.  In this case, they will attempt to elect a new leader. It is likely that the existing leader won't be able to participate in this election.  If another bot manages to get n/2+1 votes, they will write to the Coordinating Room, which will notify the old leader that they are no longer leader, and allow for a seamless transition from the old leader to the new.

5. **Split-Brain.** Bots in different datacentres are unable to talk to one another.  In this case, ping messages from the leader will go missing to one set of bots. If the isolated set has a majority, they will be able to elect a new leader, and inform the leader (via Symphony) of a leader change, otherwise, the leader will stay in charge within it's set.


## BULLY Algorithm: Even-Sized Clusters 

If there are 2 nodes in the cluster (very common for hot-hot failover scenarios) then we have a problem that we can't elect by consensus.  To avoid this problem, we can use a bully algorithm, where the leader is the "strongest" (in this case, having the highest hash-code of their endpoint-string).  

Note that split-brain events cannot be decided by bully - you will end up with both nodes being hot.  In this case, the node writing the last leader message to the participation room will be the one processing the events.

