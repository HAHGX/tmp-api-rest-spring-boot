package com.tenpo.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResponse {
    private Double num1;
    private Double num2;
    private Double appliedPercentage;
    private Double result;
}