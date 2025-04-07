package com.paymentology.weather.adapter.out.cache;

import com.paymentology.weather.domain.model.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GeoLocationCacheAdapterTest {

    private GeoLocationCacheAdapter adapter;
    private ReactiveStringRedisTemplate redisTemplate;
    private ReactiveValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(ReactiveStringRedisTemplate.class);
        valueOperations = mock(ReactiveValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        adapter = new GeoLocationCacheAdapter(redisTemplate);
    }

    @Test
    void testPut_shouldStoreInCache() {
        GeoLocation location = new GeoLocation(40.3772, 49.8920);
        String expectedValue = "40.3772,49.892";

        given(valueOperations.set(Mockito.eq("127.0.0.1"), Mockito.eq(expectedValue), Mockito.any()))
                .willReturn(Mono.just(true));

        Mono<Void> result = adapter.put("127.0.0.1", location);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void testGet_shouldReturnGeoLocation() {
        given(valueOperations.get("127.0.0.1"))
                .willReturn(Mono.just("40.3772,49.892"));

        StepVerifier.create(adapter.get("127.0.0.1"))
                .expectNextMatches(loc ->
                        loc.latitude() == 40.3772 &&
                                loc.longitude() == 49.892)
                .verifyComplete();
    }

    @Test
    void testGet_invalidFormat_shouldReturnEmpty() {
        given(valueOperations.get("invalid"))
                .willReturn(Mono.just("invalid_format"));

        StepVerifier.create(adapter.get("invalid"))
                .verifyComplete(); // Expecting empty Mono
    }
}
