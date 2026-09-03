package com.bohdan.urlshortener.service;

import com.bohdan.urlshortener.dto.ShortenUrlRequest;
import com.bohdan.urlshortener.dto.UrlResponse;

public interface UrlShortenerService {
    UrlResponse shortenUrl(ShortenUrlRequest request, String baseUrl) throws IllegalAccessException;
    String getOriginalUrl(String shortCode);
}
