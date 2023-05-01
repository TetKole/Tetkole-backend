package com.tetkole.restservice.service;


import com.tetkole.restservice.config.JwtService;
import com.tetkole.restservice.models.Mail;
import com.tetkole.restservice.models.Role;
import com.tetkole.restservice.models.User;
import com.tetkole.restservice.models.UserCorpusRole;
import com.tetkole.restservice.payload.request.*;
import com.tetkole.restservice.payload.response.LoginResponse;
import com.tetkole.restservice.payload.response.RegisterResponse;
import com.tetkole.restservice.payload.response.SuccessResponse;
import com.tetkole.restservice.repositories.MailRepository;
import com.tetkole.restservice.repositories.UserCorpusRoleRepository;
import com.tetkole.restservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserCorpusRoleRepository userCorpusRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public final MailRepository mailRepository;

    public SuccessResponse register(RegisterRequest request) {
        String firstname = request.firstname();
        String lastname = request.lastname();
        String email = request.mail();
        String password = request.password();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || userRepository.findOneByEmail(email).isPresent() ) {
            return new SuccessResponse(false);
        }

        Optional<Mail> mailDB = mailRepository.findByMail(email);

        if(mailDB.isEmpty()){
            return new SuccessResponse(false);
        }


        User user = User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        mailRepository.delete(mailDB.get());

        return new SuccessResponse(true);
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

        User user = userRepository.findOneByEmail(request.mail()).orElseThrow();

        String jwtToken = jwtService.generatedToken(user);

        List<UserCorpusRole> userCorpusRoles = userCorpusRoleRepository.findAllByUser(user);
        HashMap<Integer, String> roles = new HashMap<>();
        userCorpusRoles.forEach(userCorpusRole -> roles.put(userCorpusRole.getCorpus().getCorpusId(), userCorpusRole.getRole().toString()));

        return new LoginResponse(jwtToken, user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole().toString(), roles);
    }

    public SuccessResponse changePassword(ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        User user =  userRepository.findOneByEmail(username).get();

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);
            return new SuccessResponse(true);
        }

        return new SuccessResponse(false);
    }

    //if user is Admin OR moderator give a service to force change password
    public SuccessResponse forceResetPassword(ForcePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        User adminUser =  userRepository.findOneByEmail(username).get();
        Role roleOfRequest = adminUser.getRole();

        User user = userRepository.findOneByEmail(request.mail()).get();
        if (request.newPassword().length() > 0) {
            if (roleOfRequest == Role.ADMIN ){
                user.setPassword(passwordEncoder.encode(request.newPassword()));
                userRepository.save(user);
                return new SuccessResponse(true);
            }
        }
        return new SuccessResponse(false);
    }

    public SuccessResponse addMAdmin(RoleChangeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        User adminUser =  userRepository.findOneByEmail(username).get();
        Role roleOfRequest = adminUser.getRole();

        User user = userRepository.findOneByEmail(request.mail()).get();
        if (roleOfRequest == Role.ADMIN){
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            return new SuccessResponse(true);
        }
        return new SuccessResponse(false);
    }

    public SuccessResponse addMailInscription(MailInscriptionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        User adminUser =  userRepository.findOneByEmail(username).get();
        if (adminUser.getRole() != Role.ADMIN){
            return new SuccessResponse(false);
        }
        if(mailRepository.existsByMail(request.mail()) || userRepository.existsByEmail(request.mail())) {
            return new SuccessResponse(false);
        }
        Mail mail = new Mail(request.mail());
        mailRepository.save(mail);
        return new SuccessResponse(true);
    }
}
