package com.tetkole.restservice.payload.request;

public record RegisterRequest(
    String firstname,
    String lastname,
    String mail,
    String password,
    String role
) { }
