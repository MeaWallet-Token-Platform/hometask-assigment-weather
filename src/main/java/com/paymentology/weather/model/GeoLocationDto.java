package com.paymentology.weather.model;

public record GeoLocationDto(
        String host,
        double latitude,
        double longitude) {

    public GeoLocationDto(String host, IpApiResponseDto responseDto) {
        this(host, responseDto.getLatitude(), responseDto.getLongitude());
    }

}
