package com.tetkole.restservice.controllers;

import com.tetkole.restservice.models.User;
import com.tetkole.restservice.payload.request.LoginRequest;
import com.tetkole.restservice.payload.request.SignupRequest;
import com.tetkole.restservice.payload.response.JwtResponse;
import com.tetkole.restservice.repositories.UserRepository;
import com.tetkole.restservice.security.jwt.JwtUtils;
import com.tetkole.restservice.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping ()
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
    {
        if (userRepository.existsByMail(signUpRequest.getMail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Mail is already taken!");
        }

        userRepository.save(new User(
                signUpRequest.getFirstname(),
                signUpRequest.getLastname(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getMail(),
                "default" // TODO : Change role from default
        ));

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping ("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getMail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getUserId(),
                userDetails.getFirstname(),
                userDetails.getLastname(),
                userDetails.getMail()
        ));
    }

    @GetMapping ("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId)
    {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> // value is present
                ResponseEntity.ok(value)
        ).orElseGet(() ->
                ResponseEntity.notFound().build()
        );
    }

    @GetMapping ()
    public ResponseEntity<List<User>> getAll()
    {
        List<User> listUsers = userRepository.findAll();
        if(listUsers.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(listUsers,HttpStatus.OK);
    }

}