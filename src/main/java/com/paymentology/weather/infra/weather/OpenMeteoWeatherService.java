package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.*;
import com.paymentology.weather.infra.cache.weather.WeatherCacheService;
import com.paymentology.weather.infra.weather.dto.OpenMeteoCurrentWeather;
import com.paymentology.weather.infra.weather.dto.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Service
public class OpenMeteoWeatherService implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherService.class);

    private final OpenMeteoConfig openMeteoConfig;
    private final RestTemplate restTemplate;
    private final WeatherCacheService weatherCacheService;

    @Autowired
    public OpenMeteoWeatherService(OpenMeteoConfig openMeteoConfig,
                                   RestTemplate restTemplate,
                                   WeatherCacheService weatherCacheService) {
        this.openMeteoConfig = openMeteoConfig;
        this.restTemplate = restTemplate;
        this.weatherCacheService = weatherCacheService;
    }

    @Override
    public Weather get(GeoLocation geoLocation, TemperatureUnit unit) {
        URI uri = UriComponentsBuilder.fromHttpUrl(openMeteoConfig.getUrl())
                .queryParam("latitude", geoLocation.latitude())
                .queryParam("longitude", geoLocation.longitude())
                .queryParam("current_weather", true)
                .queryParam("temperature_unit", unit.name().toLowerCase())
                .build()
                .toUri();
        return getOpenMeteoResponse(uri)
                .map(OpenMeteoResponse::currentWeather)
                .map(cw -> createWeatherAndStoreInCache(geoLocation, cw, unit))
                .or(() -> weatherCacheService.getCached(geoLocation.latitude(), geoLocation.longitude()))
                .orElseThrow();
    }

    private Weather createWeatherAndStoreInCache(GeoLocation geoLocation,
                                                 OpenMeteoCurrentWeather openMeteoCurrentWeather,
                                                 TemperatureUnit temperatureUnit) {
        var wind = new Wind(openMeteoCurrentWeather.windSpeed(), openMeteoCurrentWeather.windDirection());
        var temperature = new Temperature(openMeteoCurrentWeather.temperature(), temperatureUnit);
        var weather = new Weather(temperature, wind, Instant.now());
        weatherCacheService.createOrUpdateCacheEntry(geoLocation.latitude(), geoLocation.longitude(), weather);
        return weather;
    }

    private Optional<OpenMeteoResponse> getOpenMeteoResponse(URI uri) {
        try {
            var response = restTemplate.getForEntity(uri, OpenMeteoResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(response.getBody());
            } else {
                log.warn(String.format("Open-meteo response status is %s. Request uri: %s", response.getStatusCode(), uri));
            }
        } catch (RestClientException ex) {
            log.warn("Call to Open-meteo API ended with exception. Message: " + ex.getMessage());
        }
        return Optional.empty();
    }
}
