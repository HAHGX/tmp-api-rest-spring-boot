package com.tenpo.challenge.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    private final RateLimitProperties rateLimitProperties;

    public RateLimitConfig(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    @Bean
    public Bucket rateLimitBucket() {
        Bandwidth limit;

        switch (rateLimitProperties.getStrategy()) {
            case INTERVAL -> limit = Bandwidth.builder()
                    .capacity(rateLimitProperties.getRequestsPerMinute())
                    .refillIntervally(rateLimitProperties.getRequestsPerMinute(),
                            Duration.of(1, rateLimitProperties.getTimeUnit()))
                    .build();
            case GREEDY -> limit = Bandwidth.builder()
                    .capacity(rateLimitProperties.getRequestsPerMinute())
                    .refillGreedy(rateLimitProperties.getRequestsPerMinute(),
                            Duration.of(1, rateLimitProperties.getTimeUnit()))
                    .build();
            default -> throw new IllegalArgumentException("Unknown rate limit strategy");
        }

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}