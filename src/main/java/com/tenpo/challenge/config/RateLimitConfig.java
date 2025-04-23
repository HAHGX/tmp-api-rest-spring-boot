package com.tenpo.challenge.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RateLimitConfig {

    private final RateLimitProperties rateLimitProperties;

    public RateLimitConfig(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }

    @Bean
    public Bucket rateLimitBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(rateLimitProperties.getRequestsPerMinute())
                .refillGreedy(rateLimitProperties.getRequestsPerMinute(), 
                        Duration.of(1, rateLimitProperties.getTimeUnit()))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

@Getter
@Validated
@ConfigurationProperties(prefix = "rate-limit")
class RateLimitProperties {

    @ConstructorBinding
    public RateLimitProperties(
            @Min(1) int requestsPerMinute, 
            ChronoUnit timeUnit, 
            RateLimitStrategy strategy) {
        this.requestsPerMinute = requestsPerMinute;
        this.timeUnit = timeUnit != null ? timeUnit : ChronoUnit.MINUTES;
        this.strategy = strategy != null ? strategy : RateLimitStrategy.GREEDY;
    }

    @Min(1)
    private final int requestsPerMinute;
    
    private final ChronoUnit timeUnit;
    
    private final RateLimitStrategy strategy;
}

enum RateLimitStrategy {
    GREEDY,
    INTERVAL
}
