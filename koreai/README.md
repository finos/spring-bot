


    koreaiConnector
            .sendPost(koreai, user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(), action.getFormValues().get("selection").toString(),
                    (response) -> {
                        String message = String.format("<mention email=\"%s\"/> %s", user.getEmail(), response);
                        sendMessage(streamId, message);
                    });
