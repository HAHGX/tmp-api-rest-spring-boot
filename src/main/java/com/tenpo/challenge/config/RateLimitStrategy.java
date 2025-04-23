package com.tenpo.challenge.config;

public enum RateLimitStrategy {
    /**
     * Refill inmediato cuando se consume, hasta alcanzar la capacidad.
     */
    GREEDY,

    /**
     * Refill por intervalos fijos sin acumulación.
     */
    INTERVAL
}