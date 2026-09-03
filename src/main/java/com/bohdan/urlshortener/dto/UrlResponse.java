package com.bohdan.urlshortener.dto;

import java.time.LocalDateTime;

public record UrlResponse(String originalUrl
        ,String shortUrl
        ,String shortCode
        ,LocalDateTime createdAt
        ,LocalDateTime expiresAt) {

}
