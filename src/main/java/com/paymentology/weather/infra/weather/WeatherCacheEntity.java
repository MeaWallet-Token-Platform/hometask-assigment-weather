package com.paymentology.weather.infra.weather;
import com.paymentology.weather.domain.weather.Temperature;
import com.paymentology.weather.domain.weather.TemperatureUnit;
import com.paymentology.weather.domain.weather.Weather;
import com.paymentology.weather.domain.weather.Wind;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
public class WeatherCacheEntity implements Serializable {
    private Double latitude;
    private Double longitude;
    private String temperatureUnit;
    private Double temperatureValue;
    private Double windSpeed;
    private Double windDirection;
    private Instant createdAt;
    private Instant lastAccessedAt;

    public WeatherCacheEntity(Double latitude, Double longitude, Weather weather) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperatureUnit = weather.temperature().unit().toString();
        this.temperatureValue = weather.temperature().value();
        this.windSpeed = weather.wind().speed();
        this.windDirection = weather.wind().direction();
        this.createdAt = weather.timestamp();
        this.lastAccessedAt = Instant.now();
    }

    public Weather toDomain() {
        Temperature temperature = new Temperature(
                temperatureValue,
                TemperatureUnit.valueOf(temperatureUnit)
        );
        Wind wind = new Wind(windSpeed, windDirection);
        return new Weather(temperature, wind, createdAt);
    }
}