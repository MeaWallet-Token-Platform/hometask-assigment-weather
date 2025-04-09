package com.paymentology.weather.infra.http;

import com.paymentology.weather.domain.location.GeoLocationService;
import com.paymentology.weather.domain.weather.WeatherService;
import com.paymentology.weather.domain.weather.model.TemperatureUnit;
import com.paymentology.weather.infra.http.response.WeatherJsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

@RestController
public class WeatherController {
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private GeoLocationService geoLocationService;

    @GetMapping(path = "/weather")
    public ResponseEntity<WeatherJsonResponse> getWeather(
            @RequestParam(name = "unit", defaultValue = "celsius") String tempUnitParam,
            @RequestHeader(name = "X-Forwarded-For") String ipAddress,
            @RequestHeader(name = "X-API-KEY") String apiKey) {

        log.debug("Received weather request for IP: {}", ipAddress);

        try {
            InetAddress resolvedIp = InetAddress.getByName(ipAddress);
            Instant now = Instant.now();

            return geoLocationService.detectByIp(resolvedIp)
                    .flatMap(location -> weatherService.get(location, TemperatureUnit.fromString(tempUnitParam), now))
                    .map(weather -> WeatherJsonResponse.fromWeather(weather, now))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());
        } catch (UnknownHostException e) {
            log.error("Invalid IP address: {}", ipAddress, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving weather data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}