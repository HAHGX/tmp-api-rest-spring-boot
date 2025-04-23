package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.CalculationRequest;
import com.tenpo.challenge.dto.CalculationResponse;
import com.tenpo.challenge.service.CalculationService;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenpo.challenge.exception.RateLimitExceededException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/calculation")
@RequiredArgsConstructor
@Slf4j
public class CalculationController {

    private final CalculationService calculationService;
    private final Bucket rateLimitBucket;

    /**
     * Endpoint para realizar el cálculo (enfoque sincrónico)
     * @param request Los números a calcular
     * @return Resultado del cálculo
     * @throws RateLimitExceededException si se excede el límite de solicitudes permitidas
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request) {
        // Verificar límite de tasa
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Se ha excedido el límite de solicitudes permitidas. Por favor, intente más tarde.");
        }

        log.info("Processing calculation request: {}", request);
        CalculationResponse response = calculationService.calculate(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para realizar el cálculo (enfoque reactivo)
     * @param request Los números a calcular
     * @return Resultado del cálculo de forma reactiva
     */
    @PostMapping(value = "/reactive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CalculationResponse>> calculateReactive(@Valid @RequestBody CalculationRequest request) {
        // Verificar límite de tasa
        if (!rateLimitBucket.tryConsume(1)) {
            return Mono.error(new RateLimitExceededException("Se ha excedido el límite de solicitudes permitidas. Por favor, intente más tarde."));
        }

        log.info("Processing reactive calculation request: {}", request);
        return calculationService.calculateReactive(request)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error processing reactive calculation: {}", e.getMessage()));
    }
}