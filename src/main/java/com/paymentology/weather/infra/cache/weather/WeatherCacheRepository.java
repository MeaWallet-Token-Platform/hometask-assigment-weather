package com.paymentology.weather.infra.cache.weather;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface WeatherCacheRepository extends JpaRepository<WeatherCacheEntity, WeatherCacheEntityId> {

    @Query("SELECT w FROM WeatherCacheEntity w WHERE w.id = :identifier AND w.expiresAt > :now")
    Optional<WeatherCacheEntity> findEntryByIdentifier(WeatherCacheEntityId identifier, Instant now);
}
