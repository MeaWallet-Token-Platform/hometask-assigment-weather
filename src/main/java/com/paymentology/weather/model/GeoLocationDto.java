package com.paymentology.weather.model;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Validated
public record GeoLocationDto(

        @NotNull
        String host,

        @NotNull
        Double latitude,

        @NotNull
        Double longitude

) implements Serializable {

    public GeoLocationDto(String host, IpApiResponseDto responseDto) {
        this(host, responseDto.latitude(), responseDto.longitude());
    }

}
