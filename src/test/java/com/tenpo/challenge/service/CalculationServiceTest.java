package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.CalculationRequest;
import com.tenpo.challenge.dto.CalculationResponse;
import com.tenpo.challenge.exception.CacheValueNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private PercentageService percentageService;

    @InjectMocks
    private CalculationService calculationService;

    private CalculationRequest request;

    @BeforeEach
    void setUp() {
        request = new CalculationRequest(10.0, 20.0);
    }

    @Test
    void calculate_WhenPercentageServiceReturnsValue_ShouldCalculateCorrectly() {
        // Arrange
        double percentage = 10.0; // 10%
        double sum = request.getNum1() + request.getNum2(); // 30
        double expectedResult = sum + (sum * percentage / 100); // 33

        when(percentageService.fetchPercentage()).thenReturn(percentage);

        // Act
        CalculationResponse response = calculationService.calculate(request);

        // Assert
        assertNotNull(response);
        assertEquals(10.0, response.getNum1());
        assertEquals(20.0, response.getNum2());
        assertEquals(percentage, response.getAppliedPercentage());
        assertEquals(expectedResult, response.getResult());

        verify(percentageService, times(1)).fetchPercentage();
    }

    @Test
    void calculate_WhenPercentageServiceReturnsNull_ShouldThrowException() {
        // Arrange
        when(percentageService.fetchPercentage()).thenReturn(null);

        // Act & Assert
        assertThrows(CacheValueNotAvailableException.class, () -> calculationService.calculate(request));
        verify(percentageService, times(1)).fetchPercentage();
    }

    @Test
    void calculateReactive_WhenPercentageServiceReturnsValue_ShouldCalculateCorrectly() {
        // Arrange
        double percentage = 10.0; // 10%
        double sum = request.getNum1() + request.getNum2(); // 30
        double expectedResult = sum + (sum * percentage / 100); // 33

        when(percentageService.fetchPercentageReactive()).thenReturn(Mono.just(percentage));

        // Act & Assert
        StepVerifier.create(calculationService.calculateReactive(request))
                .assertNext(response -> {
                    assertEquals(10.0, response.getNum1());
                    assertEquals(20.0, response.getNum2());
                    assertEquals(percentage, response.getAppliedPercentage());
                    assertEquals(expectedResult, response.getResult());
                })
                .verifyComplete();

        verify(percentageService, times(1)).fetchPercentageReactive();
    }

    @Test
    void calculateReactive_WhenPercentageServiceReturnsEmptyMono_ShouldThrowException() {
        // Arrange
        when(percentageService.fetchPercentageReactive()).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(calculationService.calculateReactive(request))
                .expectError(CacheValueNotAvailableException.class)
                .verify();

        verify(percentageService, times(1)).fetchPercentageReactive();
    }
}