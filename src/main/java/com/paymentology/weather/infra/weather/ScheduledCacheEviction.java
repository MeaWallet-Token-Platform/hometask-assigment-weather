package com.paymentology.weather.infra.weather;

import com.paymentology.weather.infra.weather.repository.WeatherCacheRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class ScheduledCacheEviction {
    private static final Logger log = LoggerFactory.getLogger(ScheduledCacheEviction.class);

    private final WeatherCacheRepository repository;

    public ScheduledCacheEviction(WeatherCacheRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void evictOldEntries() {
        Instant cutoffTime = Instant.now().minus(Duration.ofDays(30));
        int deletedCount = repository.deleteByCreatedAtBefore(cutoffTime);

        if (deletedCount > 0) {
            log.info("Scheduled eviction: removed {} cache entries older than {}",
                    deletedCount, cutoffTime);
        }
    }
}