package com.tetkole.restservice.security.services;

import java.util.Collection;
import java.util.Objects;

import com.tetkole.restservice.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private int userId;

    private String firstName;

    private String lastName;

    private String mail;

    @JsonIgnore
    private String password;

    public UserDetailsImpl(int userId, String firstName, String lastName, String mail, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.password = password;
    }

    public static UserDetailsImpl build(User user) {

        return new UserDetailsImpl(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getMail(),
                user.getPassword());
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

    public String getMail() {
        return getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.mail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(this.userId, user.getUserId());
    }
}