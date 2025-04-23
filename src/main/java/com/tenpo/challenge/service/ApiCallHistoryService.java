package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.ApiCallHistoryResponse;
import com.tenpo.challenge.model.ApiCallHistory;
import com.tenpo.challenge.repository.ApiCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCallHistoryService {

    private final ApiCallHistoryRepository apiCallHistoryRepository;

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
        return apiCallHistoryRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
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