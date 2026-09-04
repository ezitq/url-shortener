package com.bohdan.urlshortener.service;

import com.bohdan.urlshortener.UrlMapping;
import com.bohdan.urlshortener.dto.ShortenUrlRequest;
import com.bohdan.urlshortener.dto.UrlResponse;
import com.bohdan.urlshortener.exception.AliasAlreadyExistsException;
import com.bohdan.urlshortener.exception.UrlExpiredException;
import com.bohdan.urlshortener.exception.UrlNotFoundException;
import com.bohdan.urlshortener.repository.UrlMappingRepository;
import com.bohdan.urlshortener.util.Base62Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShortenerServiceImpl urlShortenerService;

    private final String BASE_URL = "http://localhost:8080";

    // ========== shortenUrl ==========

    @Test
    void shouldCreateShortUrlWithGeneratedCode() {
        // given
        ShortenUrlRequest request = new ShortenUrlRequest(
                "https://example.com/long-url",
                null,
                null
        );

        when(urlMappingRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        try (MockedStatic<Base62Util> mocked = mockStatic(Base62Util.class)) {
            mocked.when(Base62Util::generateRandomCode).thenReturn("abc123");

            // when
            UrlResponse response = urlShortenerService.shortenUrl(request, BASE_URL);

            // then
            assertThat(response.shortCode()).isEqualTo("abc123");
            assertThat(response.shortUrl()).isEqualTo("http://localhost:8080/abc123");
            assertThat(response.originalUrl()).isEqualTo("https://example.com/long-url");
            assertThat(response.expiresAt()).isNull();

            ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);
            verify(urlMappingRepository).save(captor.capture());

            UrlMapping saved = captor.getValue();
            assertThat(saved.getShortCode()).isEqualTo("abc123");
            assertThat(saved.getClickCount()).isZero();
        }
    }

    @Test
    void shouldCreateShortUrlWithCustomAlias() {
        ShortenUrlRequest request = new ShortenUrlRequest(
                "https://example.com",
                "my-link",
                30L
        );

        when(urlMappingRepository.existsByShortCode("my-link")).thenReturn(false);
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        UrlResponse response = urlShortenerService.shortenUrl(request, BASE_URL);

        assertThat(response.shortCode()).isEqualTo("my-link");
        assertThat(response.expiresAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenCustomAliasAlreadyExists() {
        ShortenUrlRequest request = new ShortenUrlRequest(
                "https://example.com",
                "taken",
                null
        );

        when(urlMappingRepository.existsByShortCode("taken")).thenReturn(true);

        assertThatThrownBy(() -> urlShortenerService.shortenUrl(request, BASE_URL))
                .isInstanceOf(AliasAlreadyExistsException.class)
                .hasMessageContaining("taken");
    }

    // ========== getOriginalUrl ==========

    @Test
    void shouldReturnOriginalUrlAndIncrementClickCount() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setOriginalUrl("https://example.com");
        mapping.setClickCount(5L);
        mapping.setExpiresAt(null);

        when(urlMappingRepository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));
        when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = urlShortenerService.getOriginalUrl("abc123");

        assertThat(result).isEqualTo("https://example.com");
        assertThat(mapping.getClickCount()).isEqualTo(6L);
        verify(urlMappingRepository).save(mapping);
    }

    @Test
    void shouldThrowWhenUrlNotFound() {
        when(urlMappingRepository.findByShortCode("not-exist")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlShortenerService.getOriginalUrl("not-exist"))
                .isInstanceOf(UrlNotFoundException.class);
    }

    @Test
    void shouldThrowWhenUrlExpired() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("expired");
        mapping.setOriginalUrl("https://example.com");
        mapping.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(urlMappingRepository.findByShortCode("expired")).thenReturn(Optional.of(mapping));

        assertThatThrownBy(() -> urlShortenerService.getOriginalUrl("expired"))
                .isInstanceOf(UrlExpiredException.class);
    }
}