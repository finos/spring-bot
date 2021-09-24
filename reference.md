---
layout: default
title: Reference Guide
nav_order: 4
nav-menu: true
---

# Reference Guide

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

todo;
mention todo list.

more details in the javadocs.

# Basic Workflow

This diagram shows the process for dealing with events on the Symphony platform, although other platforms work in the same way.

![Workflow Events from/to the Symphony Platform](/assets/images/reference/chat-workflow.png)

## 0. (Startup)

When your application starts up, Spring does a class-path scan of your project to find your beans.  As part of this process, a `ChatHandlerMapping` instance is created for each controller method annotated with `@ChatRequest` or `@ChatButton` (see [Annotations](#annotations)).   

This is very much like Spring Web MVC, which tries to map controller methods to HTTP-endpoints by the `@GetRequest` annotation (and others).

## 1. Events are received from the chat platform. 

These will be dependent on the platform itself, but usually will be things like:

 - A message is sent to your bot
 - A room is created 
 - A form is submitted
 
...etc.

## 2. These are mapped to the `Action` class hierarchy.

Actions are a platform-agnostic view of upstream events.  The table below summarises:

| Class                | Features                      |
|----------------------|-------------------------------|
|`Action`               | - `getAddressable()`: the source of the action, i.e. the room or chat it originated in.|
|                      | - `getUser()`: the user that originated the action.|
|                      | - `getData()`: data associated with the action (usually a `Map<>`) |
|`FormAction`           | extends `Action`, but also has `getFormData()` for the data posted in the form. Used when a form is posted to the bot.  Form data will generally be an `@Work`-annotated POJO, although if the form data _can't_ be mapped to a POJO, then the `FormSubmission` class is used instead which captures the form data in a simple map. |
|`SimpleMessageAction`   | extends `Action`, but contains `getMessage()`, for the message that was sent to the bot.  Used when a user or other bot chats with your bot.|

## 3. `Action`s are processed by `ActionConsumer`s

Spring Bot bundles a number of `ActionConsumer` beans, which process the `Action`s.  All `ActionConsumer` beans receive all actions.   You are free to define your own `ActionConsumer` beans in your application.

Of special interest is the `ChatHandlerMappingActionConsumer`.  This is responsible for routing `Action`s to the `ChatHandlerMapping` instances, which in turn mediate between the `Action` and calling your controller method.

### CURRENT_ACTION

`Action` has a thread-local field called `CURRENT_ACTION` which you can call to get the details of the action being processed by the current thread, at any time.  This is useful for initializing values, for example here we are initializing the `Reminder` object so that the author is the user from the action:

```java
@Work
public class Reminder {
  
  String description;
  
  @Display(name = "Remind At")
  LocalDateTime localTime;

  User author = Action.CURRENT_ACTION.get().getUser();

  // getters / setters

}
```

## 4. `Response`s are created

`Response` classes have different purposes as follows:

| Class                | Purpose                        |
|----------------------|-------------------------------|
| `Response`            | All responses have `getAddressable()` for where they are to be sent|
|  - `DataResponse`        | All responses sending data to a chat will implement this class.  Has `getTemplate()`, which dictates the format of the message, and `getData()` to indicate the content of the data-part of the message.
|  -- `MessageResponse`     | A particular type of `DataResponse`, which contains a `Message` body (see [Content Classes](#content-classes))
|  --- `AttachmentResponse` | Like a `MessageResponse`, but also contains an attachment to include with the message |
|  -- `ErrorResponse`       | You can wrap a `Throwable`/`Exception` in this and send it as a response when your process fails (useful for debugging).
|  -- `WorkResponse`        | Contains a [`@Work`](#work)-annotated POJO, which needs to be rendered and sent back, either as a form or display data |

You can either return `Response` objects (or `List`s of `Response` objects) from your controller method or `ActionConsumer` instance, or you can `@Autowire` the `ResponseHandlers` bean into your controller and use that directly.

## 5. `Response`s are sent to `ResponseHandler`s.

`ResponseHandler` beans are held in a priority-ordered list inside the `ResponseHandlers` bean, and responses pass along the chain of all `ResponseHandler`s.  This means you can write `ResponseHandler` beans to "decorate" the `Response` object as it passes down the chain.

There are platform-specific `ResponseHandler`s which converts the generic `Response`s back into chat-platform specific API calls.  So, messages for **Symphony** `Addressable`s will be processed by the `SymphonyResponseHandler`, while teams or email `Addressable`s can be processed by other `ResponseHandler`s.

# Annotations

## `@Work`

This annotation is designed to be put on any objects (POJOs, specifically) that will carry your model back and forth from your Spring runtime to the chat, and back again.  For example, a `ToDoItem` will be annotated like this:

```java

@Work
public class ToDoItem {

  // fields, getters, setters
```

By requiring this annotation, we achieve two things:

  - You clearly delineate objects that will contain data to be sent and received in conversations.
  - You avoid a class of injection attacks in Java, where a client can post any class to you.

## `@ChatRequest`

The `@ChatRequest` annotation on a controller method indicates that the method should be called when a user in conversation with the bot types some message that matches a given pattern.

### Simple Commands

In it's simplest form, this can be used on a Spring controller like so:

```java

  @ChatRequest(value="new", description = "Create new item list")
  public ToDoList init() {
    return new ToDoList();
  }

```

In this case, when the user types `/new` then the method will be executed.  In the case above, that means returning a `TodoList` object back to the user (which has the `@Work` annotation).

### Multiple Commands

`value` can take multiple, different commands, allowing you to provide synonyms, or perhaps even alternate language versions of the pattern:

```java

  @ChatRequest(value={"new", "nouveau"}, description = "Create new item list")
  public ToDoList init() {
    return new ToDoList();
  }

```

### Pattern Matching

`value` can also match variables (given in curly braces `{}`), like so:

```java
  @ChatRequest(value="delete {item}", description = "Remove items by number. e.g. \"/delete 5 6 7\"")
  public ToDoList delete(@ChatVariable(name = "item") List<Word> toDelete, Optional<ToDoList> toDo) {
    
     // body of method    

  }

```

Above, we are creating a _chat variable_ called `item`, which will resolve to `List<Word>` (i.e. a list of words).  The annotation `@ChatVariable` allows us to annotate a method parameter to receive this variable.  Use:

 - `Optional<X>`:  where the _chat variable_ matches one or zero X's (where X is some sub-type of [Content](#content-classes).
 - `List<X>`:  the _chat variable_ contains zero or more X's.  In the example above, the user can type `/delete 5 6 7`, so each of `5`, `6` and `7` will be passed in as `Word`s in a `List<Word>`.
 - `X`: where _exactly 1 X is required.  If the user doesn't provide all the parameters as specified, the pattern won't match.
 
 
### Restricting Availablity

You can limit the command to just _room administrators_, or _particular chat rooms_.   

```java

  // particular rooms
  @ChatRequest(value="limited rooms", description="something only for certain rooms", rooms={"Special Room", "Admin Room"}})
  
 
  // only admins in a room
  @ChatRequest(value="only admin", description="Only room administrators", admin=true})
  
```

Beyond this, you can programmatically control whether or not the command does anything, as demonstrated by the RSS Bot.

## `@ChatButton`

The `@ChatButton` annotation allows you to set up a button on a controller method.  In the example below, we provide the button on the `NewItemDetails` form which allows the user to add a new element into their To-Do List.

```java
  @ChatButton(value = NewItemDetails.class, buttonText = "add")
  public ToDoList add(NewItemDetails a, User u, Optional<ToDoList> toDo) {
    ToDoList out = toDo.orElse(new ToDoList());
    out.getItems().add(new ToDoItem(a.getDescription(), u, a.getAssignTo(), Status.OPEN));
    reNumber(out);
    return out;
  }
```

The `@ChatButton` annotationn _requires_ the `buttonText` (i.e. the label) and the `value` (the class it appears on) to be set, as shown above.

### Restricting Availablity

You can limit the button to just _room administrators_, or _particular chat rooms_.   

```java

  // particular rooms
  @ChatButton(value=SomeWork.class, buttonText="delete", rooms={"Special Room", "Admin Room"}})
  
 
  // only admins in a room
  @ChatButton(value=SomeWork.class, buttonText="delete", admin=true})
  
```

Beyond this, you can programmatically control whether or not the command does anything, as demonstrated by the RSS Bot.

## `@Template`

The `@Template` annotation is used on a POJO class to indicate which templates it should use by default.  Generally, all `@Work` objects can either be returned for _viewing_ or _editing_ (as a form).  So template has two optional arguments `view` and `edit` where you can specify the _name_ of a template to use for rendering the object in a given mode.

Here is an example from the Poll Bot, where we are specifying a template for the view mode:

```java

@Work
@Template(view="answer")
public class Answer {

  // etc.
  
```

**Note**: `@Template` does not provide the _path_ of the template.  It is up to the `ResponseHandler` to resolve the template given it's name.  That's because the template used on MS Teams will be an adaptive card template, whereas on Symphony it will be a Freemarker template.  (See [Response Handlers](#response-handlers)).

## `@Display`

Display is used to guide the template-generation process.   You can set the field name, or exclude the field from rendering in the template generator.  Here is an example:

```
@Work
public class SubscribeRequest {
  
  @Display(name = "Subscription Name (Optional)")
  String name; 
  
  // more code
```

## `@Dropdown` 

`@Dropdown` is a symphony-specific annotation also used to guide template generation.   In the example of the code below, usually, a `String` class would be rendered as a simple input field (in edit mode).  However, by using the `@Dropdown` annotation you can instruct the template generator to build a `<select>` picker, where the user can select from a limited set of options.  

The `data` parameter indicates the source of the data within your Entity Json payload.

By default, this is expected to be a map, however you can set the `format` parameter for a custom data-structure.  See `RoomFormat` for an example of that.

@Work
public class TimezonePicker {

  @Dropdown(data = "entity.timezones")
  public String timezone;
  
}


## `@ChatResponseBody`

This annotation is given as a clue to the template engine as to whether the response is expected in view or edit mode.  For example, the method below returns a `TodoList` object, but the `@ChatResponseBody` tells us that this will be returned in edit mode (i.e. a form) rather than view mode (just for display).

```java

  @ChatRequest(value="show", description = "Show current list of items")
  @ChatResponseBody(workMode = WorkMode.EDIT)
  public ToDoList show(Optional<ToDoList> in) {
    // method body
  }
```

You can also use `@ChatResponseBody` to specify a particular template to be used:

```java

  @ChatResponseBody(template = "my-answer")
  public ToDoList showInSomeOtherWay() {
    // method body
  }

```

# Parameter Resolution

You can specify controller methods annotated with `@ChatButton` or `@ChatRequest` with a variety of different parameters.  These are resolved (i.e. bound to values) when your controller method is called by a number of built-in classes implementing the `WorkflowResolverFactory` interface.   

 - Implement your own beans implementing `WorkflowResolverFactory` for custom parameter handling.
 - `WorkflowResolver`s have a priority.  If a higher priority resolver cannot resolve a parameter, the next-highest resolver tries and so on.  Eventually, if a method parameter _can't_ be resolved then an error is logged.
 - Most `WorkflowResolvers` allow `Optional<>` parameter types.  That way, should the resolver-chain _not_ resolve a parameter, there is no error, and an empty Optional is returned.

## 1. `@ChatVariable` Annotated Parameters

If a method parameter is annotated with `@ChatVariable` (as below) then the `ChatVariableWorkflowResolverFactory` will resolve the parameter based on the message the user typed to the bot:

```java
  @ChatRequest(value="delete {item}", description = "Remove items by number. e.g. \"/delete 5 6 7\"")
  public ToDoList delete(@ChatVariable(name = "item") List<Word> toDelete, Optional<ToDoList> toDo) {
```

## 2. `Addressable`, `Chat`, `User`

Add these parameters to a method to find out who the message came from, and the room or chat it came from.  e.g.

```java
  @ChatRequest(value="where")
  public Object details(User author, Optional<Chat> roomWhereItHappened) {
    // do something
  }
```

|Class           |Purpose              |
|----------------|---------------------|
|`Addressable`    |An interface which indicates something that can be sent a message|
|`User`          |A sub-class of `Addressable`, indicating a specific user or a 1-1 chat.  |
|`Chat`          |A multi-way chat, with multiple users, like a chat room or topic|

**Note**: If you want a method to work whether it is happening inside a chat-room or not, you can use `Optional<Chat>` as the argument type, as shown above. 

## Spring Beans

You can add any spring beans as parameters to your mapped methods, although usually you will want to `@Autowire` them into the controller properties to keep your method signatures simple.

## Form Data

User data can be captured in a form, based on a `@Work`-annotated POJO class.  When this data is returned to your method, you will need a parameter to receive it, like so:

```java
  @ChatButton(value = NewItemDetails.class, buttonText = "add")
  public ToDoList add(NewItemDetails a, User u, Optional<ToDoList> toDo) {
    // 'a' contains the form submission data from the NewItemDetails form
  }
```

## Other Message Parts

Let's say the user types a long message:

> "Let's go shopping on saturday, @Suresh Rupnar and @Robert Moffat"

We might not have a fixed pattern for this kind of message that we can put into `@ChatRequest`.  However, we can capture the various parts of the message quite easily:

```java
  @ChatRequest(value = "*")
  public void listen(Message wholeMessage, User firstuser, User secondUser) {
    // 'a' contains the form submission data from the NewItemDetails form
  }
```

todo: add another.

## Historical Messages

For complex workflows, you might rely on some previous data from the chat room as context for whatever it is you are doing.   For example, when we add an item to the To-Do list, we want to use the last known state of the To-Do list in the room, which might have been many messages ago.  

```java
  @ChatRequest(value="show", description = "Show current list of items")
  @ChatResponseBody(workMode = WorkMode.EDIT)
  public ToDoList show(Optional<ToDoList> in) {
     // parameter 'in' here contains the last To-Do list put in the chat where the /show command was typed.
  }
```

This is implemented by the `MessageHistoryWorflowResolver`, which uses the [History](#history) functionality.

**Note: ** Since this involves a look-up, this is a low-priority `Workflow Resolver`.  

# Returning From Controller Methods

In the same way that `WorkflowResolver`s resolve method parameters, `ResponseConverter`s are able to turn the objects you _return_ from your controllers into `Response` instances.

Supplied, there are 3 main things you can return from your controller methods.  However if you implement a `ResponseConverter` bean you can extend this:

|Return a...   |What Happens             |
|--------------|-------------------------|
|`Response` instance|This is routed to the `ResponseHandlers` bean.|
|`List<Response>` instance|*All* of those `Response`s are sent to the `ResponseHandlers` bean.|
|`@Work`-annotated object|This is converted into a `Response` by the `WorkResponseConverter`, which considers templating from `@ChatResponseBody` and returns into the chat it came from|

## `ResponseHandlers`

Instead of relying on the return object, you can `@Autowire` an instance of `ResponseHandlers` into your controller. 

This class aggregates together (in priority order) all of the defined `ResponseHandler`s defined in your project.  With this, you don't need to return anything from your controller method at all.   Instead, you can simply call the `accept(Response)` method at any point in your controller method.

# `Content` Classes

Before a textual message is handed to your controller method, it is parsed and turned into a hierarchy of `Content` classes.  These are much easier to manipulate and pattern-match with than a piece of plain text, or other markup.  Also, the `Content` class structure is platform-agnostic, which means your method can be too.  

We use the `Content` class structure for parsing messages _arriving_ into your controller method, you can also use it within the method for constructing messages _leaving_, which can then be formatted in a platform-specific way by an appropriate [Response Handler](#response-handlers).

## Features of `Content` Objects

- `Content` implementations are _immutable_ (since they are shared by method parameters and used in other places).  
- `Content` implementations have various utility methods, such as `visit()`, `without()`, `startsWith()`, `matches()` and `getNth(Class, n)`, which allow you rich functionality to do clever pattern-matching within your controller methods if the annotations aren't fine-grained enough.
- `Content` instances are used for `@ChatVariable` mapping.

### Simple `Content`

|Class          |Purpose                      |
|---------------|-----------------------------|
|`Content`       |A parent interface, indicating something that is read from/can be sent to a chat room|
|`Word`          |A single word. e.g. `database`|
|`CodeBlock`     |A block of code from the chat  |
|`Table`         |A table (like an HTML table) |
|`Tag`           |Platform dependent, but probably a `$cashtag`, `#hashtag` or `@mention`.  |
|`User`          |Subclass or `Tag` (meaning an `@mention` - note this implements `Addressable` too, so you can use it to construct `Response`s.) |

### `OrderedContent`

`OrderedContent` represents things like _lists_, _paragraphs_ and whole _messages_:

|Class          |Purpose                      |
|---------------|-----------------------------|
|`OrderedList`   |A numbered list of things.  |
|`UnorderedList` |A bulleted list of things.  |
|`Paragraph`     |A list of words, or other bits of content.  |
|`Message`       |The whole message from the chat. |

# Conversations API

You can get details of the chats and 1-1 conversations your bot is engaged in by `@Autowire`-ing the `Conversations` bean into your code.  If you are working with multiple chat platforms concurrently, you may need to wire in a platform-specific bean, such as `SymphonyConversations`.  This gives you functionality such as:

 - Find out all the conversations containing your bot
 - Creating new rooms/topics (i.e. a `Chat`)
 - Finding out who is in a room/topic (i.e. a `Chat`)
 - Finding out who is an administrator for a given `Chat`.

```java
public interface Conversations {

  /**
   * Returns all the conversations that the bot is a member of.
   */
  public Set<Addressable> getAllConversations();

  /**
   * Returns the subset of all conversations that are chats.
   */
  public Set<Chat> getAllChats();

  public Chat ensureChat(Chat r, List<User> users, Map<String, Object> meta);
  
  public List<User> getChatMembers(Chat r);
  
  public List<User> getChatAdmins(Chat r);

}

```

As an example, this API is used by the [Reminder Bot](/reminder-bot), whose `TimedAlerter` checks each conversation it belongs to on a regular interval to see if there are any reminders it needs to issue in that conversation: 

```java
  public void onAllStreams(Consumer<Addressable> action) {
        LOG.info("TimedAlerter waking");

        if (leaderService.isLeader(self)) {
            Set<Addressable> allRooms = rooms.getAllConversations();
            allRooms.forEach(s -> action.accept(s));
            LOG.info("TimedAlerter processed " + allRooms.size() + " streams ");
        } else {
            LOG.info("Not leader, sleeping");
        }
    }
```

# History API

You can pull back historic data from any `Addressable` by `@Autowire`-ing the `History` bean into your code.  If you are working with multiple chat platforms concurrently, you may need to wire in a platform-specific bean, such as `SymphonyHistory`.  This gives you functionality such as:

 - `getLastFromHistory`: returns an `Optional` containing the last `@Work` annotated-object in a given `Addressable` history of a particular class.
 - `getFromHistory`: returns a `List` containing all of the `@Work` annotated-objects in a given `Addressable` history of a particular class.

As an example of use, in the [Poll Bot]() there is a button to "End The Poll Now", which is implemented in this way:

```java
  @ChatButton(value = Poll.class, showWhen = WorkMode.VIEW, buttonText = "End Poll Now")
  public Result end(Poll p, Chat r, History h) {
    List<Answer> responses = h.getFromHistory(Answer.class, p.getId(), null, null);     // get all the answers that have been submitted to the poll
    
    // further code
    
```

# Addressing Checkers

The `AddressingChecker` is used to filter requests to your bot.  You can 

The default `InRoomAddressingChecker` is used to make sure that when you are chatting to a bot in a room (which may contain lots of other participants), you either mention the bot like so:

```
 @Bot help
```

or prefix the command with a slash, like so:

```
/help
```

You can override the default `AddressingChecker` bean with your your own implementation if you want to.

# Templating `@Work` Objects

When you send a POJO with the `@Work` annotation to a chat room, it will get rendered in the room in a platform-specific way.  As discussed before, for Symphony this means a [Freemarker](https://https://freemarker.apache.org) template.  On MS Teams this means an [Adaptive Card](https://adaptivecards.io).  The `SymphonyResponseHandler` or `TeamsResponseHandler` will load the appropriate template file based on the template name from the `WorkResponse` or `@ChatResponseBody` annotation.

If there is no template, one will be constructed.  This is done by reflecting on the fields in the `@Work` POJO's class, and building a static template from the class definition.    This provides you with a quick way to visualize or edit your data on the chat platfrom.

## Customizing Templates

You can take these generated templates, customize them and save them into your project.  This is useful if you want to follow a particular house style, want to provide instructions, or customize the layout of the form.

| Platform           | Location                         | Example                         |
|--------------------|----------------------------------|---------------------------------|
|Symphony            |`classpath:/templates/symphony/*.ftl` |  'template-name' -> `classpath:/templates/symphony/template-name.ftl` |

## Validation

By default, the templates constructed by the template generator allow for field validation using JSR-380 validation annotations.  For example, in the `NewClaim` class we have:

```java

@Work
public class NewClaim {

  @Min(0)             // stops people entering negative numbers
  Number amount;

```

When the `NewClaim` object is created after being passed back to your bot, validation occurs.  If the form fails validation, the form is sent back to the user and the fields that didn't validate are marked with errors.

## Supported Property Types

Out-of-the-box support exists for:

- `Collection`s (e.g. `List`s) of other objects in the workflow
- `String`s
- `Number`s
- `Instant`s
- Other objects in the workflow as nested properties.
- `Chat`:   the name of a room
- `Tag`s, including `HashTag`, `CashTag` and `ID` ( a hashtag build on a UUID, used for identifying objects uniquely in the workflow).
- `User`: the name of a user, rendered as an @-mention for viewing, or a user-picker when editing

## Extending Support To New Types

If you want to customize the way in which the Freemarker templates are created (for example, adding support for a new Java Class) you can do this by implementing `org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.TypeConverter`. There are plenty of examples of these in the source code.  `AbstractSimpleTypeConverter` is a good thing to extend for this.

## Limitations

At the moment, the forms are displayed by walking the class structure and inspecting the declared types (not instance types).  For that reason, polymorphism won't work: only the fields in declared types will be shown.



# Help Controller

The `HelpController` is provided so that any time a user asks `/help` in a room with your bot, a list of commands is given in a table.  This is generated dynamically based on the `@ChatRequest`-annotated controller methods.  

You can exclude a particular command from the help by setting `addToHelp=false` on the annotation, and you can tailor the description in the help by adding a `description` attribute to the annotation, like this (which is the controller method used to provide the help):

```java
  @ChatRequest(value = "help", description="Display this help page")
  public Response handleHelp(Addressable a, User u) {
```

