package com.paymentology.weather.service.impl;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.exception.BadRequestException;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.service.GeoLocationApiService;
import com.paymentology.weather.service.GeoLocationEntityService;
import com.paymentology.weather.service.WeatherApiService;
import com.paymentology.weather.service.WeatherEntityService;
import com.paymentology.weather.util.GeoLocationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacadeService {


    public static final String UNABLE_TO_FIND_WEATHER = "Unable to find weather data for host: ";
    private final GeoLocationEntityService geoLocationEntityService;
    private final GeoLocationApiService geoLocationApiService;
    private final WeatherApiService weatherApiService;
    private final WeatherEntityService weatherEntityService;
    private final GeoLocationUtil geoLocationUtil;


    public WeatherDto findByUnitAndHost(TemperatureUnit unit, String host) {
        GeoLocationDto geoLocationDto;

        var geoLocationOptional = geoLocationEntityService.findByHost(host);

        if (geoLocationOptional.isPresent()){
            geoLocationDto = geoLocationOptional.get();
        } else {
            geoLocationDto = geoLocationApiService.findByHost(host)
                    .orElseGet(() -> geoLocationUtil.getStandardLocation(host));

            geoLocationDto = geoLocationEntityService.save(geoLocationDto);
        }

        var weatherDtoOptional = weatherApiService.findByLocationAndUnit(geoLocationDto, unit);

        if (weatherDtoOptional.isPresent()) {
            return weatherEntityService.save(weatherDtoOptional.get());
        }

        return weatherEntityService.findByLocationAndUnit(geoLocationDto, unit)
                .orElseThrow(() -> new BadRequestException(UNABLE_TO_FIND_WEATHER + host));

    }


}
