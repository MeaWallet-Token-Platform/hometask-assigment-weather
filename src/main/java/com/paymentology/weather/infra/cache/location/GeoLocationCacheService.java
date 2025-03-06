package com.paymentology.weather.infra.cache.location;

import com.paymentology.weather.domain.location.GeoLocation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class GeoLocationCacheService {

    private final long geoLocationCacheEntryTtlMinutes;
    private final GeoLocationsCacheRepository geoLocationsCacheRepository;

    @Autowired
    public GeoLocationCacheService(@Value("${cache.geo-location.ttl-minutes:1}") long geoLocationCacheEntryTtlMinutes,
                                   GeoLocationsCacheRepository geoLocationsCacheRepository) {
        this.geoLocationCacheEntryTtlMinutes = geoLocationCacheEntryTtlMinutes;
        this.geoLocationsCacheRepository = geoLocationsCacheRepository;
    }

    public Optional<GeoLocation> getCached(InetAddress inetAddress) {
        if (Objects.isNull(inetAddress)) {
            throw new IllegalArgumentException("Expected non null argument InetAddress");
        }
        Optional<GeoLocationsCacheEntity> cacheEntity = geoLocationsCacheRepository.findEntryByIpAddress(inetAddress, Instant.now());
        return cacheEntity
                .map(e -> new GeoLocation(e.getLatitude(), e.getLongitude()));
    }

    public void createOrUpdateCacheEntry(InetAddress inetAddress, GeoLocation geoLocation) {
        if (Objects.isNull(inetAddress) || Objects.isNull(geoLocation)) {
            throw new IllegalArgumentException("Expected non null arguments InetAddress and GeoLocation");
        }

        GeoLocationsCacheEntity cacheEntity =
                new GeoLocationsCacheEntity(inetAddress,
                        geoLocation.latitude(),
                        geoLocation.longitude(),
                        Instant.now().plus(geoLocationCacheEntryTtlMinutes, ChronoUnit.MINUTES));
        geoLocationsCacheRepository.save(cacheEntity);
    }
}
