package com.paymentology.weather.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentology.weather.domain.model.WeatherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherCacheAdapter {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration TTL = Duration.ofMinutes(30);

    public Mono<WeatherInfo.Weather> get(String cacheKey) {
        return redisTemplate.opsForValue()
                .get(cacheKey)
                .doOnNext(val -> log.info("Weather cache hit for key: {}", cacheKey))
                .flatMap(val ->
                        Mono.fromCallable(() -> objectMapper.readValue(val, WeatherInfo.Weather.class))
                                .onErrorResume(e -> {
                                    log.error("Failed to deserialize cached weather for key {}", cacheKey, e);
                                    return Mono.empty();
                                })
                );
    }

    public Mono<Void> put(String cacheKey, WeatherInfo.Weather weather) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(weather))
                .flatMap(json -> redisTemplate.opsForValue().set(cacheKey, json, TTL))
                .doOnSuccess(v -> log.info("Weather cached for key: {}", cacheKey))
                .doOnError(e -> log.error("Failed to serialize weather for key {}", cacheKey, e))
                .then();
    }
}
