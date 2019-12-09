
## 21 November 2019

 - Added Swagger Code for Symphony Elements

## 7/8 November 2019

 - Improved error reporting (Symphony returned JSON is included in the exception) for both Jersey and CXF
 - Rebuilt against Symphony 1.55 API
 - Fixed Jackson vulnerability: 
   - This required advancing to Spring Boot 2.1.10.RELEASE
   - Advancing Jackson to 2.9.10
   - Required minor changes to health / metrics tests
   - Simplified bindings dependencies to just annotations.
 - Releasing version: 4.55.1 
 - Changed demo bot application.yml so that it starts on develop pod.
 
