package com.paymentology.weather.infrastructure.api.openmeteo;

import com.paymentology.weather.core.constant.TemperatureUnit;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.model.TemperatureDto;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.model.WindDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Clock;

@Component
public class WeatherUtil {

    public WeatherDto createWeatherDto(@NotNull OpenMeteoResponseDto response,
                                       @NotNull GeoLocationDto geoLocationDto,
                                       @NotNull TemperatureUnit unit) {
        var temperatureDto = new TemperatureDto(response.getTemperature(), unit);
        var windDto = new WindDto(response.getWindSpeed(), response.getWindDirection());
        var timestamp = Clock.systemUTC().instant();

        return new WeatherDto(geoLocationDto.host() + unit, temperatureDto, windDto, timestamp);
    }

    public OpenMeteoResponseDto applyKelvinLogic(TemperatureUnit requestUnit, OpenMeteoResponseDto openMeteoResponseDto) {
        if (TemperatureUnit.KELVIN.equals(requestUnit)) {
            var temperatureCelsius = openMeteoResponseDto.getTemperature();

            openMeteoResponseDto.getCurrentWeather()
                    .setTemperature(Double.sum(temperatureCelsius, 273.15));
        }

        return openMeteoResponseDto;
    }

    public TemperatureUnit getApiUnit(TemperatureUnit requestUnit) {
        if (TemperatureUnit.KELVIN.equals(requestUnit)) {
            return TemperatureUnit.CELSIUS;
        }

        return requestUnit;
    }

    public URI getCurrentWeatherUri(GeoLocationDto geoLocation, TemperatureUnit unit, String url) {
        return UriComponentsBuilder
                .fromUriString(url)
                .buildAndExpand(
                        geoLocation.latitude(),
                        geoLocation.longitude(),
                        unit.toString().toLowerCase())
                .toUri();
    }

}
