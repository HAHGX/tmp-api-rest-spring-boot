package com.tenpo.challenge.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuración de WebClient para realizar peticiones HTTP.
 * 
 * Esta clase proporciona una configuración personalizada para WebClient
 * con timeouts específicos para evitar bloqueos indefinidos en las 
 * operaciones de red.
 * 
 * Los timeouts configurados son:
 * - Timeout de conexión: 5 segundos
 * - Timeout de respuesta: 5 segundos
 * - Timeout de lectura: 5 segundos
 * - Timeout de escritura: 5 segundos
 *
 * @Author Hugo Herrera
 * @Version 1.0
 *
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Configurar timeout para evitar bloqueos indefinidos
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Timeout de conexión: 5 segundos
                .responseTimeout(Duration.ofSeconds(5)) // Timeout de respuesta: 5 segundos
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS)) // Timeout de lectura: 5 segundos
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))); // Timeout de escritura: 5 segundos

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}