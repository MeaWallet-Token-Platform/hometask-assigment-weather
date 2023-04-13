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

    private int counter = 0;

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

        OpenMeteoResponseDto responseDto;

        if (counter == 3) {
            counter = 0;
            return Optional.empty();
        }

        try {
            responseDto = restTemplate.getForObject(uri, OpenMeteoResponseDto.class);
            counter++;
        } catch (RestClientException ex) {
            log.warn(this.getClass().getSimpleName() + " caught exception: " + ex);
            return Optional.empty();
        }

        if (responseDto == null) {
            return Optional.empty();
        }

        var weatherResponseDto = weatherUtil.createResponseDto(responseDto, geoLocation, unit);
        return Optional.of(weatherResponseDto);
    }

}
