package com.paymentology.weather.infra.weather.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface WeatherCacheRepository extends JpaRepository<WeatherCacheEntry, Long> {

    @Query("SELECT w FROM WeatherCacheEntry w WHERE w.latitude = :latitude AND w.longitude = :longitude ORDER BY w.createdAt DESC")
    Optional<WeatherCacheEntry> findLatestByLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    @Modifying
    @Query("DELETE FROM WeatherCacheEntry w WHERE w.createdAt < :cutoffTime")
    int deleteByCreatedAtBefore(@Param("cutoffTime") Instant cutoffTime);
}

