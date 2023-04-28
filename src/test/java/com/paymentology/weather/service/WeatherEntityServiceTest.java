package com.paymentology.weather.service;

import com.paymentology.weather.core.mapper.WeatherMapper;
import com.paymentology.weather.core.service.WeatherEntityService;
import com.paymentology.weather.core.repository.WeatherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.paymentology.weather.core.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        var expected = newWeatherDto(entity);
        var geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        when(repository.findDtoById(geoLocationDto.host() + CELSIUS)).thenReturn(Optional.of(expected));
        var resultOptional = victim.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertTrue(resultOptional.isPresent());
        var result = resultOptional.get();
        assertEquals(expected, result);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void findByLocationAndUnit_whenDoNotExists_thenReturnOptionalEmpty() {
        when(repository.findDtoById(any())).thenReturn(Optional.empty());

        var resultOptional = victim
                .findByLocationAndUnit(newGeoLocationDto(newGeoLocationEntity()), CELSIUS);

        assertTrue(resultOptional.isEmpty());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void saveUpdateAsync_whenSaveRequest_thenDelegate() {
        var entity = newWeatherEntity();
        var dto = newWeatherDto(entity);
        given(mapper.dtoToEntity(dto)).willReturn(entity);
        given(repository.save(any())).willReturn(entity);

        assertThatNoException().isThrownBy(() -> victim.saveUpdateAsync(dto));
    }


}
