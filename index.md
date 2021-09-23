---
layout: default
title: Welcome
nav_order: 1
layout: home
---

<section class="front-page-top">
  <div class="logo-surround">
    <div class="logo-image">
      <img src="assets/images/logo.svg" title="Bot Logo" />
    </div>
    <div class="logo-title"><span class="logo-spring">SPRING</span><span class="logo-bot"> BOT</span></div>
  </div>
</section>

<section class="front-page-alt"><div class="inner"><aside markdown="1">

  “Like [Spring Web MVC](https://spring.io/guides/gs/serving-web-content/)...
     but for building chat-bots”
  
</aside><div markdown="1">
  This project is for:
  
   - building chat-bots in Java/Kotlin using Spring / Spring Boot
   - using a familiar, Spring-annotation-driven way to configure bots.
   - having access to the Java & Spring ecosystems for complex bot use-cases 
   
  Although there are many low-code and no-code ways to put a chat-bot together now, for advanced use-cases and situations where you want to integrate with existing systems, Java will be the way to go.   
  
</div></div></section>

<section class="front-page"><div class="inner" markdown="1">
 
# One Quick Example


Here is an excerpt from the [To-Do Tutorial]().  

Let's say I have a simple POJO called `ToDoItem` (containing fields like `Description`, `Creator` etc.) and a further POJO called `ToDoList` which looks like this:

```java
@Work
public class ToDoList {

  private List<ToDoItem> items = new ArrayList<ToDoItem>();

  // setters/getters omitted for brevity
  
}
```

We can create a Spring Controller like so:

```java
@Controller
public class ToDoController {

  @ChatRequest(value="new", description = "Create new item list")
  public ToDoList init() {
    return new ToDoList();
  }

```

This creates a new command for the bot.  When I type `/help`, the bot responds with...

![Help Page](/assets/images/index/help.png)

When I type '/new' the bot responds with an empty To-Do list like so:

![Empty To-Do list](/assets/images/index/new.png)

I can put an add button on the To-Do list like this:

```java
@Controller
public class ToDoController {
  
  // Other methods
  
  @ChatButton(value = ToDoList.class, buttonText = "Add")
  @ChatResponseBody(workMode = WorkMode.EDIT)
  public NewItemDetails add(User author) {
    NewItemDetails out = new NewItemDetails();
    out.assignTo = author;      // assign to-do item to the person who clicked the button
    return out;
  }
}
```

This button will then appear on my To-Do List:

![Add Button](/assets/images/index/add.png)

... and so on. [Tutorial continues here.](getting-started.md)

</div></section>

<section class="front-page-alt"><div class="inner"><aside markdown="1>

# MVC For Bots

Spring Bot has similar logic to Spring Web MVC, in that it handles the MVC (Model, View, Controller) pattern for your code.

</aside><div markdown="1">

## Model

- Spring Bot espouses a technique for building stateless bots, recommending that you store all working state as data within the chat platform. 
- You use POJOs to define the data model that your bot needs (just like in the To-Do example above), persisting data into chat rooms and retrieving it using the [`History` API](reference.md#history)
- Spring Bot will handle serialization / deserialization of this model to your chat platform using Jackson.

## View

 - Spring Bot will generate default views of your model, which are sent to the user. 
 - It provides functionality for using alternate views, and customizing views for each platform.
 
## Controller

 - Mapping of methods on your controllers to messages sent to the bot.
 - Resolving method parameters (such as the `User` in the example above).
 
</div></div></section>

<section class="front-page"><div class="inner" markdown="1">


# Platform Bindings

Spring Bot is designed to be an _abstraction layer_ on top of other Java bot libraries.  i.e it handles the concerns above, and leaves the connectivity and API logic to lower-level libraries.  

This means that you can build bots in Spring Bot that will work concurrently on different chat-platforms.

Currently we have just have bindings for [Symphony](symphony.md) with MS Teams coming next.

</div></section>


<section class="front-page-alt"><div class="inner"><aside markdown="1">

## Demo Bots

- [Claim Bot](https://github.com/finos/symphony-java-toolkit/tree/master/demos/claim-bot)  - discussed in the [Symphony](symphony.md) tutorial.
- [To Do Bot](https://github.com/finos/symphony-java-toolkit/tree/master/demos/todo-bot)  - discussed in the [Getting Started](getting-started) tutorial.

</aside><div markdown="1">

## Tools

To see how this toolkit works in action, check out some source code here:

### [Reminder Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/reminder-bot)

Creates reminders in chat rooms when you start talking about time.  Uses Stanford NLP Time package.

### [RSS (News) Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/rss-bot)

Allows you to configure news feeds into chat rooms.

### [Poll Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/poll-bot)

Allows you to poll the members of a room, providing a question and a list of answers to vote on.

</div></div></section>


