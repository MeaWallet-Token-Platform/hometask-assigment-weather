package com.paymentology.weather.adapter.out.persistence;

import com.paymentology.weather.domain.model.ClientApiKey;
import com.paymentology.weather.domain.port.out.ClientKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class R2DBCClientKeyRepositoryAdapter implements ClientKeyRepository {

    private final R2DBCClientApiKeyRepository repository;

    @Override
    public Mono<ClientApiKey> find(String apiKey) {
        return repository.findByApiKey(apiKey)
                .map(entity -> new ClientApiKey(
                        entity.getId(),
                        entity.getApiKey(),
                        entity.isRevoked()
                ));
    }
}
