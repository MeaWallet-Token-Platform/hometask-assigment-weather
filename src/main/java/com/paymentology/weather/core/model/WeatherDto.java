package com.paymentology.weather.core.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;

public record WeatherDto(

        @JsonIgnore
        String host,

        TemperatureDto temperature,

        WindDto wind,

        Instant timestamp

) implements Serializable {}
