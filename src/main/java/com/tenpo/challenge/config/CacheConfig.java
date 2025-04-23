package com.tenpo.challenge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Clase de configuración para los ajustes de caché Redis.
 * 
 * Esta clase configura Redis como el proveedor de caché para la aplicación,
 * implementando la interfaz CachingConfigurer para personalizar el comportamiento de la caché.
 * 
 * La caché está configurada con:
 * - Un valor TTL (Tiempo de Vida) especificado en las propiedades de la aplicación
 * - Almacenamiento en caché de valores nulos desactivado
 * - Serialización JSON para valores en caché utilizando el convertidor Jackson
 * 
 * @see org.springframework.cache.annotation.CachingConfigurer
 * @see org.springframework.data.redis.cache.RedisCacheManager
 * 
 * 
 * @Author Hugo Herrera
 * @Version 1.0
 *
 */
@Configuration
public class CacheConfig implements CachingConfigurer {

    @Value("${external-service.percentage.cache-ttl}")
    private long percentageCacheTtl;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuración general de caché para Redis
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(percentageCacheTtl)) // TTL de 30 minutos
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));

        // Construir el gestor de caché de Redis
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}