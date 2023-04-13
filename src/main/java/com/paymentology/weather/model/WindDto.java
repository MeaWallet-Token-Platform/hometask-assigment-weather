package com.paymentology.weather.model;

import java.io.Serializable;

public record WindDto(double speed, double direction) implements Serializable {
}
