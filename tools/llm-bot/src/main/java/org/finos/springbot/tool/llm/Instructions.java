package org.finos.springbot.tool.llm;

import java.util.List;

public class Instructions {
    private List<String> roomsUsedIn;
    private Integer messagesPerDayLimit;
    private Boolean onlyAdmin;
    private String template;
    private String instructions;

    public Instructions() {
    }

    public Instructions(List<String> roomsUsedIn, Integer messagesPerDayLimit, Boolean onlyAdmin, String template,
            String instructions) {
        this.roomsUsedIn = roomsUsedIn;
        this.messagesPerDayLimit = messagesPerDayLimit;
        this.onlyAdmin = onlyAdmin;
        this.template = template;
        this.instructions = instructions;
    }

    public List<String> getRoomsUsedIn() {
        return roomsUsedIn;
    }

    public void setRoomsUsedIn(List<String> roomsUsedIn) {
        this.roomsUsedIn = roomsUsedIn;
    }

    public Integer getMessagesPerDayLimit() {
        return messagesPerDayLimit;
    }

    public void setMessagesPerDayLimit(Integer messagesPerDayLimit) {
        this.messagesPerDayLimit = messagesPerDayLimit;
    }

    public Boolean getOnlyAdmin() {
        return onlyAdmin;
    }

    public void setOnlyAdmin(Boolean onlyAdmin) {
        this.onlyAdmin = onlyAdmin;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "Instructions{" +
                "roomsUsedIn=" + roomsUsedIn +
                ", messagesPerDayLimit=" + messagesPerDayLimit +
                ", onlyAdmin=" + onlyAdmin +
                ", template='" + template + '\'' +
                ", instructions='" + instructions + '\'' +
                '}';
    }
}
