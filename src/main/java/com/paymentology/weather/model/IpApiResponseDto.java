package com.paymentology.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record IpApiResponseDto(

        @NotNull
        String status,

        String message,

        @JsonProperty("lat")
        Double latitude,

        @JsonProperty("lon")
        Double longitude,

        String query

) {
}
