package com.paymentology.weather.infra.weather.repository;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model.Temperature;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;
import com.paymentology.weather.domain.weather.model.Weather;
import com.paymentology.weather.domain.weather.model.Wind;
import jakarta.persistence.*;


import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "weather_cache")
public class WeatherCacheEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private String temperatureUnit;

    @Column(nullable = false)
    private double windSpeed;

    @Column(nullable = false)
    private double windDirection;

    @Column(nullable = false)
    private Instant createdAt;

    public WeatherCacheEntry() {}

    public WeatherCacheEntry(
            Long id,
            double latitude,
            double longitude,
            double temperature,
            String temperatureUnit,
            double windSpeed,
            double windDirection,
            Instant createdAt) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.temperatureUnit = temperatureUnit;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static WeatherCacheEntry fromWeather(GeoLocation location, Weather weather, Instant createdAt) {
        return new WeatherCacheEntry(
                null,
                location.latitude(),
                location.longitude(),
                weather.temperature().value(),
                weather.temperature().unit().name(),
                weather.wind().speed(),
                weather.wind().direction(),
                createdAt
        );
    }

    public Weather toWeather() {
        return new Weather(
                new Temperature(temperature, TemperatureUnit.valueOf(temperatureUnit)),
                new Wind(windSpeed, windDirection)
        );
    }

    public boolean isValid(Duration maxAge) {
        return Duration.between(createdAt, Instant.now()).compareTo(maxAge) < 0;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}


