package com.paymentology.weather.infrastructure.api.ipapi;

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
