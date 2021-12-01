1. Change the dependency to "symphony-chat-workflow-spring-boot-starter"
2. @Work annotation - change NS, remove attributes
3. @Template:  
  - change to names not paths, 
  - move templates into templates/symphony dir.
  - extension .ftl
4. Change ID -> Tag
5. HashTag, CashTag
6. FormResponse -> WorkResponse
7. Rooms -> Conversations
8. Room -> Addressable
9. Author -> User
10. Remove Workflow and it's object. No need create to custom workflow bean to add Work classes.
11. Create a method to start a workflow and return an empty form
12. Move all @Exposed methods to a Controller and annotate with @Controller
  - @Exposed becomes @ChatRequest
13. ResponseHandler -> ResponseHandlers
14. Add Header / Footer to template
15. Update templates: 
  - .formdata. -> .form. 
  - .workflow_001. -> .form.
  - .name -> .value
16. No need of "ApplicationContextAware" to get Spring beans, you can get beans by using "@Autowired".
17. Create button action method and annotate with @ChatButton. 
