package com.paymentology.weather.domain.port.out;

import com.paymentology.weather.domain.model.ClientApiKey;
import reactor.core.publisher.Mono;

public interface ClientKeyRepository {
    Mono<ClientApiKey> find(String apiKey);
}
