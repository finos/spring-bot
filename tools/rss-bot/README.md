# RSS Bot

This provides a simple bot that is able to publish RSS news articles into a chatroom, by consuming RSS feeds.  The RSS feeds are configurable by the members of the room.  As usual, type /help to get the bot to provide help information.  This is standalone, and just requires `application.yml` to run.  

Add the bot to a room (or 1-1 chat) and type /help to begin configuring your RSS feeds in Symphony.

## Things To Note

- In this example, we are using the chat-workflow module to define the "look" of the messages with the bot.  
- We use the article messages themselves to track what has already been published in the room.
- The `FeedList` object is held in the room - this contains the details of the feeds you've subscribed to.
- We have custom Freemarker templates for the look of the articles and the feedlist.
- We're using Spring's Scheduling to download the feeds every hour.
- Every month, we pause the feed and send a message to the user asking them to resume it.  This stops the bot posting to rooms that aren't interested anymore.


## Configuration

You can configure the bot to use proxies to collect the data. This follows the same format as `symphony-api-spring-boot-starter` e.g.

```
symphony:
  rss:
    proxies:
      - host: somehost.com
        port: 8080
      - host: <none>
      - host: someotherhost.com
        port: 8090
```

The RSS bot will initially try each proxy in turn to set up the feed, and then store the correct proxy with the feed for later use.       
       
You can also configure how often the bot will check for messages.  Note, there is an overhead here - it has to download a list of streams that the bot is a member of, and then look in each stream to find out what the RSS settings are in that stream.  So, n+1 API calls to Symphony, where n is the number of rooms/chats containing the bot.   

```
symphony:
  rss:
    cron: 0 0 * * * MON-FRI  # Means once an hour, monday-friday
```

Check the Spring `@Scheduled` documentation for the format of this expression (it follows `cron`). 
    
       
