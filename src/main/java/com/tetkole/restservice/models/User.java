package com.tetkole.restservice.models;

import jakarta.persistence.*;

@Entity
@Table(name= "user")
public class User {

    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "firstname", nullable=false)
    private String firstname;
    @Column(name = "lastname", nullable=false)
    private String lastname;
    @Column(name = "password", nullable=false)
    private String password;
    @Column(name = "mail", nullable=false)
    private String mail;
    @Column(name = "role", nullable=false)
    private String role;

    public User() {
    }

    public User(String firstname, String lastname, String password, String mail, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.mail = mail;
        this.role = role;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }
}