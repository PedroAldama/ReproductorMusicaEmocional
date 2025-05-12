package com.reproductor.music.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    @GetMapping
    public String saludo(){
        return "Saludo";
    }


}
