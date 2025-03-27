package com.example.hubspot_integration.controller;

import com.example.hubspot_integration.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/contact")
public class ContactController {



    private final WebClient.Builder webClientBuilder;
    private final AuthService authService;

    public ContactController(WebClient.Builder webClientBuilder, AuthService authService) {
        this.webClientBuilder = webClientBuilder;
        this.authService = authService;
    }

    @PostMapping("/create-contact")
    public Mono<String> createContact(@RequestBody String contactData) {
        // URL para a criação do contato
        String url = "https://api.hubapi.com/crm/v3/objects/contacts";

        return authService.getAccessToken() // Verifica se o token está válido ou atualiza com o refresh token
                .flatMap(accessToken ->
                        webClientBuilder.baseUrl(url)
                                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)  // Passando o token no cabeçalho
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Definindo o Content-Type
                                .build()
                                .post()
                                .bodyValue(contactData) // Passando os dados do contato
                                .retrieve()
                                .bodyToMono(String.class) // Retorna a resposta como String
                );
}}
