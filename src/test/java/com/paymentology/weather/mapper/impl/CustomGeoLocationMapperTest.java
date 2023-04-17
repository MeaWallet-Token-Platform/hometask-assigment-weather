package com.paymentology.weather.mapper.impl;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.repository.entity.GeoLocationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomGeoLocationMapperTest {

    GeoLocationDto geoLocationDto;
    GeoLocationEntity geoLocationEntity;

    @InjectMocks
    CustomGeoLocationMapper victim;

    @BeforeEach
    void setUp() {
        geoLocationEntity = newGeoLocationEntity();
        geoLocationDto = newGeoLocationDto(geoLocationEntity);
    }

    @Test
    void dtoToEntity_whenNotNullParam_thenMapAndReturn() {
        var expected = geoLocationEntity;

        var result = victim.dtoToEntity(geoLocationDto);

        assertEquals(expected, result);
    }

    @Test
    void entityToDto_whenNotNullParam_thenMapAndReturn() {
        var expected = geoLocationDto;

        var result = victim.entityToDto(geoLocationEntity);

        assertEquals(expected, result);
    }

}