package com.tenpo.challenge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Entidad que representa un registro histórico de llamadas a la API realizadas dentro del sistema.
 * <p>
 * Esta clase captura detalles sobre cada solicitud a la API y su correspondiente respuesta,
 * incluyendo el endpoint accedido, el método HTTP utilizado, los parámetros de la solicitud, 
 * el cuerpo de la respuesta, el código de estado y cualquier mensaje de error que haya ocurrido. 
 * Cada registro es automáticamente marcado con fecha y hora al ser creado.
 * <p>
 * La clase se utiliza para propósitos de auditoría, monitoreo y resolución de problemas.
 *
 * @author Hugo Herrera
 * @version 1.0
 */
@Entity
@Table(name = "api_call_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "request_params")
    private String requestParams;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = ZonedDateTime.now();
    }
}