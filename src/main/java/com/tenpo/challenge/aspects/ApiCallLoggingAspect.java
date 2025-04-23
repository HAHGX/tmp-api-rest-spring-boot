package com.tenpo.challenge.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.model.ApiCallHistory;
import com.tenpo.challenge.service.ApiCallHistoryService;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiCallLoggingAspect {

    private final ApiCallHistoryService apiCallHistoryService;
    private final ObjectMapper objectMapper;

    /**
     * Pointcut para todos los métodos de los controladores
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
        // Método vacío para definir el pointcut
    }

    /**
     * Registra las llamadas exitosas a la API
     * @param joinPoint Punto de ejecución
     * @param result Resultado de la llamada
     */
    @AfterReturning(pointcut = "restControllerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            int statusCode = 200;
            String responseBody = "";

            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                statusCode = responseEntity.getStatusCode().value();

                if (responseEntity.getBody() != null) {
                    responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
                }
            } else if (result != null) {
                responseBody = objectMapper.writeValueAsString(result);
            }

            ApiCallHistory history = ApiCallHistory.builder()
                    .endpoint(request.getRequestURI())
                    .method(request.getMethod())
                    .requestParams(extractRequestParams(request))
                    .responseBody(responseBody)
                    .statusCode(statusCode)
                    .build();

            apiCallHistoryService.saveAsync(history);
            
        } catch (Exception e) {
            log.error("Error logging API call: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra las llamadas a la API que han generado excepciones
     * @param joinPoint Punto de ejecución
     * @param exception Excepción generada
     */
    @AfterThrowing(pointcut = "restControllerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            ApiCallHistory history = ApiCallHistory.builder()
                    .endpoint(request.getRequestURI())
                    .method(request.getMethod())
                    .requestParams(extractRequestParams(request))
                    .errorMessage(exception.getMessage())
                    .statusCode(determineStatusCode(exception))
                    .build();

            apiCallHistoryService.saveAsync(history);
            
        } catch (Exception e) {
            log.error("Error logging API call exception: {}", e.getMessage(), e);
        }
    }

    /**
     * Extrae los parámetros de la solicitud
     * @param request solicitud HTTP
     * @return cadena con los parámetros
     */
    private String extractRequestParams(HttpServletRequest request) {
        try {
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                String content = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                if (!content.isEmpty()) {
                    return content;
                }
            }
            
            // Si no hay contenido en el cuerpo o no es un ContentCachingRequestWrapper
            StringBuilder params = new StringBuilder();
            request.getParameterMap().forEach((key, value) -> {
                params.append(key).append("=").append(String.join(",", value)).append("&");
            });
            
            return params.toString();
        } catch (Exception e) {
            log.error("Error extracting request params: {}", e.getMessage());
            return "Error extracting parameters";
        }
    }

    /**
     * Determina el código de estado HTTP en base a la excepción
     * @param exception excepción generada
     * @return código de estado HTTP
     */
    private int determineStatusCode(Exception exception) {
        // Asignar códigos de estado según el tipo de excepción
        if (exception.getClass().getSimpleName().equals("RateLimitExceededException")) {
            return 429; // Too Many Requests
        } else if (exception.getClass().getSimpleName().equals("CacheValueNotAvailableException")) {
            return 503; // Service Unavailable
        } else if (exception.getClass().getSimpleName().contains("NotFound")) {
            return 404; // Not Found
        } else if (exception.getClass().getSimpleName().equals("MethodArgumentNotValidException")) {
            return 400; // Bad Request
        }
        
        return 500; // Internal Server Error por defecto
    }
}