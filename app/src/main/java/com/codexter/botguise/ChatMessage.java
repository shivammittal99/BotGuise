package com.codexter.botguise;

public class ChatMessage {
    private String userInitials;
    private String message;
    private int actual;
    private int guess;

    public ChatMessage() {
    }

    public ChatMessage(String message, String userInitials, int actual) {
        this.message = message;
        this.userInitials = userInitials;
        this.actual = actual;
        this.guess = 0;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserInitials() {
        return userInitials;
    }

    public void setUserInitials(String userInitials) {
        this.userInitials = userInitials;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public int getGuess() {
        return guess;
    }

    public void setGuess(int guess) {
        this.guess = guess;
    }
}
