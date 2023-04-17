package com.paymentology.weather.test.uti;

import com.paymentology.weather.model.ClientApiKeyDto;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.TemperatureDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.model.WindDto;
import com.paymentology.weather.repository.entity.ClientApiKeyEntity;
import com.paymentology.weather.repository.entity.GeoLocationEntity;
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

    public static GeoLocationEntity newGeoLocationEntity() {
        return new GeoLocationEntity()
                .setId("testHost")
                .setLatitude(255.5)
                .setLongitude(255.5);
    }

    public static GeoLocationDto newGeoLocationDto(GeoLocationEntity entity) {
        return new GeoLocationDto(entity.getId(), entity.getLatitude(), entity.getLongitude());
    }

    public static WeatherDto newWeatherDto(WeatherEntity entity) {
        return new WeatherDto(entity.getId(), newTemperatureDto(), newWindDto(), entity.getCreated());
    }

    public static WeatherEntity newWeatherEntity() {
        return new WeatherEntity()
                .setId("testHostCELSIUS")
                .setCreated(Clock.systemUTC().instant())
                .setTemperature(25.5)
                .setTemperatureUnit(CELSIUS)
                .setWindSpeed(25.5)
                .setWindDirection(25.5);
    }

    public static IpApiResponseDto newIpApiResponseDto(String status) {
        return new IpApiResponseDto(status, null, 255.5, 255.5, null);
    }

    public static ClientApiKeyEntity newClientApiKeyEntity(String key) {
        return new ClientApiKeyEntity()
                .setApiKey(key)
                .setCreatedAt(Clock.systemUTC().instant())
                .setId(1L)
                .setRevoked(false);
    }

    public static ClientApiKeyDto newClientApiKeyDto(ClientApiKeyEntity entity) {
        return new ClientApiKeyDto(entity.getId(), entity.getApiKey(), entity.getRevoked());
    }


}
