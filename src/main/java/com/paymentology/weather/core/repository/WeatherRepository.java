package com.paymentology.weather.core.repository;

import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.repository.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, String> {

    @Query("""
            SELECT new com.paymentology.weather.core.model.WeatherDto(
                w.id,
                new com.paymentology.weather.core.model.TemperatureDto(w.temperature, w.temperatureUnit),
                new com.paymentology.weather.core.model.WindDto(w.windSpeed, w.windDirection),
                w.created
            )
            FROM WeatherEntity w
            WHERE w.id = :id
            """)
    Optional<WeatherDto> findDtoById(@Param("id") String id);

}
