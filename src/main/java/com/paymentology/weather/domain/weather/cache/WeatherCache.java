package com.paymentology.weather.domain.weather.cache;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model.Weather;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Interface for weather data caching mechanisms.
 * This abstraction allows for different cache implementations
 * (database, in-memory, Redis, etc.)
 */
public interface WeatherCache {

    /**
     * Find a cached weather entry for a specific location
     *
     * @param location The geographic location to search for
     * @return Optional containing the cached weather data with its creation timestamp, or empty if not found
     */
    Optional<CachedWeatherEntry> find(GeoLocation location);

    /**
     * Saves weather data to the cache
     *
     * @param location The geographic location for which this weather data applies
     * @param weather The weather data to cache
     * @param createdAt The timestamp when the weather data was created
     */
    void save(GeoLocation location, Weather weather, Instant createdAt);

    /**
     * Represents a cached weather entry with its creation timestamp.
     */
    record CachedWeatherEntry(Weather weather, Instant createdAt) {
        /**
         * Checks if this cache entry is still valid based on the provided maximum age
         *
         * @param maxAge Maximum allowed age for a valid entry
         * @return true if the entry is still valid, false otherwise
         */
        public boolean isValid(java.time.Duration maxAge) {
            return java.time.Duration.between(createdAt, Instant.now()).compareTo(maxAge) < 0;
        }

        public String log(Duration maxAge, GeoLocation geoLocation) {
            return isValid(maxAge)
                    ? "API fetch failed. Using valid cached weather data for location: " + geoLocation
                    : "API fetch failed. Using stale cached weather data for location: " + geoLocation;
        }
    }
}

