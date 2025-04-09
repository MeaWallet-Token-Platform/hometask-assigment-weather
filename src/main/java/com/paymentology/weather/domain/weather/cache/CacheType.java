package com.paymentology.weather.domain.weather.cache;

/**
 * Enum defining the available cache implementation types.
 * Used for configuration and selecting the appropriate cache implementation.
 */
public enum CacheType {

    /**
     * Database-backed cache (persistent storage)
     */
    DATABASE,

    /**
     * In-memory cache (e.g., Redis)
     */
    REDIS;

    /**
     * Parse a string value to a CacheType, with fallback to DATABASE for invalid values.
     *
     * @param value The string value to parse
     * @return The corresponding CacheType, or DATABASE if no match is found
     */
    public static CacheType fromString(String value) {
        if (value == null || value.isBlank()) {
            return DATABASE; // Default
        }

        try {
            return CacheType.valueOf(value.toUpperCase().replace('-', '_'));
        } catch (IllegalArgumentException e) {
            // Log the error
            return DATABASE;
        }
    }
}
