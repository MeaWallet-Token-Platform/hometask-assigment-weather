package com.paymentology.weather.adapter.in.web;

import com.paymentology.weather.common.exception.InvalidApiKeyException;
import com.paymentology.weather.domain.port.out.ClientKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ApiKeyAuthFilter implements WebFilter {

    private static final String HEADER_API_KEY = "X-API-KEY";

    private final ClientKeyRepository clientKeyRepository;

    @NotNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(HEADER_API_KEY);

        return Mono.justOrEmpty(apiKey)
                .flatMap(clientKeyRepository::find)
                .filter(clientKey -> !clientKey.revoked())
                .switchIfEmpty(Mono.error(() -> new InvalidApiKeyException("Invalid API key: " + apiKey)))
                .flatMap(validKey -> {
                    log.info("API key validated for client: {}", validKey.id());
                    return chain.filter(exchange);
                })
                .onErrorResume(ex-> deny(exchange, "Invalid or missing API key"));
    }

    private Mono<Void> deny(ServerWebExchange exchange, String message) {
        log.warn("API key error: {}", message);
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = String.format("""
        {
          "status": 403,
          "message": "%s",
          "timestamp": "%s"
        }
        """, message, Instant.now());

        var buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

}
