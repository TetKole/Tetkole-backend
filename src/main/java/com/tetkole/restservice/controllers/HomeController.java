package com.tetkole.restservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String helloWorld()
    {
        return "Bienvenue sur l'API Tètkole";
    }
}
