package com.paymentology.weather.infra.cache.weather;

import java.io.Serializable;

public class WeatherCacheEntityId implements Serializable {

    private Double latitude;
    private Double longitude;

    protected WeatherCacheEntityId() {
    }

    public WeatherCacheEntityId(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
