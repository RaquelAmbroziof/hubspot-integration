package com.example.hubspot_integration.controller;

import com.example.hubspot_integration.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/oauth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorizationUrlGeneration() {
        return ResponseEntity.ok(authService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public ResponseEntity<Object> oauthCallbackProcessing(@RequestParam("code") String code) {
        return authService.exchangeCodeForToken(code);
    }

}
