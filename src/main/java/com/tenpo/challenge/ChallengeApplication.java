package com.tenpo.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import com.tenpo.challenge.config.RateLimitProperties;

/*
* Main class para la aplicacion Tenpo Challenge
* @author Hugo Herrera
* @version 1.0
*/

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableRetry
@EnableConfigurationProperties(RateLimitProperties.class)
public class ChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeApplication.class, args);
    }

}