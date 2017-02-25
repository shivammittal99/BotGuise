package com.codexter.botguise;

public final class UserDetails {
    private String username;
    private String email;
    private Boolean introductionStatus;
    private String brain;

    public UserDetails() {
        this.username = null;
        this.email = null;
        this.introductionStatus = false;
    }

    public UserDetails(String username, String email, Boolean introductionStatus) {
        this.username = username;
        this.email = email;
        this.introductionStatus = introductionStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIntroductionStatus() {
        return introductionStatus;
    }

    public void setIntroductionStatus(Boolean introductionStatus) {
        this.introductionStatus = introductionStatus;
    }
}


