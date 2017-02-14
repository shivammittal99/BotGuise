package com.codexter.botguise;

public class Message {
    private String mOpponentInitials;
    private String mUserInitials;
    private String mMessage;

    public Message(String opponentInitials, String message, String userInitials) {
        mOpponentInitials = opponentInitials;
        mMessage = message;
        mUserInitials = userInitials;
    }

    public String getOpponentInitials() {
        return mOpponentInitials;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getUserInitials() {
        return mUserInitials;
    }
}
