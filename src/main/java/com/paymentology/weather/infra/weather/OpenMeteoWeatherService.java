package com.paymentology.weather.infra.weather;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.*;
import com.paymentology.weather.infra.weather.dto.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.Optional;

@Service
public class OpenMeteoWeatherService implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherService.class);

    private final OpenMeteoConfig openMeteoConfig;
    private final RestTemplate restTemplate;
    private final WeatherCacheService cacheService;

    @Autowired
    public OpenMeteoWeatherService(OpenMeteoConfig openMeteoConfig, RestTemplate restTemplate, WeatherCacheService cacheService) {
        this.openMeteoConfig = openMeteoConfig;
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    // todo: @NameHidden you kidding me man... I will show this to a candidate and will ask his opinion.
    @Override
    public Weather get(GeoLocation geoLocation, TemperatureUnit unit) {
        // First, try to get from cache
        Optional<Weather> cachedWeather = cacheService.getFromCache(geoLocation, unit);
        if (cachedWeather.isPresent()) {
            log.debug("Returning cached weather data for location: {}, {}",
                    geoLocation.latitude(), geoLocation.longitude());
            return cachedWeather.get();
        }

        // If not in cache, try to get from API
        try {
            Weather weather = fetchFromApi(geoLocation, unit);

            cacheService.saveToCache(geoLocation, weather);

            return weather;
        } catch (Exception e) {
            // If api fails, read from cache
            log.error("Error fetching weather data from Open Meteo API: {}", e.getMessage());

            Optional<Weather> fallbackWeather = cacheService.getFromCache(geoLocation, unit);
            return fallbackWeather.orElseThrow(() ->
                    new RuntimeException("Failed to fetch weather data and no cached data available", e));
        }
    }

    Weather fetchFromApi(GeoLocation geoLocation, TemperatureUnit unit) {
        // Build API URL
        String queryString = buildQueryString(geoLocation, unit);
        String url = buildFullUrl(queryString);
        log.debug("Fetching weather data from Open Meteo API: {}", url);

        // Make API call
        OpenMeteoResponse response = restTemplate.getForObject(url, OpenMeteoResponse.class);
        if (response == null || response.currentWeather() == null) {
            throw new RestClientException("Invalid response from Open Meteo API");
        }

        var currentWeather = response.currentWeather();

        Weather weather = new Weather(
                new Temperature(
                        doubleOf(currentWeather.temperature()),
                        unit == TemperatureUnit.Kelvin ? TemperatureUnit.Celsius : unit
                ),
                new Wind(
                        doubleOf(currentWeather.windspeed()),
                        doubleOf(currentWeather.winddirection())
                ),
                Clock.systemUTC().instant()
        );

        // Convert to Kelvin if needed
        return unit == TemperatureUnit.Kelvin ? convertToKelvin(weather) : weather;
    }

    private String buildQueryString(GeoLocation location, TemperatureUnit unit) {
        return String.format(
                "?latitude=%f&longitude=%f&current_weather=true&temperature_unit=%s",
                location.latitude(),
                location.longitude(),
                unitOf(unit)
        );
    }

    private String buildFullUrl(String queryString) {
        String baseUrl = openMeteoConfig.getUrl();
        return baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1) + queryString
                : baseUrl + queryString;
    }

    private String unitOf(TemperatureUnit unit) {
        return switch (unit) {
            case Celsius, Kelvin -> "celsius";
            case Fahrenheit -> "fahrenheit";
        };
    }

    private Weather convertToKelvin(Weather weather) {
        return new Weather(
                convertTemperature(weather.temperature()),
                weather.wind(),
                weather.timestamp()
        );
    }

    private double doubleOf(Object value) {
        return switch (value) {
            case Double d -> BigDecimal.valueOf(d)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            case Integer i -> BigDecimal.valueOf(i)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            case null -> throw new IllegalArgumentException("Value cannot be null");
            default -> throw new IllegalArgumentException("Unexpected type " + value.getClass());
        };
    }

    private Temperature convertTemperature(Temperature temperature) {
        return new Temperature(
                doubleOf(temperature.value() + 273.15),
                TemperatureUnit.Kelvin
        );
    }
}

