package com.paymentology.weather.infrastructure.config;

import com.paymentology.weather.infrastructure.properties.CacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import static java.time.Duration.ofMinutes;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    private final CacheProperties properties;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return defaultCacheConfig()
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("geoLocationCache", defaultCacheConfig()
                        .entryTtl(ofMinutes(properties.getTtl())))

                .withCacheConfiguration("weatherCache", defaultCacheConfig()
                        .entryTtl(ofMinutes(properties.getTtl())));
    }

}
