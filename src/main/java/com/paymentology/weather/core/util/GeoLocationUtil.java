package com.paymentology.weather.core.util;

import com.paymentology.weather.core.model.GeoLocationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeoLocationUtil {


    private static final double STANDARD_LATITUDE = 25.761681;
    private static final double STANDARD_LONGITUDE = -80.191788;


    public GeoLocationDto getStandardLocation(String host) {
        return new GeoLocationDto(host, STANDARD_LATITUDE, STANDARD_LONGITUDE);
    }

}
