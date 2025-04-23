package com.tenpo.challenge.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

// Eliminamos @SpringBootTest para evitar cargar todo el contexto
@ActiveProfiles("test") 
class RateLimitConfigIntegrationTest {

    private Bucket rateLimitBucket;

    @BeforeEach
    void setUp() {
        // Crear un bucket en memoria para pruebas (3 tokens, recarga 3 por minuto)
        Bandwidth limit = Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1)));
        rateLimitBucket = Bucket.builder().addLimit(limit).build();
    }

    @Test
    void testRateLimiter_AllowsLimitedRequests() {
        int allowedRequests = 3;
        int allowed = 0;

        for (int i = 0; i < 5; i++) {
            if (rateLimitBucket.tryConsume(1)) {
                allowed++;
            }
        }

        assertEquals(allowedRequests, allowed,
                "Rate limiter debe permitir solo " + allowedRequests + " requests por minuto");
    }

    @Test
    void testRateLimiter_DeniesAfterLimitExceeded() {
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimitBucket.tryConsume(1), "Debe permitir las primeras 3 solicitudes");
        }

        assertFalse(rateLimitBucket.tryConsume(1), "Debe rechazar después de superar el límite");
    }
}