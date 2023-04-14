package com.paymentology.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IpApiResponseDto(

        String status,

        String message,

        @JsonProperty("lat")
        Double latitude,

        @JsonProperty("lon")
        Double longitude,

        String query

) {
}
