package com.tetkole.restservice.payload.request;

public record ForcePasswordRequest(String mail, String newPassword) { }