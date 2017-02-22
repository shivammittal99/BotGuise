package com.codexter.botguise;

public class TrainMessage {
    private boolean fromBot;
    private String message;

    public TrainMessage(boolean fromBot, String message) {
        this.message = message;
        this.fromBot = fromBot;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getFromBot() {
        return fromBot;
    }
}
