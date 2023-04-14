package com.tetkole.restservice.payload.request;

public record ChangePasswordRequest(String mail, String password, String newPassword) { }
