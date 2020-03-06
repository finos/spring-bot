
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

## 27 January 2020

 - Rebuilt against Symphony 1.55.4 API
 - Added FIX JSON and Entity JSON Support via Jackson Modules
 - Added StreamIDHelp class to make streamIDs URL-safe automatically
 - Allowed ApiWrappers to be configured in sympony-api-spring-boot-starter
 
## 29 January 2020

 - Fixed Security issue with Jackson reported by Github.

## 28 February 2020
 
 - Fixed Enum Bug in Entity Json 
 - #30, allowed multiple proxies per connection
 
## 6 March 2020

 - Fixed #33, making JSON parsing lenient in the face of additions on Symphony's side.