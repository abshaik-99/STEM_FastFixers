package com.example.demo.response;

public class LoginResponse {
    String message;
    Boolean status;
    private String redirectUrl;
    private String userRole;

    public String getRedirectUrl() {
        return redirectUrl;
    }
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    public String getUserRole() {
        return userRole;
    }
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Boolean isStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public LoginResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
        
    }

    public LoginResponse(String redirectUrl, String userRole) {
        this.redirectUrl = redirectUrl;
        this.userRole = userRole;
    }

    public LoginResponse(String message, Boolean status, String userRole) {
        this.message = message;
        this.status = status;
        this.userRole = userRole;
    }

    public LoginResponse() {
    }
    @Override
    public String toString() {
        return "LoginResponse [message=" + message + ", status=" + status + "]";
    }
    
    

    
}
