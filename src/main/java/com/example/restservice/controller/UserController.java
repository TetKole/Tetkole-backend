package com.example.restservice.controller;

import com.example.restservice.model.User;
import com.example.restservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserRepository userRepository;

    @PostMapping ()
    public ResponseEntity<User> postInscription(@RequestBody User newUser)
    {
        User user = userRepository.save(new User(
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getPassword(),
                newUser.getMail(),
                newUser.getRole()
        ));
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping ("/login")
    public ResponseEntity<?> postLogin( @RequestParam(value = "password") String password, @RequestParam(value = "mail") String mail)
    {
        Optional<User> user = userRepository.findOneByMail(mail);

        return user.map((value) -> {
            if (value.getPassword().equals(password))
                return new ResponseEntity<>(value, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NO_CONTENT)
        );
    }

    @GetMapping ("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId)
    {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> // value is present
                new ResponseEntity<>(value, HttpStatus.OK)
        ).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NO_CONTENT)
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