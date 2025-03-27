package com.example.hubspot_integration.service;

import com.example.hubspot_integration.dto.Token;
import com.example.hubspot_integration.exception.ExternalApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    private final WebClient.Builder webClientBuilder;
    private Token currentToken;

    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    // Método que gera a URL de autorização
    public ResponseEntity<String> getAuthorizationUrl() {
        try {
            String url = String.format(
                    "%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
                    authUrl, clientId, redirectUri, scopes);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            throw new ExternalApiException("Erro ao gerar URL de autorização" + e.getMessage(), e);
        }
    }

    // Método para trocar o código de autorização por um token de acesso
    public Mono<ResponseEntity<String>> exchangeCodeForToken(String code) {
        String requestBody = String.format(
                "grant_type=%s&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s",
                grantType, clientId, clientSecret, redirectUri, code);

        return webClientBuilder.baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        // Processa a resposta JSON e armazena o token
                        currentToken = extractTokenFromResponse(response);  // Aqui o currentToken é atualizado
                        return Mono.just(ResponseEntity.ok("Token recuperado com sucesso: " + currentToken.getAccessToken()));
                    } catch (Exception e) {
                        return Mono.error(new ExternalApiException("Erro ao processar a resposta do HubSpot", e));
                    }
                })
                .onErrorResume(e -> {
                    // Tratamento de erro na troca do token
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Erro ao recuperar o token: " + e.getMessage()));
                });
    }

    // Método para obter o token de acesso, verificando se está expirado
    public Mono<String> getAccessToken() {
        if (currentToken == null || currentToken.isExpired()) {
            return refreshAccessToken();  // Atualiza o token se estiver expirado ou nulo
        }
        return Mono.just(currentToken.getAccessToken());  // Retorna o token se não expirou
    }

    // Método para renovar o token de acesso usando o refresh token
    private Mono<String> refreshAccessToken() {
        if (currentToken == null || currentToken.getRefreshToken() == null) {
            return Mono.error(new ExternalApiException("Refresh token não disponível para renovação."));
        }

        String requestBody = String.format(
                "grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s",
                clientId, clientSecret, currentToken.getRefreshToken());

        return webClientBuilder.baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        // Atualiza o currentToken com o novo token de acesso
                        currentToken = extractTokenFromResponse(response);  // Atualiza o currentToken
                        return Mono.just(currentToken.getAccessToken());  // Retorna o novo accessToken como Mono<String>
                    } catch (JsonProcessingException e) {
                        return Mono.error(new ExternalApiException("Erro ao processar a resposta da renovação do token", e));
                    }
                });
    }


    // Método para processar a resposta JSON e retornar um novo Token
    private Token extractTokenFromResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        // Extraindo os valores do access_token, refresh_token e expires_in
        String accessToken = rootNode.path("access_token").asText();
        String refreshToken = rootNode.path("refresh_token").asText();
        int expiresIn = rootNode.path("expires_in").asInt();

        // Criando e retornando um novo objeto Token
        return new Token(accessToken, refreshToken, expiresIn);
    }
}
