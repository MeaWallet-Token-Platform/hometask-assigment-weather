package com.paymentology.weather.repository;

import com.paymentology.weather.model.ClientApiKeyDto;
import com.paymentology.weather.repository.entity.ClientApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientKeyRepository extends JpaRepository<ClientApiKeyEntity, Long> {

    @Query("""
            SELECT new com.paymentology.weather.model.ClientApiKeyDto(k.id, k.apiKey, k.revoked)
            FROM ClientApiKeyEntity k
            WHERE k.apiKey = :apiKey
            """)
    Optional<ClientApiKeyDto> findByApiKey(@Param("apiKey") String apiKey);

}
