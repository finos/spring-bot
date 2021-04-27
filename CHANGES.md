
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

## 16th March 2020

 - Continued fix of #33
 
## 9th April 2020

 - Working version of the `symphony-maven-build-reporter`
 - First version of `symphony-shared-stream`
 
## 26th May 2020

 - Improved tests and reliability of `symphony-shared-stream`
 
## 2nd September 2020

 - Added FINOS materials.

## 9th September 2020

 - Updated to Symphony Agent 1.59.0 API (this includes breaking changes to old APIs, sadly)
 - Updated `swagger-codegen-maven-plugin` to version 3.0.21, removing stutter in method names (e.g. `isIsPodConnectivity()`).  
 - These are both breaking changes, forcing an update of the major version number.
 
## 20th October 2020

 - Tutorial and major functionality updates for `chat-workflow` module.
 - New demo bots:  `poll-bot`, `claim-bot`, `todo-bot`.

## 28th October 2020
 
 - Moved to `org.finos.symphony.toolkit` groupId.
 - Renaming modules so that they match directories/spring-starter naming structure.

## 29th October 2020

 - Renamed packages to match `org.finos.symphony.toolkit`
 - Divided tests into integration tests, unit tests
 - Minor fixes to chat workflow 
 
## 25th November 2020

 - Added TeamCity Integration

## 16th December 2020
 
 - KoreAI Mult-Tennant Support
 - Fixing missing links
 - Better factoring of Shared Streams
 - Minor fixes to chat workflow

## 7th January 2021

 - Some improvements to POM struture to close whitesource issues
 - Bumped spring dependencies
 
## 13th January 2021

 - Upgraded to JUnit 5.
 - Fixed Code Coverage Report.
 - Fixed KoreAI Issues (#122)
 - Fixed Chat Workflow Bug (#124)
 
## 2nd February 2021

 - Plugable Freemarker templating for Chat workflow (#130)
 - MessageML support in KoreAI Formatter (#125)
 - Bot Welcome Messages (#119)
 - Improvement for Content Class in Chat Workflow (#109)
 - Fixing Whitesource security issue (#88)
 
## 22nd February 2021

 - Fixing Shared Stream issue, removed multiple algorithms from shared stream (#128)

## March 2021

 - Fixing issue #123 to allow shared-stream bots to start even without a listener.
 - Fix for #140, using dedicated object mapper for bundles.
 - Configurable timeout for shared-stream HttpMulticaster (#138)
 - Added RSS Bot
 - Fixed #146, #144, #142

## April 2021

 - More logical package structure #148
 - OBO Functionality #35
 - Fix for #149
 
## May 2021 
 - Fix for #165, @Display annotation to choose how fields are shown.
 - Fix for #163, Error Reporting Could Be Better.
 - Fix for #176 devtool properties moved to parent chat-workflow dependency
