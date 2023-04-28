package com.paymentology.weather.core.model;

import com.paymentology.weather.core.constant.TemperatureUnit;

import java.io.Serializable;

public record TemperatureDto(

        Double value,

        TemperatureUnit unit

) implements Serializable {
}
