package com.tetkole.restservice.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @Email
    private String mail;
    @NotBlank(message = "password is required")
    private String password;

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
}
