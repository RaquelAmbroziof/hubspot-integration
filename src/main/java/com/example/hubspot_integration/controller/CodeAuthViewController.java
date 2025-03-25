package com.example.hubspot_integration.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class CodeAuthViewController {

    @RequestMapping("/access-code")
    public String showAuthCodePage() {
        return "auth-code";
    }
}
