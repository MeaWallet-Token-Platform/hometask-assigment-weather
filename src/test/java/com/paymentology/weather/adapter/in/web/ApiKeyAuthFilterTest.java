package com.paymentology.weather.adapter.in.web;

import com.paymentology.weather.domain.model.ClientApiKey;
import com.paymentology.weather.domain.port.out.ClientKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class ApiKeyAuthFilterTest {

    private ClientKeyRepository clientKeyRepository;
    private ApiKeyAuthFilter apiKeyAuthFilter;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        clientKeyRepository = mock(ClientKeyRepository.class);
        chain = mock(WebFilterChain.class);
        apiKeyAuthFilter = new ApiKeyAuthFilter(clientKeyRepository);
    }

    @Test
    void shouldPassWhenApiKeyIsValid() {
        String validKey = "valid-api-key";
        ClientApiKey clientKey = new ClientApiKey(1, validKey, false);

        when(clientKeyRepository.find(validKey)).thenReturn(Mono.just(clientKey));
        when(chain.filter(any())).thenReturn(Mono.empty());

        var request = MockServerHttpRequest.get("/weather")
                .header("X-API-KEY", validKey)
                .build();

        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(apiKeyAuthFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }

    @Test
    void shouldDenyWhenApiKeyIsMissing() {
        var request = MockServerHttpRequest.get("/weather").build();
        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(apiKeyAuthFilter.filter(exchange, chain))
                .expectComplete()
                .verify();

        MockServerHttpResponse response = exchange.getResponse();
        assert response.getStatusCode() == HttpStatus.FORBIDDEN;
    }

    @Test
    void shouldDenyWhenApiKeyIsRevoked() {
        String revokedKey = "revoked-key";
        ClientApiKey revokedClientKey = new ClientApiKey(2, revokedKey, true);

        when(clientKeyRepository.find(revokedKey)).thenReturn(Mono.just(revokedClientKey));

        var request = MockServerHttpRequest.get("/weather")
                .header("X-API-KEY", revokedKey)
                .build();

        var exchange = MockServerWebExchange.from(request);

        StepVerifier.create(apiKeyAuthFilter.filter(exchange, chain))
                .expectComplete()
                .verify();

        assert exchange.getResponse().getStatusCode() == HttpStatus.FORBIDDEN;
    }

    @Test
    void shouldSkipWhenActuatorEndpoint() {
        var request = MockServerHttpRequest.get("/actuator/health").build();
        var exchange = MockServerWebExchange.from(request);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(apiKeyAuthFilter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }
}
