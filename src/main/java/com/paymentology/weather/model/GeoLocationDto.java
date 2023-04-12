package com.paymentology.weather.model;

import java.io.Serializable;

public record GeoLocationDto(
        String host,
        double latitude,
        double longitude) implements Serializable {

    public GeoLocationDto(String host, IpApiResponseDto responseDto) {
        this(host, responseDto.getLatitude(), responseDto.getLongitude());
    }

}
