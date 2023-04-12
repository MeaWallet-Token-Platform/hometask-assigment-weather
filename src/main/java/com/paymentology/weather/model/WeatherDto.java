package com.paymentology.weather.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

public record WeatherDto(

        @JsonIgnore
        String host,

        TemperatureDto temperature,

        WindDto wind,

        Instant timestamp

) {


}
