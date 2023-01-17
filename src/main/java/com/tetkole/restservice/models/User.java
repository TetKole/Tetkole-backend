package com.tetkole.restservice.models;

import jakarta.persistence.*;

@Entity
@Table(name= "user")
public class User {

    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Column(name = "mail")
    private String mail;
    @Column(name = "role")
    private String role;

    public User() {
    }

    public User(String firstName, String lastName, String password, String mail, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.mail = mail;
        this.role = role;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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