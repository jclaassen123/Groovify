package com.groovify.web.form;

/**
 * Represents the login form data submitted by a user through the web interface.
 * <p>
 * This POJO (Plain Old Java Object) is used by Spring MVC to bind form
 * input fields (username and password) to an object that can be validated
 * and passed to the service layer for authentication.
 */
public class LoginForm {

    /** The username submitted by the user */
    private String username;

    /** The password submitted by the user */
    private String password;

    /** @return the username */
    public String getUsername() {
        return username;
    }

    /** @param username the username to set */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the password */
    public String getPassword() {
        return password;
    }

    /** @param password the password to set */
    public void setPassword(String password) {
        this.password = password;
    }
}
