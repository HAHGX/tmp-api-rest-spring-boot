package com.tenpo.challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.dto.CalculationRequest;
import com.tenpo.challenge.dto.CalculationResponse;
import com.tenpo.challenge.exception.CacheValueNotAvailableException;
import com.tenpo.challenge.service.CalculationService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// publisher mono se usa para el enfoque reactivo. Mono a diferencia de flux, es un solo valor
// TODO: implementar el enfoque mixto
//import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CalculationControllerTest {

    @Mock
    private CalculationService calculationService;

    @Mock
    private Bucket rateLimitBucket;

    @InjectMocks
    private CalculationController calculationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(calculationController)
                .setControllerAdvice(new com.tenpo.challenge.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void calculate_WhenRateLimitIsNotExceeded_ShouldReturnCalculationResult() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest(5.0, 5.0);
        CalculationResponse response = CalculationResponse.builder()
                .num1(5.0)
                .num2(5.0)
                .appliedPercentage(10.0)
                .result(11.0)
                .build();

        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        when(calculationService.calculate(any(CalculationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.num1").value(5.0))
                .andExpect(jsonPath("$.num2").value(5.0))
                .andExpect(jsonPath("$.appliedPercentage").value(10.0))
                .andExpect(jsonPath("$.result").value(11.0));
    }

    @Test
    void calculate_WhenRateLimitIsExceeded_ShouldReturnTooManyRequests() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest(5.0, 5.0);

        when(rateLimitBucket.tryConsume(1)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.error").value("Límite de velocidad excedido"));
    }

    @Test
    void calculate_WhenCacheValueNotAvailable_ShouldReturnServiceUnavailable() throws Exception {
        // Arrange
        CalculationRequest request = new CalculationRequest(5.0, 5.0);

        when(rateLimitBucket.tryConsume(1)).thenReturn(true);
        when(calculationService.calculate(any(CalculationRequest.class)))
                .thenThrow(new CacheValueNotAvailableException("No hay un porcentaje disponible"));

        // Act & Assert
        mockMvc.perform(post("/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("Servicio temporalmente no disponible"));
    }
}