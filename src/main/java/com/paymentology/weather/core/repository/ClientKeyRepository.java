package com.paymentology.weather.core.repository;

import com.paymentology.weather.core.model.ClientApiKeyDto;
import com.paymentology.weather.core.repository.entity.ClientApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientKeyRepository extends JpaRepository<ClientApiKeyEntity, Long> {

    @Query("""
            SELECT new com.paymentology.weather.core.model.ClientApiKeyDto(k.id, k.apiKey, k.revoked)
            FROM ClientApiKeyEntity k
            WHERE k.apiKey = :apiKey
            """)
    Optional<ClientApiKeyDto> findByApiKey(@Param("apiKey") String apiKey);

}
