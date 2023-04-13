package com.paymentology.weather.model;

import java.io.Serializable;

public record WindDto(

        Double speed,

        Double direction

) implements Serializable {
}
