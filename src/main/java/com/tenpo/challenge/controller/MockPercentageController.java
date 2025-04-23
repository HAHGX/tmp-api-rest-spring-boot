package com.tenpo.challenge.controller;

import com.tenpo.challenge.dto.PercentageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Controlador mock que simula un servicio externo que devuelve un porcentaje
 */
@RestController
@RequestMapping("/mock/percentage")
@Slf4j
public class MockPercentageController {

    private final Random random = new Random();

    /**
     * Endpoint que devuelve un porcentaje aleatorio entre 0 y 20
     * @return Respuesta con un porcentaje
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PercentageResponse> getPercentage() {
        // Simular latencia aleatoria de 0-2s
        simulateLatency();
        
        // Ocasionalmente simular un error (10% de las veces)
        if (random.nextInt(10) == 0) {
            log.info("Simulating error in mock percentage service");
            return ResponseEntity.internalServerError().build();
        }
        
        // Valor aleatorio entre 0 y 20
        double percentage = random.nextDouble() * 20;
        log.info("Mock service returning percentage: {}", percentage);
        
        return ResponseEntity.ok(new PercentageResponse(percentage));
    }

    private void simulateLatency() {
        try {
            // Latencia entre 0 y 2 segundos
            Thread.sleep(random.nextInt(2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}