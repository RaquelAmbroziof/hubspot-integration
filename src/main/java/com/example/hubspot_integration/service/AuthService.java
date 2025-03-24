package com.example.hubspot_integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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

    public String getAuthorizationUrl() {
        return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                authUrl, clientId, redirectUri, scopes);
    }
}
