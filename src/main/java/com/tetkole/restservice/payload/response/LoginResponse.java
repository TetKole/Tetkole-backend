package com.tetkole.restservice.payload.response;

import java.util.HashMap;

public record LoginResponse(
    String token,
    Integer userId,
    String firstname,
    String lastname,
    String mail,
    String role,
    HashMap<Integer, String> roles
) { }

