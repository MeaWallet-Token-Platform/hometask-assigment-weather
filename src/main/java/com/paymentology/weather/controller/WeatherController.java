package com.paymentology.weather.controller;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.service.FacadeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private final FacadeService service;

    @GetMapping
    public ResponseEntity<WeatherDto> findByTemperatureUnitAndHost(@RequestParam(defaultValue = "CELSIUS") TemperatureUnit unit,
                                                                   HttpServletRequest request) {
        var header = request.getHeader(X_FORWARDED_FOR);
        var responseDto = service.findByUnitAndHost(unit, header);

        return ResponseEntity
                .ok(responseDto);
    }


}
