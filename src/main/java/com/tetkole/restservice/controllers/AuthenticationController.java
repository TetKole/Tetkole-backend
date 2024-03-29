package com.tetkole.restservice.controllers;

import com.tetkole.restservice.payload.request.*;
import com.tetkole.restservice.payload.response.LoginResponse;
import com.tetkole.restservice.payload.response.RegisterResponse;
import com.tetkole.restservice.payload.response.SuccessResponse;
import com.tetkole.restservice.repositories.MailRepository;
import com.tetkole.restservice.repositories.UserCorpusRoleRepository;
import com.tetkole.restservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping()
    public ResponseEntity<SuccessResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<SuccessResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(service.changePassword(request));
    }

    @PostMapping("/forceResetPassword")
    public ResponseEntity<SuccessResponse> forceResetPassword(@RequestBody ForcePasswordRequest request) {
        return ResponseEntity.ok(service.forceResetPassword(request));
    }

    @PostMapping("/addAdmin")
    public ResponseEntity<SuccessResponse> addModerator(@RequestBody RoleChangeRequest request) {
        return ResponseEntity.ok(service.addMAdmin(request));
    }

    @PostMapping("/addMailInscription")
    public ResponseEntity<SuccessResponse> addMailInscription(@RequestBody MailInscriptionRequest request) {
        return ResponseEntity.ok(service.addMailInscription(request));
    }

}