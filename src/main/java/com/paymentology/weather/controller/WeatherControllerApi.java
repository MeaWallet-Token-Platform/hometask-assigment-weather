package com.paymentology.weather.controller;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.service.FacadeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherControllerApi {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private final FacadeService service;

    @GetMapping
    public ResponseEntity<WeatherDto> findByTemperatureUnitAndHost(@RequestParam(defaultValue = "CELSIUS") String unit,
                                                                   HttpServletRequest request) {
        var header = request.getHeader(X_FORWARDED_FOR);
        var responseDto = service.findByUnitAndHost(unit, header);

        return ResponseEntity
                .ok(responseDto);
    }


}
