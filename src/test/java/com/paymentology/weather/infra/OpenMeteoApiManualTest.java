package com.paymentology.weather.infra;

import com.paymentology.weather.domain.weather.TemperatureUnit;
import com.paymentology.weather.infra.weather.OpenMeteoConfig;
import com.paymentology.weather.infra.weather.dto.OpenMeteoResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Manual test to test API contract")
@SpringBootTest
public class OpenMeteoApiManualTest {

    @Autowired
    private OpenMeteoConfig openMeteoConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void verifyOpenMeteoResponse() {
        var latitude = 56.945354;
        var longitude = 24.09285;
        URI url = UriComponentsBuilder.fromHttpUrl(openMeteoConfig.getUrl())
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .queryParam("temperature_unit", TemperatureUnit.CELSIUS.name().toLowerCase())
                .build()
                .toUri();
        var responseEntity = restTemplate.getForEntity(url, OpenMeteoResponse.class);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

        var openMeteoResponse = responseEntity.getBody();
        assertNotNull(openMeteoResponse);
        assertEquals(latitude, openMeteoResponse.latitude());
        assertEquals(longitude, openMeteoResponse.longitude());

        var currentWeather = openMeteoResponse.currentWeather();
        assertNotNull(currentWeather);
    }
}
