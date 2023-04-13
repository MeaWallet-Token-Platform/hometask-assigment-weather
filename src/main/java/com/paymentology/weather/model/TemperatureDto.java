package com.paymentology.weather.model;

import com.paymentology.weather.constant.TemperatureUnit;

import java.io.Serializable;

public record TemperatureDto(double value, TemperatureUnit unit) implements Serializable {
}
