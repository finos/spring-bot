
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

## April 09, 2021

 - More logical package structure #148
 - OBO Functionality #35
 - Fix for #149
 - Fix for #176, devtool properties moved to parent chat-workflow dependency
 
## April 28, 2021 
 - Fix for #165, @Display annotation to override field name.
 - Fix for #163 Error Reporting Could Be Better.
 - Fix for KoreAI Health Reporting #180

## May 06, 2021
 - Fix for #171, Checkbox and Delete button Alignment
 - Fix for #163, Error Reporting Could Be Better with some update
 - Fix for #182, Symphony Chat Workflow Annotations Documentation

## Jun 24, 2021
 - Fix for #190, Chat Workflow documentation in the wrong place
 - Fix for #180, Kore AI Bot Health Unreliable
 - Fix for #171, Chat Workflow Delete/Checkbox alignment display
 - Fix for #141, Handle Primitives in Chat Workflow

## Jul 28, 2021
 - Fix for #183, camelcase issue for methods
 - Fix for #192, FormConverter doesn't use same object mapper as EntityJsonConverter
 - Migrated from InstantConverter to TimeConverter, which supports Zone and LocaldateTime
 
## Sep 9, 2021
 - Fix for #214, support for escape character

## Sep 13, 2021

Major release of chat workflow.  Most interfaces for this changed.  See migration guide for details.

 - Fix for #210, removed EntityJSON parameter from freemarker templating engine
 - Fix for #209, support for Dynamic Dropdowns, and room dropdowns
 - Fix for #178, proper error reporting inside symphony
 - Fix for #173, use of HandlerMapping pattern, removal of Workflow class.
 - Fix for #172, better Response class hierarchy
 - Fix for #168, spring scanning for @Work annotation classes
 - Fix for #164, help provision now generic, not part of symphony code
 - Fix for #113, trying to improve serialization of Entity JSON format
 - Fix for #196, mapping of EntityJson tags are Content objects.
 - Fix for #174, Addressable object hierarchy improved

# Sep 27, 2021

 - Fix for #221, Reminder Bot not starting
 - Fix for #223, help command not working by calling bot name

# Oct 13, 2021

 - Symphony API version upgraded to v20.13 by #238
 - Fix for #232, Un-marshalling of historical objects 

# Oct 20, 2021

 - Fix for #241, fixing mock issue
 - Fix for #239, fixing Integration test

# Nov 8, 2021

 - Minor bugfix - KoreAI - JAVA_HOME variable replaced with actual path from System Property
 
# Nov 29, 2021

 - Fix for #264 - For ChatRequest and ChatButton - room exclusion feature
 - Fix for #262 and #235 - For ChatRequest and ChatButton - rooms parameter
 - Fix for #260 - We can search user from different POD as well
 - Fix for #259 - Buttons are now visible on HelpPage
  
# Jan 27 2022

 - Beta version of Spring Bot
 - Microsoft Teams Connectivity
 - Dependency on Symphony BDK

# Mar 3 2022
 - Alpha version of SpringBot 9.x
 - #252 - Bean Validation working in Symphony and Teams
 - #253 - Tutorial For MS Teams
 - #242 - Code coverage properly checking related modules
 - #246 - Fixed User resolution in workflow resolvers
 - #248 - Teams, Symphony starters chosen based on active profiles
 - #220 - Platform agnostic workflow annotation
 - #284 - Teams to display buttons in view Mode
 - #245 - History/Conversations APIs working for teams
 - #235 - ChatButton visibility determined by room
 - #286 - Teams Requests not sent multiple times
 - #254 - Adpative Card templating for Teams
 - #244 - Conversations API for Teams
 - #247 - Updated Bots to work with teams (except Poll Bot)
 - #219 - MessageMLWriter now works with all Content objects
 
# Apr 27 2022 
 - Fixing Whitesource-reported vulnerabilities
 - #300 - Deploy tools/demos to maven central
 - #313 - Fixing Help Controller in Demo Bot not working in teams
 - Release of Beta 2.

# Jun 22 2022
 - #339 - MS Teams test failures on Windows machine fix
 - #350 - Rooms passed from Teams to Symphony or Vice-versa don't resolve correctly
 - Added rooms-bot demo
 - Removed room list from Todo bot
 - Spring version upgrade
 - Release 9.0.0

# Sept 20 2022
 - #363 Error message through in 9.x if you send IM message
 - #357 PresentationMLHandler NPE for Federation POD/Whatsapp users
 - #350 Rooms passed from Teams to Symphony or Vice-versa don't resolve correctly
 - #340 Running selfcontained spring boot jar (fat jar) fails on help command
 - #282 Fix The PollBot
 - Release 9.0.1

# May 31 2023

 - #390 Improved documentation for FINOS Active badge
 - #387 Fixed CVEs, rolled out FINOS Security Scanning
 - #386 Fixed Teams File State Storage
 - #380 Removing GPL-licensed dependency in Reminder Bot
 - #378 Added retry logic for teams
 - Release 9.0.2

 # Sept 11 2023
 - #405 Can we add SpringBot dependency on project which has parent dependency Sping-Boot:3x
 - #408 Handle exception for Teams 
 - #409 For Teams return conversation id on message sent
 - Release 9.0.3
