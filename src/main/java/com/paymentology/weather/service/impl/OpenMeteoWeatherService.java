package com.paymentology.weather.service.impl;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.OpenMeteoResponseDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.properties.OpenMeteoProperties;
import com.paymentology.weather.service.WeatherApiService;
import com.paymentology.weather.util.WeatherUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoWeatherService implements WeatherApiService {


    private final OpenMeteoProperties properties;
    private final RestTemplate restTemplate;
    private final WeatherUtil weatherUtil;

    @Override
    public Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocation, TemperatureUnit unit) {
        var uri = UriComponentsBuilder
                .fromUriString(properties.getCurrentWeatherUrl())
                .buildAndExpand(
                        geoLocation.latitude(),
                        geoLocation.longitude(),
                        unit.toString().toLowerCase())
                .toUri();

        try {
            var openMeteoResponseDto = restTemplate.getForObject(uri, OpenMeteoResponseDto.class);

            if (openMeteoResponseDto == null) {
                return Optional.empty();
            }

            var weatherResponseDto = weatherUtil.createResponseDto(openMeteoResponseDto, geoLocation, unit);

            return Optional.of(weatherResponseDto);

        } catch (RestClientException ex) {
            log.warn(this.getClass().getSimpleName() + " caught exception: " + ex);
            return Optional.empty();
        }

    }

}
