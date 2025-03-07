package com.paymentology.weather.infra.http;

import com.paymentology.weather.domain.location.GeoLocationService;
import com.paymentology.weather.domain.weather.WeatherResponse;
import com.paymentology.weather.domain.weather.WeatherService;
import com.paymentology.weather.infra.weather.TemperatureUnitParam;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Optional;

@RestController
@Validated
public class WeatherController {
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;
    private final GeoLocationService geoLocationService;

    @Autowired
    public WeatherController(WeatherService weatherService, GeoLocationService geoLocationService) {
        this.weatherService = weatherService;
        this.geoLocationService = geoLocationService;
    }

    @GetMapping(path = "/weather")
    public ResponseEntity<WeatherResponse> get(
            @RequestParam(name = "unit", defaultValue = "CELSIUS") String unit,
            HttpServletRequest request) {

        String forwardedIp = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(forwardedIp)) {
            log.warn("Missing X-Forwarded-For header");
            return ResponseEntity.badRequest().build();
        }

        TemperatureUnitParam temperatureUnit;
        try {
            temperatureUnit = TemperatureUnitParam.valueOf(unit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid temperature unit. Allowed values are: celsius, fahrenheit, kelvin");
        }

        try {
            return resolveIpAddress(forwardedIp)
                    .flatMap(geoLocationService::detectByIp)
                    .map(location -> weatherService.get(location, temperatureUnit.toTemperatureUnit()))
                    .map(WeatherResponse::from)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error processing weather request for IP {}: {}", forwardedIp, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private Optional<InetAddress> resolveIpAddress(String ip) {
        try {
            return Optional.of(InetAddress.getByName(ip));
        } catch (Exception e) {
            log.warn("Failed to resolve IP address {}: {}", ip, e.getMessage());
            return Optional.empty();
        }
    }
}
