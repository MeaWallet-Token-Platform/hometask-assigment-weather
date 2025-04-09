package com.paymentology.weather.infra.client;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;
import com.paymentology.weather.domain.weather.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenMeteoWeatherClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OpenMeteoConfig openMeteoConfig;

    private OpenMeteoWeatherClient weatherClient;
    private final GeoLocation testLocation = new GeoLocation(51.5074, -0.1278);

    @BeforeEach
    void setUp() {
        when(openMeteoConfig.getUrl()).thenReturn("https://api.open-meteo.com/v1/forecast");
        weatherClient = new OpenMeteoWeatherClient(restTemplate, openMeteoConfig);
    }

    @Test
    void shouldReturnWeatherWhenApiReturnsValidResponse() {
        Map<String, Object> currentWeather = new HashMap<>();
        currentWeather.put("temperature", 10.5);
        currentWeather.put("windspeed", 5.3);
        currentWeather.put("winddirection", 270.0);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("current_weather", currentWeather);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Celsius);

        assertTrue(result.isPresent());
        Weather weather = result.get();
        assertEquals(10.5, weather.temperature().value());
        assertEquals(TemperatureUnit.Celsius, weather.temperature().unit());
        assertEquals(5.3, weather.wind().speed());
        assertEquals(270.0, weather.wind().direction());
    }

    @Test
    void shouldReturnEmptyWhenApiReturnsNull() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Celsius);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenResponseMissingCurrentWeather() {
        Map<String, Object> apiResponse = new HashMap<>();

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Celsius);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenApiThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("API unavailable"));

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Celsius);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenResponseHasInvalidData() {
        Map<String, Object> currentWeather = new HashMap<>();
        currentWeather.put("temperature", "not a number");
        currentWeather.put("windspeed", 5.3);
        currentWeather.put("winddirection", 270.0);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("current_weather", currentWeather);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Celsius);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleDifferentTemperatureUnits() {
        Map<String, Object> currentWeather = new HashMap<>();
        currentWeather.put("temperature", 50.0);
        currentWeather.put("windspeed", 5.3);
        currentWeather.put("winddirection", 270.0);

        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("current_weather", currentWeather);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(apiResponse);

        Optional<Weather> result = weatherClient.fetchWeather(testLocation, TemperatureUnit.Fahrenheit);

        assertTrue(result.isPresent());
        Weather weather = result.get();
        assertEquals(50.0, weather.temperature().value());
        assertEquals(TemperatureUnit.Fahrenheit, weather.temperature().unit());
    }
}
