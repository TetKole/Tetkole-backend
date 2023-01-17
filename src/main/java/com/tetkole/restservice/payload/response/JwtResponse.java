package com.tetkole.restservice.payload.response;


public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int userId;
    private String firstName;
    private String lastName;
    private String mail;

    public JwtResponse(String token, int userId, String firstName, String lastName, String mail) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}