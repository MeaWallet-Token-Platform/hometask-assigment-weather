package com.paymentology.weather.model;

import java.io.Serializable;

public record GeoLocationDto(

        String host,

        Double latitude,

        Double longitude

) implements Serializable {

    public GeoLocationDto(String host, IpApiResponseDto responseDto) {
        this(host, responseDto.latitude(), responseDto.longitude());
    }

}
