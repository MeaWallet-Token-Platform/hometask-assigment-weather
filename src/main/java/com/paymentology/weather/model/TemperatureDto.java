package com.paymentology.weather.model;

import com.paymentology.weather.constant.TemperatureUnit;

public record TemperatureDto(double value, TemperatureUnit unit) {
}
