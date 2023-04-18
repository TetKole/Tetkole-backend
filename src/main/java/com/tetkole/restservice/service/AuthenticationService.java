package com.tetkole.restservice.service;


import com.tetkole.restservice.config.JwtService;
import com.tetkole.restservice.models.Role;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import com.tetkole.restservice.payload.request.ChangePasswordRequest;
import com.tetkole.restservice.payload.request.ForcePasswordRequest;
import com.tetkole.restservice.payload.request.LoginRequest;
import com.tetkole.restservice.payload.request.RegisterRequest;
import com.tetkole.restservice.payload.response.LoginResponse;
import com.tetkole.restservice.payload.response.RegisterResponse;
import com.tetkole.restservice.payload.response.SuccessResponse;
import com.tetkole.restservice.repositories.UserCorpusRoleRepository;
import com.tetkole.restservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final UserCorpusRoleRepository userCorpusRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        String firstname = request.firstname();
        String lastname = request.lastname();
        String email = request.mail();
        String password = request.password();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || repository.findOneByEmail(email).isPresent() ) {
            return new RegisterResponse(false);
        }


        User user = User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        System.out.println(user.getEmail());

        repository.save(user);

        return new RegisterResponse(true);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.mail(),
                        request.password()
                )
        );

        // if the function reach this point,
        // it means that the authentication works

        User user = repository.findOneByEmail(request.mail()).orElseThrow();

        String jwtToken = jwtService.generatedToken(user);

        List<UserCorpusRole> userCorpusRoles = userCorpusRoleRepository.findAllByUser(user);
        HashMap<Integer, String> roles = new HashMap<>();
        userCorpusRoles.forEach(userCorpusRole -> roles.put(userCorpusRole.getCorpus().getCorpusId(), userCorpusRole.getRole().toString()));

        return new LoginResponse(jwtToken, user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole().toString(), roles);
    }

    public SuccessResponse changePassword(ChangePasswordRequest request) {
        User user = repository.findOneByEmail(request.mail()).orElseThrow();

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
            repository.save(user);
            return new SuccessResponse(true);
        }

        return new SuccessResponse(false);
    }

    //if user is Admin OR moderator give a service to force change password
    public SuccessResponse forceResetPassword(ForcePasswordRequest request) {
        User user = repository.findOneByEmail(request.mail()).orElseThrow();
        User admin = repository.findOneByEmail(request.adminMail()).orElseThrow();
        if (request.newPassword().length() > 0) {
            if (admin.getRole() == Role.ADMIN || admin.getRole() == Role.MODERATOR){
                user.setPassword(passwordEncoder.encode(request.newPassword()));
                repository.save(user);
                return new SuccessResponse(true);
            }
        }
        return new SuccessResponse(false);
    }
}
