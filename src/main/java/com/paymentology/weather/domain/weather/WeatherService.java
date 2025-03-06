package com.paymentology.weather.domain.weather;

import com.paymentology.weather.domain.location.GeoLocation;

public interface WeatherService {

    Weather get(GeoLocation location, TemperatureUnit unit);

}
