package com.tetkole.restservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/api")
    public String helloApi()
    {
        return "Bienvenue sur l'API Tètkole";
    }

    @GetMapping("/")
    public String hello()
    {
        return "don't fortget /api";
    }
}
