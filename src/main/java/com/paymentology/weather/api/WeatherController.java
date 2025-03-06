package com.paymentology.weather.api;

import com.paymentology.weather.domain.location.GeoLocationService;
import com.paymentology.weather.domain.weather.WeatherService;
import com.paymentology.weather.domain.weather.Weather;
import com.paymentology.weather.domain.weather.TemperatureUnit;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Optional;

@RestController
public class WeatherController {

    private final WeatherService weatherService;
    private final GeoLocationService geoLocationService;

    @Autowired
    public WeatherController(WeatherService weatherService, GeoLocationService geoLocationService) {
        this.weatherService = weatherService;
        this.geoLocationService = geoLocationService;
    }

    @GetMapping(path = "/weather")
    public Optional<Weather> get(@RequestParam(name = "unit", defaultValue = "celsius") String tempUnit, HttpServletRequest rqt) throws Throwable {
        var unit = TemperatureUnit.getByQueryParam(tempUnit);
        return Optional
                .ofNullable(rqt.getHeader("X-Forwarded-For"))
                .flatMap(this::resolve)
                .flatMap(geoLocationService::detectByIp)
                .map(l -> weatherService.get(l, unit));
    }

    private Optional<InetAddress> resolve(String ip) {
        try {
            var a = InetAddress.getByName(ip);
            return Optional.of(a);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
