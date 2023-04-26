package com.tetkole.restservice.payload.request;

public record ChangePasswordRequest(String password, String newPassword) { }
