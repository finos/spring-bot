# Demo Bot

This provides a very simple bot which works on Teams and Symphony, and demonstrates a few bits of functionality:

- You can ask the bot for `help` and get back a list of things it can do.
- It provides a couple of built-in commands that you can enter (see `DemoController`)
- It is an 'Echo Bot'.  That is, it will echo back to you the things you type into it (see `EchoConsumer`)
- It provides a welcome message when users are added to a room it is in (see `DemoApplication`)
- It will provide a "Hello" in a room of a given name that the bot is a member of (see `StartUpMessageBean`)

If you don't want to go through the tutorial and assemble the code yourself, you can download this instead and build it on your desktop.

You will need to provide settings for the `application.yml` file according to your own environment.  An example one is provided in this directory that you can customize.
