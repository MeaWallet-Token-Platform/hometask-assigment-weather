package com.paymentology.weather.mapper.impl;

import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.entity.WeatherEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;

import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomWeatherMapperTest {

    WeatherDto weatherDto;
    WeatherEntity weatherEntity;

    @InjectMocks
    CustomWeatherMapper victim;

    @BeforeEach
    void setUp() {
        weatherEntity = newWeatherEntity();
        weatherDto = newWeatherDto(weatherEntity);
    }

    @Test
    void dtoToEntity_whenNotNullParam_thenMapAndReturn() {
        var expected = weatherEntity;

        var result = victim.dtoToEntity(weatherDto);

        assertEquals(expected, result);
    }

    @Test
    void entityToDto_whenNotNullParam_thenMapAndReturn() {
        var expected = weatherDto;

        var result = victim.entityToDto(weatherEntity);

        assertEquals(expected, result);
    }

}