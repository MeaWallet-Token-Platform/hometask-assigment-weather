package com.paymentology.weather.adapter.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class R2DBCClientKeyRepositoryAdapterTest {

    private R2DBCClientApiKeyRepository repository;
    private R2DBCClientKeyRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(R2DBCClientApiKeyRepository.class);
        adapter = new R2DBCClientKeyRepositoryAdapter(repository);
    }

    @Test
    void find_shouldReturnClientApiKey_whenFoundInDb() {
        String apiKey = "test-key";
        ClientApiKeyEntity entity = new ClientApiKeyEntity();
        entity.setId(1L);
        entity.setApiKey(apiKey);
        entity.setRevoked(false);

        when(repository.findByApiKey(apiKey)).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.find(apiKey))
                .expectNextMatches(result ->
                        result.id() == 1L &&
                                result.apiKey().equals(apiKey) &&
                                !result.revoked()
                )
                .verifyComplete();

        verify(repository).findByApiKey(apiKey);
    }

    @Test
    void find_shouldReturnEmpty_whenNotFound() {
        String apiKey = "missing-key";

        when(repository.findByApiKey(apiKey)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.find(apiKey))
                .verifyComplete();

        verify(repository).findByApiKey(apiKey);
    }
}
