package com.paymentology.weather.model;

import com.paymentology.weather.constant.TemperatureUnit;

import java.io.Serializable;

public record TemperatureDto(

        Double value,

        TemperatureUnit unit

) implements Serializable {
}
