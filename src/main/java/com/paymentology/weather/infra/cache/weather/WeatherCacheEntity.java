package com.paymentology.weather.infra.cache.weather;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "weather_cache")
@IdClass(WeatherCacheEntityId.class)
public class WeatherCacheEntity {

    @Id
    private Double latitude;
    @Id
    private Double longitude;

    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String weatherData;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected WeatherCacheEntity() {
    }

    public WeatherCacheEntity(Double latitude, Double longitude, String weatherData, Instant expiresAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.weatherData = weatherData;
        this.expiresAt = expiresAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getWeatherData() {
        return weatherData;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
