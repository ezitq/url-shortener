package com.bohdan.urlshortener.dto;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, int statusCode, String error, String message, String path) {
}
