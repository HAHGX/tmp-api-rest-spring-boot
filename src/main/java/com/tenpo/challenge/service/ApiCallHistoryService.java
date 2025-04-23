package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.ApiCallHistoryResponse;
import com.tenpo.challenge.model.ApiCallHistory;
import com.tenpo.challenge.repository.ApiCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio para gestionar el registro histórico de llamadas a la API.
 * 
 * Este servicio proporciona funcionalidades para guardar de manera asíncrona
 * registros de llamadas a la API y recuperar estos registros de manera paginada.
 * El guardado asíncrono asegura que los fallos en el registro no afecten
 * al servicio principal de la aplicación.
 * 
 * @author Hugo Herrera
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCallHistoryService {

    private final ApiCallHistoryRepository apiCallHistoryRepository;
    
    // Lista de campos por los que se permite ordenar
    private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(
            Arrays.asList("id", "endpoint", "method", "statusCode", "createdAt"));

    /**
     * Guarda de forma asíncrona un registro de llamada a la API
     * @param apiCallHistory registro a guardar
     */
    @Async
    public void saveAsync(ApiCallHistory apiCallHistory) {
        try {
            apiCallHistoryRepository.save(apiCallHistory);
            log.debug("API call history record saved successfully: {}", apiCallHistory);
        } catch (Exception e) {
            // La falla en el registro no debe afectar al servicio principal
            log.error("Error saving API call history record: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene el historial de llamadas a la API paginado
     * @param pageable configuración de paginación
     * @return página con historial de llamadas
     */
    public Page<ApiCallHistoryResponse> findAllPaginated(Pageable pageable) {
        // Crear una ordenación segura filtrada
        Pageable safePageable = createSafePageable(pageable);
        
        // Si no hay ordenación, utilizar el método existente con ordenación por defecto
        if (safePageable.getSort().isEmpty()) {
            return apiCallHistoryRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(
                    safePageable.getPageNumber(), 
                    safePageable.getPageSize()))
                    .map(this::mapToResponse);
        }
        
        // Si hay ordenación válida, usar el método findAll con la ordenación filtrada
        return apiCallHistoryRepository.findAll(safePageable)
                .map(this::mapToResponse);
    }

    /**
     * Crea un objeto Pageable seguro que solo incluye campos de ordenación permitidos
     * @param pageable configuración original de paginación
     * @return objeto Pageable filtrado y seguro
     */
    private Pageable createSafePageable(Pageable pageable) {
        // Si no hay ordenación, retornar el mismo pageable
        if (pageable.getSort().isEmpty()) {
            return pageable;
        }

        List<Sort.Order> validOrders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            if (ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                validOrders.add(order);
            } else {
                log.warn("Ignorando campo de ordenación no permitido: {}", order.getProperty());
            }
        });
        
        // Si no hay ordenaciones válidas, retornar un pageable simple sin ordenación
        if (validOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }
        
        // Crear un nuevo pageable con las ordenaciones válidas
        return PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                Sort.by(validOrders));
    }

    /**
     * Mapea la entidad ApiCallHistory a un objeto de respuesta
     * @param history entidad a mapear
     * @return objeto de respuesta
     */
    private ApiCallHistoryResponse mapToResponse(ApiCallHistory history) {
        return ApiCallHistoryResponse.builder()
                .id(history.getId())
                .endpoint(history.getEndpoint())
                .method(history.getMethod())
                .requestParams(history.getRequestParams())
                .responseBody(history.getResponseBody())
                .statusCode(history.getStatusCode())
                .errorMessage(history.getErrorMessage())
                .createdAt(history.getCreatedAt())
                .build();
    }
}