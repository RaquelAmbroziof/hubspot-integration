package com.example.hubspot_integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String refreshToken;
    private Integer expiresIn;
    private String accessToken;
}
