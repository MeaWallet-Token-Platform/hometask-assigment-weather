package com.paymentology.weather.controller;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.service.FacadeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final FacadeService service;

    @GetMapping(path = "/weather")
    public Optional<WeatherDto> get(@RequestParam(name = "unit", defaultValue = "celsius") String tempUnit, HttpServletRequest rqt) {

        return Optional
                .ofNullable(rqt.getHeader("X-Forwarded-For"))
                .map(ip -> service.findByUnitAndHost(tempUnit, ip));
    }

}
