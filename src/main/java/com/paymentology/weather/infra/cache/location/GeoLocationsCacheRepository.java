package com.paymentology.weather.infra.cache.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface GeoLocationsCacheRepository extends JpaRepository<GeoLocationsCacheEntity, InetAddress> {

    @Query("SELECT g FROM GeoLocationsCacheEntity g WHERE g.ipAddress = :ipAddress AND g.expiresAt > :now")
    Optional<GeoLocationsCacheEntity> findEntryByIpAddress(InetAddress ipAddress, Instant now);
}
