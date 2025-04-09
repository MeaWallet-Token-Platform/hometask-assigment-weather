package com.paymentology.weather.infra.client;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.client.WeatherClient;
import com.paymentology.weather.domain.weather.model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
public class OpenMeteoWeatherClient implements WeatherClient {
    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherClient.class);

    private final RestTemplate restTemplate;
    private final OpenMeteoConfig openMeteoConfig;

    @Autowired
    public OpenMeteoWeatherClient(RestTemplate restTemplate, OpenMeteoConfig openMeteoConfig) {
        this.restTemplate = restTemplate;
        this.openMeteoConfig = openMeteoConfig;
    }

    public Optional<model.Weather> fetchWeather(GeoLocation geoLocation, model.TemperatureUnit unit) {
        try {
            return Optional.of(fetchWeatherFromApi(geoLocation, unit));
        } catch (Exception e) {
            log.error("Failed to fetch weather data from API: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private model.Weather fetchWeatherFromApi(GeoLocation geoLocation, model.TemperatureUnit unit) {
        String url = buildApiUrl(geoLocation, unit);
        log.debug("Fetching weather from API: {}", url);

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                throw new RuntimeException("Empty response received from OpenMeteo API");
            }
        } catch (RestClientException e) {
            log.error("API request failed: {}", e.getMessage());
            throw e;
        }

        Map<String, Object> currentWeather = (Map<String, Object>) response.get("current_weather");
        if (currentWeather == null) {
            throw new RuntimeException("Response missing current_weather data");
        }

        double temperature = getValue(currentWeather, "temperature");
        double windSpeed = getValue(currentWeather, "windspeed");
        double windDirection = getValue(currentWeather, "winddirection");

        return new model.Weather(
                new model.Temperature(temperature, unit),
                new model.Wind(windSpeed, windDirection)
        );
    }

    private String buildApiUrl(GeoLocation geoLocation, model.TemperatureUnit unit) {
        String baseUrl = openMeteoConfig.getUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl + String.format(
                "?latitude=%f&longitude=%f&current_weather=true&temperature_unit=%s",
                geoLocation.latitude(),
                geoLocation.longitude(),
                unit.toApiString()
        );
    }


    private double getValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required field: " + key);
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        throw new IllegalArgumentException("Expected numeric value for " + key + ", got: " + value.getClass().getName());
    }
}