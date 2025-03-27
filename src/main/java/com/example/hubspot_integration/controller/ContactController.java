package com.example.hubspot_integration.controller;

import com.example.hubspot_integration.service.ContactService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/contact")
public class ContactController {


    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/create")
    public Mono<String> createContact(@RequestBody String contactData) {
        return contactService.createContact(contactData);
}}
