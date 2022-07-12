---
layout: default
title: Microsoft Teams Tutorial
nav_order: 5
nav-menu: true
---

# Microsoft Teams Tutorial
{: no_toc }

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
1. TOC
{:toc}
</details>

This is a short guide on how to get started with Microsoft Teams.  It is a bit of a minefield, but once you have your bot talking to the Spring Bot app, things are easy.  Hopefully this will give some clues as to how to get there.

## Create a Teams Developer Account

You can apply for a 90-day developer account that allows you to set up an instance of Microsoft Teams with up to 20 different users.   This allows you to work with colleagues to build and test a bot.

- You can apply for this [here](https://developer.microsoft.com/en-us/microsoft-365/dev-program)

![Developer Program](assets/images/teams/developer-program.png)

- Create some user accounts and distribute them to your friends.

![Accounts](assets/images/teams/teams-users.png)

- With your own account, you should be able to log into Microsoft Teams with your `onmicrosoft.com` email:

![Login](assets/images/teams/login.png)  

- From here, you can explore the delights of Microsoft Teams, and talk to any friends you invited.

## Create a Bot

- Access the Teams Developer Portal [here](https://dev.teams.microsoft.com/home).

- Then go to `Tools -> Bot Management -> New Bot` and create your bot.

![New Bot Screen](assets/images/teams/1.NewBot.png)

- Your bot will be given an **App ID** as shown below - you'll need this in your Spring Bot `application.yml` file later so keep a track of it.

![Bot App Id](assets/images/teams/2.CreatedBot.png)

## `ngrok` Port Forwarding

Since your bot is running on `localhost`, Microsoft Teams can't access it.  However, we can use `ngrok` to tunnel onto the public internet, running in HTTPS with a proper certificate.  From there, we can get Microsoft Teams to talk to it.  

- Install `ngrok` from [here](https://ngrok.com/)

-  Start ngrok so that it forward traffic to `http` port `8080` on your localhost, like so:  

`ngrok http 8080`

![ngrok](assets/images/teams/ngrok.png)

## Configuring the Bot

- Back in the developer portal, use the hostname provided by `ngrok` to specify the bot's endpoint.  Note this is the `ngrok` `HTTPS` address, but with `/api/messages` added to the end as a path.

![Bot Endpoint](assets/images/teams/4.ConfigureEndpoint.png)

-  Next, the bot needs a secret.  Set one of these up and keep it for later to add to your `appplication.yml` file.

![Bot Secret](assets/images/teams/5.ConfigureSecret.png)


## Create an App Manifest on The Teams Developer Portal

We are going to create an `App`, which is something that is available in the Teams App Store, and can be installed into your Teams chats.   An `App` can contain a number of `bot`s.  Here, we'll just create one. 

- Click `New App` in the Teams Developer Portal:

![Create App](assets/images/teams/7.CreateApp.png)

-  Enter Some Basic Details

![App Features](assets/images/teams/8.AppBasicInfo.png)
![App Features](assets/images/teams/23.Add-app.png)
![App Features](assets/images/teams/24.Add-app.png)

-  Associate The Bot With Your App

![Identify Bot](assets/images/teams/9.AppBot1.png)

![Identify Bot](assets/images/teams/10.AppBot2.png)

- Give The App Some permissions

![Teams Perms](assets/images/teams/11.AppBotPermissions.png)

- Download the created App Manifest to your local machine: 

![Download App](assets/images/teams/12.DownloadAppPackage.png)

## Add The App To Your Teams Installation

Before you can see the App in the Teams App Store, it needs to be approved (published) by your Teams administrators.

The Admin Console has a "Manage Apps" section [here](https://admin.teams.microsoft.com/policies/manage-apps).

-  Upload Your App

![Upload App](assets/images/teams/14.UploadApp.png)

- Change Publishing Status to "Published"

![Moving App To Published](assets/images/teams/15.PublishedApp.png)

## Configure Your App On Teams

Now, we're going to create a chat on Teams and add the App to it.

- Restart Teams on your local PC.  This ensures the App-Store cache is wiped.  

- Create a new chat with some friends to try the bot out

![New Chat](assets/images/teams/16.RestartTeamsNewChat.png)

- Head over to the App Store and find your app:

![App Store](assets/images/teams/17.TeamsAppStore.png)

- Add the App to your new chat:

![Adding The App 1](assets/images/teams/18.AddAppToChat1.png)

![Adding The App 2](assets/images/teams/19.AddAppToChat2.png)


## Configuring `application.yml`

Your bot will need an `application.yml` file containing the details of your teams installation:

```
teams:
  app:
    tennantId: 
  bot:
    MicrosoftAppId: 
    MicrosoftAppPassword: 
  storage:
    type: blob
    connection-string: 
    
spring:
  profiles:
    active: teams  # put teams, symphony if you want both
```

Let's go through these settings in turn:

### `tennantId`

Here I am getting the Tennant ID from my Teams Instance.  This is from the [Azure Admin Portal](https://portal.azure.com/)
![TennantID](/assets/images/teams/tennantId.png)

### `MicrosoftAppId`

This is the bot's AppId, which you can get from the Teams Developer Console:

![Bot App Id](assets/images/teams/2.CreatedBot.png)

### `MicrosoftAppPassword`

This is the bot client secret, that you generated earlier on.

### Storage Account

Unlike Symphony, Teams cannot store app-data inside chat messages, so we need to set up a separate Azure Blob storage.  
This allows the bot to keep track of the messages it has sent, and the conversations it is involved in.

- Head over to [https://portal.azure.com/](https://portal.azure.com/)

- Create The Blob Storage On This Screen

![New Blob Storage](assets/images/teams/blobStorage1.png)

- You should end up with something like this:

![Created Blob Storage](assets/images/teams/blobStorage2.png)

- Get the access key from this screen here and add it to `application.yml`:

![Access Key](assets/images/teams/blobStorage3.png)

### Start the bot

![Spring Startup](assets/images/teams/21.StartTheBot.png)

## Talk With Your Bot

For example, try asking your bot for help:

![Talk To The Bot](assets/images/teams/22.TryHelp.png)

From then on, you can follow one of the other tutorials for building a bot with custom functionality.