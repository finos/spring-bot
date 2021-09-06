1.  Change the dependency
2. @Work annotation - change NS, remove attributes
3. @Template:  
  - change to names not paths, 
  - move templates into templates/symphony dir.
  - extension .ftl
4. Change ID -> Tag
5. HashTag, CashTag
6. FormResponse -> WorkResponse
7. Remove Workflow
8. Create a method to start a workflow and return an empty form
9. Author -> User
10. Move all @Exposed methods to a Controller and annotate with @Controller
11. ResponseHandler -> ResponseHandlers
12. Add Header / Footer to template
13. Update templates: 
  - .formdata. -> .form. 
  - .workflow_001. -> .form.
  - .name -> .value