package com.paymentology.weather.util;

import com.paymentology.weather.model.GeoLocationDto;
import org.springframework.stereotype.Component;

@Component
public class GeoLocationUtil {


    private static final double STANDARD_LATITUDE = 25.761681;
    private static final double STANDARD_LONGITUDE = -80.191788;
    private static final String STANDARD_HOST = "standard_host";

    public GeoLocationDto getStandardLocation(String host) {
        if (host == null) {
            host = STANDARD_HOST;
        }

        return new GeoLocationDto(host, STANDARD_LATITUDE, STANDARD_LONGITUDE);
    }
}
