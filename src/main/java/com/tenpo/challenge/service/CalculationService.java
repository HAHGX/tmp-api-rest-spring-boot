package com.tenpo.challenge.service;

import com.tenpo.challenge.dto.CalculationRequest;
import com.tenpo.challenge.dto.CalculationResponse;
import com.tenpo.challenge.exception.CacheValueNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationService {

    private final PercentageService percentageService;
    
    /**
     * Calcula la suma de dos números y aplica un porcentaje adicional
     * @param request objeto con los dos números a sumar
     * @return objeto con el resultado del cálculo
     * @throws CacheValueNotAvailableException si no hay porcentaje disponible 
     */
    public CalculationResponse calculate(CalculationRequest request) {
        Double percentage = percentageService.fetchPercentage();
        
        if (percentage == null) {
            throw new CacheValueNotAvailableException("No hay un porcentaje disponible y el servicio externo no responde");
        }
        
        double sum = request.getNum1() + request.getNum2();
        double result = sum + (sum * percentage / 100);
        
        return buildResponse(request.getNum1(), request.getNum2(), percentage, result);
    }
    
    /**
     * Implementación reactiva del cálculo
     * @param request objeto con los dos números a sumar
     * @return Mono con el resultado del cálculo
     */
    public Mono<CalculationResponse> calculateReactive(CalculationRequest request) {
        return percentageService.fetchPercentageReactive()
                .map(percentage -> {
                    double sum = request.getNum1() + request.getNum2();
                    double result = sum + (sum * percentage / 100);
                    return buildResponse(request.getNum1(), request.getNum2(), percentage, result);
                })
                .switchIfEmpty(Mono.error(new CacheValueNotAvailableException("No hay un porcentaje disponible y el servicio externo no responde")));
    }
    
    private CalculationResponse buildResponse(Double num1, Double num2, Double percentage, Double result) {
        return CalculationResponse.builder()
                .num1(num1)
                .num2(num2)
                .appliedPercentage(percentage)
                .result(result)
                .build();
    }
}