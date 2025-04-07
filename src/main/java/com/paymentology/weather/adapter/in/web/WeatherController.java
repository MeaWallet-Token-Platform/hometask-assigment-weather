package com.paymentology.weather.adapter.in.web;

import com.paymentology.weather.domain.model.WeatherInfo;
import com.paymentology.weather.domain.port.in.GeoLocationUseCase;
import com.paymentology.weather.domain.port.in.WeatherUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/weather", produces = MediaType.APPLICATION_JSON_VALUE)
public class WeatherController {

    private final WeatherUseCase weatherUseCase;
    private final GeoLocationUseCase geoLocationUseCase;

    @GetMapping
    public Mono<WeatherInfo.Weather> getWeather(
            @RequestParam(name = "unit", defaultValue = "celsius") String unitParam,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp
    ) {
        WeatherInfo.TemperatureUnit unit;

        try {
            unit = parseTemperatureUnit(unitParam);
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid temperature unit received: {}", unitParam);
            return Mono.error(new IllegalArgumentException("Supported units are: celsius, fahrenheit"));
        }

        return Mono.justOrEmpty(clientIp)
                .flatMap(this::resolveInetAddress)
                .flatMap(geoLocationUseCase::detectByIp)
                .flatMap(location -> weatherUseCase.get(location, unit))
                .doOnSuccess(weather -> log.info("Weather data successfully retrieved"))
                .doOnError(e -> log.error("Failed to retrieve weather data", e))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Missing or invalid IP address")));
    }

    private WeatherInfo.TemperatureUnit parseTemperatureUnit(String value) {
        return switch (value.toLowerCase()) {
            case "celsius" -> WeatherInfo.TemperatureUnit.Celsius;
            case "fahrenheit" -> WeatherInfo.TemperatureUnit.Fahrenheit;
            default -> throw new IllegalArgumentException("Unsupported temperature unit: " + value);
        };
    }

    private Mono<InetAddress> resolveInetAddress(String ipString) {
        return Mono.fromCallable(() -> InetAddress.getByName(ipString))
                .doOnError(e -> log.warn("Failed to resolve IP address: {}", ipString));
    }
}
