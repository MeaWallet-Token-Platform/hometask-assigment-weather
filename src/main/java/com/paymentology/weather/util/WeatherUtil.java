package com.paymentology.weather.util;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.TemperatureDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.model.WindDto;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class WeatherUtil {

    public WeatherDto createResponseDto(OpenMeteoResponseDto response, GeoLocationDto geoLocationDto, TemperatureUnit unit) {
        var temperatureDto = new TemperatureDto(response.getTemperature(), unit);
        var windDto = new WindDto(response.getWindSpeed(), response.getWindDirection());
        var timestamp = Clock.systemUTC().instant();

        return new WeatherDto(geoLocationDto.host() + unit, temperatureDto, windDto, timestamp);
    }


}
