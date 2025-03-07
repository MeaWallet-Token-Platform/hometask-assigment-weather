package com.paymentology.weather.infra.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Success(
        @NotBlank(message = "Query IP cannot be blank")
        String query,

        @NotBlank(message = "Country cannot be blank")
        String country,

        @NotBlank(message = "Country code cannot be blank")
        String countryCode,

        String region,
        String regionName,
        String city,
        String zip,

        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        double lat,

        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        double lon,

        String timezone,
        String isp,
        String org,
        String as
) implements IpApiResponse {

    public static final String STATUS = "success";

    @Override
    public String status() {
        return STATUS;
    }
}
