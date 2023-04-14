package com.paymentology.weather.service;

import com.paymentology.weather.mapper.WeatherMapper;
import com.paymentology.weather.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherEntityServiceTest {

    @Mock
    WeatherRepository repository;
    @Mock
    WeatherMapper mapper;

    @InjectMocks
    WeatherEntityService victim;

    @Test
    void findByLocationAndUnit_whenExists_thenReturnOptionalOf() {
        var entity = newWeatherEntity();
        var expected = newWeatherDto();
        var geoLocationDto = newGeoLocationDto();
        when(repository.findById(geoLocationDto.host() + CELSIUS)).thenReturn(Optional.of(entity));
        when(mapper.entityToDto(entity)).thenReturn(expected);

        var resultOptional = victim.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertTrue(resultOptional.isPresent());
        var result = resultOptional.get();
        assertEquals(expected, result);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void findByLocationAndUnit_whenDoNotExists_thenReturnOptionalEmpty() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        var resultOptional = victim.findByLocationAndUnit(newGeoLocationDto(), CELSIUS);

        assertTrue(resultOptional.isEmpty());
        verifyNoMoreInteractions(repository, mapper);
    }


}