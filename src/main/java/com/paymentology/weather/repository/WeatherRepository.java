package com.paymentology.weather.repository;

import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, String> {

    @Query("""
            SELECT new com.paymentology.weather.model.WeatherDto(
                w.id,
                new com.paymentology.weather.model.TemperatureDto(w.temperature, w.temperatureUnit),
                new com.paymentology.weather.model.WindDto(w.windSpeed, w.windDirection),
                w.created
            )
            FROM WeatherEntity w
            WHERE w.id = :id
            """)
    Optional<WeatherDto> findDtoById(@Param("id") String id);

}
