package com.paymentology.weather.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface R2DBCClientApiKeyRepository extends ReactiveCrudRepository<ClientApiKeyEntity, UUID> {
    Mono<ClientApiKeyEntity> findByApiKey(String apiKey);
}
