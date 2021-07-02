




# How To Use






## Configuration

### Override the Welcome Message

``` 
symphony:
  reminder:
    welcomeMessage:  "The reminder bot is active in this room.   Type a date or time in your conversation and I'll create a reminder for it" 
    
```

### Set the Default TimeZone

By default, time-zone is set to london, but you can override with any Java timezone like so:

``` 
symphony:
  reminder:
    defaultTimeZone:  "America/Los_Angeles" 
    
```

### Set Remind-Before

By default, if you chat to the bot "Remind me to go to town at 8pm" you'll get a reminder at 8pm.  You can change the exact time of the reminder when you create 

However, if you want your reminders _earlier_ by default you can configure this site-wide for the bot:

``` 
symphony:
  reminder:
    defaultRemindBefore:  30  # 30 minutes before.
```



