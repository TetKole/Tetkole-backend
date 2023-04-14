package com.tetkole.restservice.payload.response;

public record LoginResponse(
    String token,
    Integer userId,
    String firstname,
    String lastname,
    String mail,

    String role
) { }

