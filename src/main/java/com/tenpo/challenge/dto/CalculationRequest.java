package com.tenpo.challenge.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationRequest {

    @NotNull(message = "El primer número no puede ser nulo")
    private Double num1;

    @NotNull(message = "El segundo número no puede ser nulo")
    private Double num2;
}