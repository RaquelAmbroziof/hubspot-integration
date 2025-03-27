package com.example.hubspot_integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ContactService {

    @Value("${create.contact.url}")
    private String createContacturl;

    private final AuthService authService;
    private final WebClient.Builder webClientBuilder;

    public ContactService(AuthService authService, WebClient.Builder webClientBuilder) {
        this.authService = authService;
        this.webClientBuilder = webClientBuilder;
    }


    public Mono<String> createContact(String contactData) {
    return authService.getAccessToken()
            .flatMap(accessToken ->
                    webClientBuilder.baseUrl(createContacturl)
                            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .build()
                            .post()
                            .bodyValue(contactData)
                            .retrieve()
                            .bodyToMono(String.class)
            );
}

}
