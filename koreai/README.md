

## Configuration

### URL 

The URL is composed of the host that KoreAI is running on, plus the bot id, looking something like this:

`https://<kore ai hostname>/chatbot/hooks/st-blah-blah-blah-blah-blahblah`

### JWT Token

Follow instructions [here](https://developer.kore.ai/docs/bots/api-guide/apis/) to generate a JWT token from your application secret.

Inside your `application.yml` file, you will need to add:

```
symphony:
  koreai:
    jwt:  <put your generated jwt token here
    url: https://<kore ai hostname>/chatbot/hooks/st-blah-blah-blah-blah-blahbla





    koreaiConnector
            .sendPost(koreai, user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(), action.getFormValues().get("selection").toString(),
                    (response) -> {
                        String message = String.format("<mention email=\"%s\"/> %s", user.getEmail(), response);
                        sendMessage(streamId, message);
                    });
