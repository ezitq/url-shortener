package com.bohdan.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(
        @NotBlank(message = "Original URL is required")
        @URL(message = "Invalid URL format")
        String originalUrl,

        @Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", message = "Custom alias must be 3-30 characters (letters, numbers, '-', '_')")
        String customAlias,

        @Positive(message = "Expiration days must be positive")
        Long expirationDays
) {}
