package com.skillproof.auth;

public class AuthResponse {
    private String message;
    private UserSummaryDto user;
    private String redirectUrl;

    public AuthResponse() {}

    public AuthResponse(String message, UserSummaryDto user, String redirectUrl) {
        this.message = message;
        this.user = user;
        this.redirectUrl = redirectUrl;
    }

    public static AuthResponse of(String message, UserSummaryDto user, String redirectUrl) {
        return new AuthResponse(message, user, redirectUrl);
    }

    public static AuthResponse of(String message, String redirectUrl) {
        return new AuthResponse(message, null, redirectUrl);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
