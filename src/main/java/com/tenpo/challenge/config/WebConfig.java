package com.tenpo.challenge.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig {

    /**
     * Configuración del filtro para capturar el contenido de las solicitudes y respuestas
     * @return filtro registrado
     */
    @Bean
    public FilterRegistrationBean<Filter> contentCachingFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                           HttpServletResponse response,
                                           FilterChain filterChain) throws ServletException, IOException {
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
                
                filterChain.doFilter(wrappedRequest, wrappedResponse);
                
                // Importante: copiar el contenido de la respuesta al stream de salida
                wrappedResponse.copyBodyToResponse();
            }
        });
        registration.addUrlPatterns("/*");
        registration.setName("contentCachingFilter");
        registration.setOrder(1);
        return registration;
    }
}