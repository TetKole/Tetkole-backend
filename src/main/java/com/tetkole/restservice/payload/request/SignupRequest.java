package com.tetkole.restservice.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignupRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    private String mail;
    @NotBlank
    private String password;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
}
