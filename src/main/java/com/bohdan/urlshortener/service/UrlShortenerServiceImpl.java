package com.bohdan.urlshortener.service;

import com.bohdan.urlshortener.UrlMapping;
import com.bohdan.urlshortener.dto.ShortenUrlRequest;
import com.bohdan.urlshortener.dto.UrlResponse;
import com.bohdan.urlshortener.repository.UrlMappingRepository;
import com.bohdan.urlshortener.util.Base62Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;

    public UrlShortenerServiceImpl(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }


    @Override
    @Transactional
    public UrlResponse shortenUrl(ShortenUrlRequest request, String baseUrl) {
        String shortCode;

        if (request.customAlias() != null && !request.customAlias().isBlank()) {
            String alias = request.customAlias().trim();
            if (urlMappingRepository.existsByShortCode(alias)) {
                throw new IllegalArgumentException("Alias already in use: " + alias);
            }
            shortCode = alias;
        } else {
            do {
                shortCode = Base62Util.generateRandomCode();
            } while (urlMappingRepository.existsByShortCode(shortCode));
        }

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = null;
        if (request.expirationDays() != null && request.expirationDays() > 0) {
            expiresAt = createdAt.plusDays(request.expirationDays());
        }

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(request.originalUrl());
        mapping.setShortCode(shortCode);
        mapping.setCreatedAt(createdAt);
        mapping.setExpiresAt(expiresAt);
        mapping.setClickCount(0L);

        urlMappingRepository.save(mapping);

        String fullShortUrl = baseUrl.endsWith("/") ? baseUrl + shortCode : baseUrl + "/" + shortCode;

        return new UrlResponse(
                request.originalUrl(),
                fullShortUrl,
                shortCode,
                createdAt,
                expiresAt
        );
    }

    @Override
    @Transactional
    public String getOriginalUrl(String shortCode) {
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("No mapping found for code: " + shortCode));

        if (urlMapping.getExpiresAt() != null && urlMapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short URL has expired");
        }

        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }
}