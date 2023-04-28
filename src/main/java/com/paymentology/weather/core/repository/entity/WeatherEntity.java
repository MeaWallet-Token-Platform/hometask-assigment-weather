package com.paymentology.weather.core.repository.entity;

import com.paymentology.weather.core.constant.TemperatureUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Entity
@Table(name = "weather")
@Data
@Accessors(chain = true)
public class WeatherEntity {

    @Id
    private String id;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "temperature_unit")
    @Enumerated(EnumType.STRING)
    private TemperatureUnit temperatureUnit;

    @Column(name = "wind_speed")
    private Double windSpeed;

    @Column(name = "wind_direction")
    private Double windDirection;

    @Column(name = "created")
    private Instant created;

}
