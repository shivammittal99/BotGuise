package com.codexter.botguise;

public class ChatMessage {
    private String userInitials;
    private String message;
    private String messageType;

    public ChatMessage() {

    }

    public ChatMessage(String message, String userInitials, String messageType) {
        this.message = message;
        this.userInitials = userInitials;
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getUserInitials() {
        return userInitials;
    }

    public String getMessageType() {
        return messageType;
    }
}
