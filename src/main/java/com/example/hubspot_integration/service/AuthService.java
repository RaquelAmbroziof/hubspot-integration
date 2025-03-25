package com.example.hubspot_integration.service;

import com.example.hubspot_integration.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class AuthService {

   @Value("${auth.url}")
   private String authUrl;

   @Value("${client.id}")
   private String clientId;

   @Value("${redirect.uri}")
   private String redirectUri;

   @Value("${scopes}")
   private String scopes;

    @Value("${token.url}")
    private String tokenUrl;

    @Value("${client.secret}")
    private String clientSecret;

    @Value("${grant.type}")
    private String grantType;

    public String getAuthorizationUrl() {
        try {
        return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                authUrl, clientId, redirectUri, scopes);
        } catch (Exception e) {
            throw new ExternalApiException("Erro ao gerar URL de autorização" + e.getMessage(), e);
        }
    }

    public ResponseEntity<Object> exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String requestBody = String.format(
                    "grant_type=%s&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s",
                    grantType, clientId, clientSecret, redirectUri, code);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok().body(response.getBody());
            } else {
                throw new ExternalApiException("Erro ao recuperar o token: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new ExternalApiException("Falha na comunicação com o HubSpot: " + e.getMessage(), e);
        }

        }
}
