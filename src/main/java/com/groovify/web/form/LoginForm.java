package com.groovify.web.form;

/**
 * Represents the login form data submitted by a user through the web interface.
 * <p>
 * This simple POJO (Plain Old Java Object) is used by Spring MVC to bind form
 * input fields (username and password) to an object that can be validated and
 * passed to the service layer for authentication.
 */
public class LoginForm {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}