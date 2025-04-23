package com.tenpo.challenge.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateLimitConfigIntegrationTest {

    @Autowired
    private Bucket rateLimitBucket;

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