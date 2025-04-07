package com.paymentology.weather.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paymentology.weather.domain.model.WeatherInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;

class WeatherCacheAdapterTest {

    private WeatherCacheAdapter cacheAdapter;
    private ReactiveStringRedisTemplate redisTemplate;
    private ReactiveValueOperations<String, String> valueOps;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        redisTemplate = mock(ReactiveStringRedisTemplate.class);
        valueOps = mock(ReactiveValueOperations.class);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        cacheAdapter = new WeatherCacheAdapter(redisTemplate, objectMapper);

    }

    @Test
    void testPut_shouldSerializeAndCache() {
        WeatherInfo.Weather weather = new WeatherInfo.Weather(
                new WeatherInfo.Temperature(25.0, WeatherInfo.TemperatureUnit.Celsius),
                new WeatherInfo.Wind(10.0, 180.0),
                Instant.now()
        );

        when(valueOps.set(any(), any(), any())).thenReturn(Mono.just(true));

        StepVerifier.create(cacheAdapter.put("weather:key", weather))
                .verifyComplete();
    }

    @Test
    void testGet_shouldDeserializeAndReturn() throws Exception {
        WeatherInfo.Weather weather = new WeatherInfo.Weather(
                new WeatherInfo.Temperature(25.0, WeatherInfo.TemperatureUnit.Celsius),
                new WeatherInfo.Wind(10.0, 180.0),
                Instant.now()
        );

        String json = objectMapper.writeValueAsString(weather);
        when(valueOps.get("weather:key")).thenReturn(Mono.just(json));

        StepVerifier.create(cacheAdapter.get("weather:key"))
                .expectNextMatches(w ->
                        w.temperature().value() == 25.0 &&
                                w.temperature().unit() == WeatherInfo.TemperatureUnit.Celsius &&
                                w.wind().speed() == 10.0 &&
                                w.wind().direction() == 180.0
                )
                .verifyComplete();
    }

    @Test
    void testGet_shouldHandleInvalidJson() {
        when(valueOps.get("bad:key")).thenReturn(Mono.just("invalid-json"));

        StepVerifier.create(cacheAdapter.get("bad:key"))
                .verifyComplete(); // fallback to Mono.empty()
    }
}
