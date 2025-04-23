package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.PercentageResponse;
import com.tenpo.challenge.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Servicio responsable de obtener valores de porcentaje desde un servicio externo.
 * 
 * Este servicio proporciona métodos tanto síncronos como reactivos para obtener valores de porcentaje,
 * con mecanismos de reintento y almacenamiento en caché integrados para mejorar la fiabilidad y el rendimiento.
 * 
 * Los valores de porcentaje se almacenan en caché para reducir las llamadas al servicio externo y proporcionar
 * valores de respaldo cuando el servicio externo no está disponible. El servicio utiliza WebClient
 * para comunicarse con el servicio externo.
 * 
 * Propiedades de configuración:
 * - external-service.percentage.url: URL del servicio externo de porcentajes
 * - external-service.percentage.retry-max-attempts: Número máximo de intentos de reintento
 * - external-service.percentage.retry-delay: Retraso entre intentos de reintento en milisegundos
 * 
 * @see WebClient
 * @see Retryable
 * @see Cacheable
 * @see ExternalServiceException
 * @author Hugo Herrera
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageService {

    private final WebClient webClient;

    @Value("${external-service.percentage.url}")
    private String percentageServiceUrl;

    @Value("${external-service.percentage.retry-max-attempts}")
    private int maxRetryAttempts;

    @Value("${external-service.percentage.retry-delay}")
    private long retryDelay;

    /**
     * Obtiene el porcentaje desde el servicio externo con reintentos y caché
     * @return Porcentaje obtenido
     * @throws ExternalServiceException si falla después de reintentos
     */
    @Cacheable(value = "percentageCache", unless = "#result == null")
    @Retryable(
            value = {ExternalServiceException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public Double fetchPercentage() {
        log.info("Fetching percentage from external service");
        try {
            return webClient.get()
                    .uri(percentageServiceUrl)
                    .retrieve()
                    .bodyToMono(PercentageResponse.class)
                    .map(PercentageResponse::getPercentage)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching percentage: {}", e.getMessage());
            throw new ExternalServiceException("Error fetching percentage from external service", e);
        }
    }

    /**
     * Método de recuperación para el caso de fallo en los reintentos
     * @param e Excepción que provocó el fallo
     * @return null para indicar que se debe usar el valor en caché
     */
    @Recover
    public Double recoverFetchPercentage(ExternalServiceException e) {
        log.warn("All retries failed for fetchPercentage. Using cached value if available.");
        return null; // Retornar null para usar el valor en caché si está disponible
    }

    /**
     * Implementación reactiva para obtener el porcentaje
     * @return Mono con el porcentaje obtenido
     */
    public Mono<Double> fetchPercentageReactive() {
        log.info("Fetching percentage from external service (reactive)");
        return webClient.get()
                .uri(percentageServiceUrl)
                .retrieve()
                .bodyToMono(PercentageResponse.class)
                .map(PercentageResponse::getPercentage)
                .retryWhen(Retry.fixedDelay(maxRetryAttempts, Duration.ofMillis(retryDelay))
                        .filter(throwable -> throwable instanceof WebClientResponseException)
                        .doBeforeRetry(signal -> log.warn("Retrying after error: {}", signal.failure().getMessage())))
                .doOnError(e -> log.error("Error fetching percentage: {}", e.getMessage()));
    }
}