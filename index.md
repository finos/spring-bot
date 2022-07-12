---
layout: default
title: Spring Bot
nav_order: 1
layout: home
---

<section class="front-page-top">
  <div class="logo-surround">
    <div class="logo-image">
      <img src="assets/images/2022_SpringBot_Horizontal.svg" title="Bot Logo" />
    </div>
  </div>
</section>

<section class="front-page-alt"><div class="inner flex"><aside markdown="1">

  “Like [Spring Web MVC](https://spring.io/guides/gs/serving-web-content/)...
     but for building chat-bots”
  
</aside><div class="item" markdown="1">
  This project is for:
  
   - building chat-bots in Java/Kotlin using Spring / Spring Boot
   - using a familiar, Spring-annotation-driven way to configure bots.
   - having access to the Java & Spring ecosystems for complex bot use-cases 
   
  Although there are many low-code and no-code ways to put a chat-bot together now, for advanced use-cases and situations where you want to integrate with existing systems, Java will be the way to go.   
  
</div></div></section>

<section class="front-page"><div class="inner" markdown="1">
<h1>Platform Bindings</h1>

Spring Bot is designed to be an _abstraction layer_ on top of other Java bot libraries.  i.e it handles the concerns above, and leaves the connectivity and API logic to lower-level libraries.  

This means that you can build bots in Spring Bot that will work **concurrently** on different chat-platforms.

Currently we have just have bindings for [Symphony](symphony.md) and  [Microsoft Teams](https://www.microsoft.com/en-GB/microsoft-teams/group-chat-software).

</div></section>


<section class="front-page"><div class="inner">
<div class="flex">
<div class="item" style="flex-basis: 100%"><h1>One Quick Example</h1></div>
<div class="item" markdown="1">

Here is an excerpt from the [To-Do Tutorial](symphony.md), with screenshots from Symphony and Microsoft Teams.  

Let's say I have a simple POJO called `ToDoItem` (containing fields like `Description`, `Creator` etc.) and a further POJO called `ToDoList` which looks like this.

</div>

<div class="item"  markdown="1">

```java
@Work
public class ToDoList {

  private List<ToDoItem> items = 
       new ArrayList<ToDoItem>();

  // setters/getters omitted for brevity
  
}
```

</div><div class="item"  markdown="1">

We can create a Spring Controller like so:

</div><div class="item"  markdown="1">

```java
@Controller
public class ToDoController {

  @ChatRequest(value="new", 
     description = "Create new item list")
  public ToDoList init() {
    return new ToDoList();
  }

```

</div><div class="item"  markdown="1">

This creates a new command for the bot.  When I type `/help`, the bot responds with...

</div><div class="item"  markdown="1">

![Help Page on Symphony](/assets/images/index/help.png)

![Help Page on Teams](/assets/images/index/help-teams.jpeg)

</div><div class="item"  markdown="1">

When I type `/new` the bot responds with an empty To-Do list like so.

</div><div class="item"  markdown="1">

![Empty To-Do list](/assets/images/index/new.png)

</div><div class="item"  markdown="1">

This is the same code running on Microsoft Teams mobile.

</div><div class="item"  markdown="1">

![Empty To-Do list Teams](/assets/images/index/new-teams.png)

</div><div class="item"  markdown="1">

I can put an add button on the To-Do list like this:

</div><div class="item"  markdown="1">

```java
@Controller
public class ToDoController {
  
 // Other methods
  
 @ChatButton(value = ToDoList.class, 
   buttonText = "Add")
 @ChatResponseBody(
   workMode = WorkMode.EDIT)
 public NewItemDetails add(User author) {
  NewItemDetails out = 
    new NewItemDetails();
  out.assignTo = author;     
  return out;
 }
}
```

</div><div class="item"  markdown="1">

This button will then appear on my To-Do List.

</div><div class="item"  markdown="1">

![Add Button](/assets/images/index/add.png)

</div><div class="item"  markdown="1">

Pressing that button gives me a form to fill in, containing the `NewItemDetails` fields.

This is Microsoft Teams Mobile again.

</div><div class="item"  markdown="1">

![Add Button](/assets/images/index/add-teams.jpeg)

</div><div class="item"  markdown="1">

... and so on. [Tutorial continues here.](symphony.md)

</div></div>

</div></section>

<section class="front-page-alt"><div class="inner flex"><aside markdown="1">

“Spring Bot handles [MVC](https://en.wikipedia.org/wiki/Model–view–controller) for your bot”

</aside><div class="item" markdown="1">

## Model

- Build stateless bots:  store all working state as data within the chat platform. 
- Create POJOs for your data model, persisting data into chat rooms and retrieving it using the [`History` API](reference.md#history).
- Spring Bot will handles serialization / deserialization via [Jackson]().

</div><div class="item" markdown="1">

## View

 - Spring Bot can auto-generate default views of your models. 
 - It provides functionality for using alternate views, and customizing views for each platform.

</div><div class="item" markdown="1">
 
## Controller

 - Mapping of `methods()` on your `@Controllers` to messages sent to the bot.
 - Resolving method parameters like `User`, `Word`, `HashTag`...
 
</div></div></section>

<section class="front-page"><div class="inner flex"><aside markdown="1">

Read the [Getting Started](getting-started) tutorial to see how...

</aside><div class="item" markdown="1">

## Source

[See Github](https://github.com/finos/symphony-java-toolkit)

## Tools

To see how this toolkit works in action, check out some source code here:

### [Reminder Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/reminder-bot)

Creates reminders in chat rooms when you start talking about time.  Uses Stanford NLP Time package.

### [RSS (News) Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/rss-bot)

Allows you to configure news feeds into chat rooms.

### [Poll Bot](https://github.com/finos/symphony-java-toolkit/tree/master/tools/poll-bot)

Allows you to poll the members of a room, providing a question and a list of answers to vote on.

</div></div></section>

<section class="footer">
   <a href="https://db.com"><img id="dbicon" src="assets/images/index/Deutsche_Bank-Logo.svg" class="corp-logo" alt="DB" title="Deutsche Bank">
  </a>
  <a href="https://www.finos.org"><img id="finosicon" src="assets/images/index/finos_wordmark.svg"  class="corp-logo" alt="FINOS" title="FINOS">
  </a>
  <a href="http://kite9.com"><img id="kite9icon" src="assets/images/index/kite9logo.svg"  class="corp-logo" alt="DB" title="Kite9">
  </a>
</section>