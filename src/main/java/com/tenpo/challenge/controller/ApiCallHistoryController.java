package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.ApiCallHistoryResponse;
import com.tenpo.challenge.service.ApiCallHistoryService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tenpo.challenge.exception.RateLimitExceededException;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Slf4j
public class ApiCallHistoryController {

    private final ApiCallHistoryService apiCallHistoryService;
    private final Bucket rateLimitBucket;

    /**
     * Endpoint para consultar el historial de llamadas a la API de forma paginada
     * @param pageable Configuración de paginación con ordenación por defecto por fecha de creación descendente
     * @return Historial de llamadas paginado
     * @throws RateLimitExceededException si se excede el límite de solicitudes permitidas
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ApiCallHistoryResponse>> getHistory(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // Verificar límite de tasa
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Se ha excedido el límite de solicitudes permitidas. Por favor, intente más tarde.");
        }

        log.info("Getting API call history, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ApiCallHistoryResponse> history = apiCallHistoryService.findAllPaginated(pageable);
        return ResponseEntity.ok(history);
    }
}