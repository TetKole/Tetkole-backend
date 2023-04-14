package com.tetkole.restservice.payload.request;

public record ForcePasswordRequest(String adminMail, String mail, String newPassword) { }