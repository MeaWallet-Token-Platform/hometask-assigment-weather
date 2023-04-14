package com.paymentology.weather.test.uti;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.TemperatureDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.model.WindDto;
import com.paymentology.weather.repository.entity.WeatherEntity;

import java.time.Clock;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;

public class TestUtil {


    public static TemperatureDto newTemperatureDto() {
        return new TemperatureDto(25.5, CELSIUS);
    }

    public static WindDto newWindDto() {
        return new WindDto(25.5, 25.5);
    }

    public static OpenMeteoResponseDto newOpenMeteoResponseDto() {
        var response = new OpenMeteoResponseDto();

        var weather = new OpenMeteoResponseDto.CurrentWeather();

        weather
                .setTemperature(25.5)
                .setWindSpeed(25.5)
                .setWindDirection(25.5);

        response.setCurrentWeather(weather);

        return response;
    }

    public static GeoLocationDto newGeoLocationDto() {
        return new GeoLocationDto("testHost", 255.5, 255.5);
    }

    public static WeatherDto newWeatherDto() {
        return new WeatherDto("testHostCELSIUS", newTemperatureDto(), newWindDto(), Clock.systemUTC().instant());
    }

    public static WeatherEntity newWeatherEntity() {
        return new WeatherEntity()
                .setId(newWeatherDto().host() + CELSIUS)
                .setCreated(Clock.systemUTC().instant())
                .setTemperature(25.5)
                .setTemperatureUnit(CELSIUS)
                .setWindSpeed(25.5)
                .setWindDirection(25.5);
    }


}
