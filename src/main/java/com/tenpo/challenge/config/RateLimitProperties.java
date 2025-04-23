package com.tenpo.challenge.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.time.temporal.ChronoUnit;

@Getter
@Validated
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    @Min(1)
    private final int requestsPerMinute;

    private final ChronoUnit timeUnit;

    private final RateLimitStrategy strategy;

    @ConstructorBinding
    public RateLimitProperties(int requestsPerMinute, ChronoUnit timeUnit, RateLimitStrategy strategy) {
        this.requestsPerMinute = requestsPerMinute;
        this.timeUnit = timeUnit != null ? timeUnit : ChronoUnit.MINUTES;
        this.strategy = strategy != null ? strategy : RateLimitStrategy.GREEDY;
    }
}