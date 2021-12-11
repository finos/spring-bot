1. Change the dependency to "symphony-chat-workflow-spring-boot-starter"
2. @Work annotation - change NS, remove attributes
  - New namespace - org.finos.symphony.toolkit.workflow.annotations.Work
  - name and instructions attributes -> No longer supported; can be put as H6 title in ftl
3. @Template:  
  - New namespace - org.finos.symphony.toolkit.workflow.annotations.Template
  - change to names not paths, 
  - move templates into templates/symphony dir.
  - extension .ftl
4. Change ID -> Tag
5. HashTag, CashTag
6. FormResponse -> WorkResponse
  - Instructions value from FormResponse -> Can be put as H6 title in ftl
  - Replace ButtonList entries with @ChatButton to handler methods 
7. Rooms -> Conversations
8. SymphonyRooms -> SymphonyConversations
9. Room -> Addressable or Chat
10. Author -> User
11. Room.id -> Chat.name (accordingly adjust code logic as well)
12. Author.id -> User.emailAddress (accordingly adjust code logic as well). As underlying object is still SymphonyUser, in ftl you can use user.id[0].value for getting userid
13. No more mention tag or messageML in MessageResponse -> Will get escaped & rendered as-is; Use template
14. Remove Workflow and it's object. No need create to custom workflow bean to add Work classes.
15. Create a method to start a workflow and return an empty form
16. Move all @Exposed methods to a Controller and annotate with @Controller
  - Safe to remove static keyword from method signature
  - @Exposed becomes @ChatRequest or @ChatButton
  - @Exposed with description attribute usually are mostly @ChatRequest candidates with value attribute set to method name
  - @Exposed with addToHelp=false and method name matching text of Button from any previous ButtonList -> are candidates for @ChatButton
17. ResponseHandler -> ResponseHandlers
18. Add Header / Footer to template
19. Update templates: 
  - .formdata. -> .form. 
  - .workflow_001. -> .form.
  - .name -> .value
20. No need of "ApplicationContextAware" to get Spring beans, you can get beans by using "@Autowired".
21. Create button action method and annotate with @ChatButton. 
22. For ObjectMapper - Check if properties set by you are already covered by existing values in EntityJsonConverter. If yes, then no need of repeating.
23. No need to wrap Work object in WorkResponse when returning -> Return Work object directly and annotated method with @ChatResponseBody
