package com.example.restservice.controller;

import com.example.restservice.dao.UserJdbcDao;
import com.example.restservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserJdbcDao userDao;

    @PostMapping ()
    public int inscription(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mail") String mail,
            @RequestParam(value = "role") String role)
    {
        User newUser = new User(firstName, lastName, password, mail, role);
        return userDao.insert(newUser);
    }

    @PostMapping ("/login")
    public User login(
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mail") String mail)
    {
        return userDao.login(mail,password);
    }

    @GetMapping ("/{userId}")
    public User findById( @PathVariable int userId)
    {
        return userDao.findById(userId);
    }

    @GetMapping ()
    public List<User> findAll()
    {
        return userDao.findAll();
    }


}