package com.paymentology.weather.adapter.out.client;

import com.paymentology.weather.config.IpApiConfig;
import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.port.out.LocationProvider;
import com.paymentology.weather.adapter.out.cache.GeoLocationCacheAdapter;
import com.paymentology.weather.adapter.out.client.response.IpApiResponse;
import com.paymentology.weather.adapter.out.client.response.Success;
import com.paymentology.weather.adapter.out.client.response.Fail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpApiGeoLocationClientAdapter implements LocationProvider {

    private final IpApiConfig ipApiConfig;
    private final WebClient webClient;
    private final GeoLocationCacheAdapter cache;

    @Override
    public Mono<GeoLocation> detectByIp(InetAddress ip) {
        String ipStr = ip.getHostAddress();

        return cache.get(ipStr)
                .switchIfEmpty(fetchAndCache(ip, ipStr))
                .onErrorResume(e -> {
                    log.warn("Fallback: cache hit after failure for IP: {}", ipStr);
                    return cache.get(ipStr);
                });
    }

    @CircuitBreaker(name = "ipApi", fallbackMethod = "fallback")
    private Mono<GeoLocation> fetchAndCache(InetAddress ip, String ipStr) {
        String url = buildUrl(ip);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(IpApiResponse.class)
                .flatMap(this::toGeoLocation)
                .doOnNext(loc -> log.info("GeoLocation fetched from IP API for {}", ipStr))
                .flatMap(loc -> cache.put(ipStr, loc).thenReturn(loc));
    }

    public Mono<GeoLocation> fallback(InetAddress ip, String ipStr, Throwable ex) {
        log.error("Circuit breaker triggered for IP: {}, reason: {}", ipStr, ex.getMessage());
        return cache.get(ipStr);
    }

    private Mono<GeoLocation> toGeoLocation(IpApiResponse response) {
        return switch (response) {
            case Success success -> Mono.just(new GeoLocation(success.lat(), success.lon()));
            case Fail fail -> {
                log.warn("IP geolocation failed: {}", fail.message());
                yield Mono.empty();
            }
        };
    }

    private String buildUrl(InetAddress ip) {
        String baseUrl = ipApiConfig.getUrl();
        return baseUrl.endsWith("/") ? baseUrl + ip.getHostAddress() : baseUrl + "/" + ip.getHostAddress();
    }
}
