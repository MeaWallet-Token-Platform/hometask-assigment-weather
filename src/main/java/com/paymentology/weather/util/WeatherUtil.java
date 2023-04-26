package com.paymentology.weather.util;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.TemperatureDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.model.WindDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Clock;
import java.util.Optional;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.constant.TemperatureUnit.KELVIN;

@Component
public class WeatherUtil {

    public Optional<WeatherDto> createWeatherDto(@NotNull OpenMeteoResponseDto response,
                                                 @NotNull GeoLocationDto geoLocationDto,
                                                 @NotNull TemperatureUnit unit) {
        var temperatureDto = new TemperatureDto(response.getTemperature(), unit);
        var windDto = new WindDto(response.getWindSpeed(), response.getWindDirection());
        var timestamp = Clock.systemUTC().instant();

        return Optional.ofNullable(new WeatherDto(geoLocationDto.host() + unit, temperatureDto, windDto, timestamp));
    }

    public Optional<OpenMeteoResponseDto> applyKelvinLogic(TemperatureUnit requestUnit, OpenMeteoResponseDto openMeteoResponseDto) {
        if (KELVIN.equals(requestUnit)) {
            var temperatureCelsius = openMeteoResponseDto.getTemperature();

            openMeteoResponseDto.getCurrentWeather()
                    .setTemperature(Double.sum(temperatureCelsius, 273.15));
        }

        return Optional.ofNullable(openMeteoResponseDto);
    }

    public TemperatureUnit getApiUnit(TemperatureUnit requestUnit) {
        if (KELVIN.equals(requestUnit)) {
            return CELSIUS;
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
