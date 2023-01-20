package com.tetkole.restservice.payload.response;


public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private int userId;
    private String firstname;
    private String lastname;
    private String mail;

    public JwtResponse(String token, int userId, String firstname, String lastname, String mail) {
        this.token = token;
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}