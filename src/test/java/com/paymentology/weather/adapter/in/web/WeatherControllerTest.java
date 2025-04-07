package com.paymentology.weather.adapter.in.web;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.port.in.GeoLocationUseCase;
import com.paymentology.weather.domain.port.in.WeatherUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WeatherControllerTest {

    @Mock
    private WeatherUseCase weatherUseCase;

    @Mock
    private GeoLocationUseCase geoLocationUseCase;

    @InjectMocks
    private WeatherController weatherController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(weatherController).build();
    }

    @Test
    void shouldReturnWeather_whenValidRequest() throws Exception {
        var location = new GeoLocation(40.3771, 49.8920);
        var weather = new WeatherInfo.Weather(
                new WeatherInfo.Temperature(20.0, WeatherInfo.TemperatureUnit.Celsius),
                new WeatherInfo.Wind(10.0, 180.0),
                java.time.Instant.now()
        );

        when(geoLocationUseCase.detectByIp(any())).thenReturn(Mono.just(location));
        when(weatherUseCase.get(location, WeatherInfo.TemperatureUnit.Celsius)).thenReturn(Mono.just(weather));

        webTestClient.get()
                .uri("/weather?unit=celsius")
                .header("X-Forwarded-For", "185.81.85.245")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.temperature.value").isEqualTo(20.0);
    }

    @Test
    void shouldReturnBadRequest_whenUnitInvalid() {
        webTestClient.get()
                .uri("/weather?unit=kelvin")
                .header("X-Forwarded-For", "185.81.85.245")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldReturnError_whenIpMissing() {
        webTestClient.get()
                .uri("/weather")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
