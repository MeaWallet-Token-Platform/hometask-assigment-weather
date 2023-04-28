package com.paymentology.weather.core.service;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.infrastructure.exception.BadRequestException;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.util.GeoLocationUtil;
import com.paymentology.weather.core.util.TemperatureUnitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacadeService {


    public static final String UNABLE_TO_FIND_WEATHER_DATA = "Unable to find weather data";
    public static final String NO_HOST_PROVIDED = "No host provided. Substituting with standard host";
    public static final String STANDARD_HOST = "standard_host";


    private final GeoLocationEntityService geoLocationEntityService;
    private final GeoLocationApiService geoLocationApiService;
    private final WeatherApiService weatherApiService;
    private final WeatherEntityService weatherEntityService;
    private final GeoLocationUtil geoLocationUtil;
    private final TemperatureUnitUtil temperatureUnitUtil;


    public WeatherDto findByUnitAndHost(String unitString, String requestHost) {
        var unit = temperatureUnitUtil.determineUnit(unitString);

        var host = Optional.ofNullable(requestHost)
                .orElseGet(() -> {
                    log.warn(NO_HOST_PROVIDED);
                    return STANDARD_HOST;
                });

        GeoLocationDto geoLocationDto;

        var geoLocationDtoOptional = geoLocationApiService.findByHost(host);

        if (geoLocationDtoOptional.isPresent()) {
            geoLocationDto = geoLocationDtoOptional.get();
            geoLocationEntityService.saveOrUpdateAsync(geoLocationDto);
        } else {
            geoLocationDto = geoLocationEntityService.findByHost(host)
                    .orElseGet(() -> geoLocationUtil.getStandardLocation(host));
        }

        var weatherDtoOptional = weatherApiService.findByLocationAndUnit(geoLocationDto, unit);

        if (weatherDtoOptional.isPresent()) {
            weatherEntityService.saveUpdateAsync(weatherDtoOptional.get());
            return weatherDtoOptional.get();
        } else {
            return weatherEntityService.findByLocationAndUnit(geoLocationDto, unit)
                    .orElseThrow(() -> new BadRequestException(UNABLE_TO_FIND_WEATHER_DATA + host));
        }
    }

}
