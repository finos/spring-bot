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

