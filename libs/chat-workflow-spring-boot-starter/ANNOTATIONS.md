## Symphony Chat Workflow Annotations
This Document provides a detailed insight for the following Annotations: 

## @Work

The @Work annotation, which provides a human-readable name and instructions for people to perform a specific set of actions

Syntax :
   ```
   @Work(editable = false, instructions = "Sales Expense Claim Form", name = "Expense Claim")
   ```

Here editable says whether the bean is editable by Symphony user
Depending on the value of editable field, edit screen is available / hidden in UI
When editable = false , UI renders as : 

![Dependency Work Annotation1](images/Work-Annotation1.png)  

When editable is True , UI renders as : 

![Dependency Work Annotation2](images/Work-Annotation2.png) 
 
Click on the … to get the Edit button


## @Template

This Annotation conveys the chat-workflow-spring-boot-starter to use a template rather than build its 
Own


## @Exposed
This Annotation enables a method to be exposed to the user in a chat room

Syntax : 
```
@Exposed(description="Begin New Expense Claim")
  public static Claim open(StartClaim c) {
    Claim out = new Claim();
    out.description = c.description;
    out.amount = c.amount;
    return out;
  }
```
UI renders as : 

![Dependency Exposed Annotation1](images/Exposed-Annotation1.png)  

Explanation : 
When user types /open in the Room where this bot is added, Open() is called .
This Method requires an object of StartClaim class , Hence User is provided with a form to create object of StartClaim
The values passed to Description and amount are in turn passed to Claim class properties
2) @Exposed annotation can also be employed to verify the Workflow is applicable to specific room Only 

Syntax  :
```
@Exposed(description = "Approve an expense claim", rooms= {"Claim Approval Room"})
  public Claim approve() {
     ..
  }
```
If the room is not the one mentioned in rooms parameter , An exception will be thrown

3) @Exposed Annotation also has the option of enabling the Items that Bot can support can be available as Button format or by Command.

   @Exposed takes isButton parameter which is a Boolean with by default value "true". It works in the following way-

Syntax:
```
        @Exposed(description="Begin New Expense Claim" , isButton = false)
	public static Claim open() {
	..
	}

	@Exposed(description = "Approve an expense claim", isButton = false )
	public Claim approve() {
	..	
	}
	
	@Exposed(description = "New Full Expense Form") 
	public static Claim full() {
	..		
	}

```
This results in unavailability of the buttons for /approve and /open.

![Dependency Exposed Annotation2](images/Exposed-Annotation3.png)

4) Next is isMessage Parameter which is also a Boolean type with default of "true". This helps to allow the bot work by typing the commands in the chat room.

Syntax:
```
        @Exposed(description="Begin New Expense Claim")
	public static Claim open() {
	..
	}

	@Exposed(description = "Approve an expense claim" , isMessage = false)
	public Claim approve() {
	..	
	}
	
	@Exposed(description = "New Full Expense Form") 
	public static Claim full() {
	..		
	}
	
```
By adding like this /approve command no longer is available to talk with bot.

![Dependency Exposed Annotation3](images/Exposed-Annotation4.png)

5) @Exposed also have addToHelp Parameter which is a boolean by default true that allows the function to be visible in Help page or not.
```
        @Exposed(description="Begin New Expense Claim")
	public static Claim open() {
	..
	}

	@Exposed(description = "Approve an expense claim" , addToButton = false)
	public Claim approve() {
	..	
	}
	
	@Exposed(description = "New Full Expense Form") 
	public static Claim full() {
	..	
	}
```

By doing this /approve or approve button will no longer be visible in Help page.But we can still avail its feature by typing the command in chat.

![Dependency Exposed Annotation2](images/Exposed-Annotation5.png)

6) @Exposed Annotation governs the /help
   Typing in /help in any room fetches the help Items the bot can support in . Result may render buttons or suggestions depending on method definition  Ex :

![Dependency Exposed Annotation2](images/Exposed-Annotation2.png)


## @Display Annotation:
   This annotation is used to customize the workflow attribute properties to display in symphony bot application. We can override the name, show/hide the attribute.
    
   This is an optional annotation. If you haven't used this annotation then workflow attribute will be displayed with default syntax of camel case having space between words.
    ```
    Syntax:
    @Display(name = "Amount", visible = true)
    Number amt;
    ```
    
   Properties of @Display annotation:
    Name -> This property is used to override the name of the attribute to display on symphony bot. 
    Visible -> This property is used to show or hide the workflow attribute to be displayed on symphony bot. Default value is true.

## Examples of Display Annotation:
   --	Without Display Annotation:
   
        ```
        @Work(name = "Person Form", editable = true, instructions = "Person Template")
        public class Person {
        
            private String firstName;
            private String lastName;
            private String emailId;
        ```    

   ![Dependency Display Annotation1](images/Display-Annotation1.png) 


   --	Display Annotation with name and visible parameters:

        ```
        @Work(editable = true, instructions = "Person Form", name = "Person")
        public class Person {
        
            @Display(name = "First Name", visible = true)
            private String first;
            @Display(name = "Last Name", visible = true)
            private String last;
            @Display(name = "Email ID", visible = false)
            private String email;
        ```

   ![Dependency Display Annotation2](images/Display-Annotation2.png) 


   --	Display annotation with visible true/false:

        ```
        @Work(editable = true, instructions = "Person Form", name = "Person")
        public class Person {
        
            @Display(name = "First Name", visible = true)   // visible with true which is default
            private String first;
            @Display(name = "LastName", visible = false)  //This will not be shown of bot as visible is false
            private String last;
            @Display(name = "Email ID")  //The attribute name is overridden 
            private String email;
        ```

   ![Dependency Display Annotation2](images/Display-Annotation3.png) 

Finally here's a small table for quicker understanding or reference for the above discussed Annotations.

@Work is associated with Class.

@Exposed is associated with Method.

@Display is associated with the attributes we declare in class.

![Dependency Display Annotation2](images/QuickView.png)