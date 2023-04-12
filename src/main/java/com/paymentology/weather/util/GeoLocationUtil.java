package com.paymentology.weather.util;

import com.paymentology.weather.model.GeoLocationDto;
import org.springframework.stereotype.Component;

@Component
public class GeoLocationUtil {


    private static final double STANDARD_LATITUDE = 25.761681;
    private static final double STANDARD_LONGITUDE = -80.191788;

    public GeoLocationDto getStandardLocation(String host) {
        return new GeoLocationDto(host, STANDARD_LATITUDE, STANDARD_LONGITUDE);
    }
}
