package com.paymentology.weather.adapter.out.cache;

import com.paymentology.weather.domain.model.GeoLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoLocationCacheAdapter {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final Duration TTL = Duration.ofHours(6);

    public Mono<GeoLocation> get(String ip) {
        return redisTemplate.opsForValue()
                .get(ip)
                .doOnNext(val -> log.info("GeoLocation cache hit for IP: {}", ip))
                .flatMap(this::safeFromString)
                .onErrorResume(e -> {
                    log.error("Failed to parse GeoLocation from cache for IP: {}", ip, e);
                    return Mono.empty();
                });
    }

    public Mono<Void> put(String ip, GeoLocation location) {
        String value = toString(location);
        return redisTemplate.opsForValue()
                .set(ip, value, TTL)
                .doOnSuccess(v -> log.info("GeoLocation cached for IP: {}", ip))
                .then();
    }

    private String toString(GeoLocation loc) {
        return loc.latitude() + "," + loc.longitude();
    }

    private Mono<GeoLocation> safeFromString(String value) {
        try {
            var parts = value.split(",");
            double lat = Double.parseDouble(parts[0]);
            double lon = Double.parseDouble(parts[1]);
            return Mono.just(new GeoLocation(lat, lon));
        } catch (Exception e) {
            return Mono.error(new IllegalArgumentException("Invalid cached value format: " + value, e));
        }
    }
}
