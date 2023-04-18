package com.paymentology.weather.service;

import com.paymentology.weather.mapper.GeoLocationMapper;
import com.paymentology.weather.repository.GeoLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class GeoLocationEntityServiceTest {

    @Mock
    GeoLocationRepository repository;
    @Mock
    GeoLocationMapper mapper;

    @InjectMocks
    GeoLocationEntityService victim;

    @Test
    void findByHost_whenExists_thenReturnOptionalOf() {
        var entity = newGeoLocationEntity();
        var expected = newGeoLocationDto(entity);
        given(repository.findDtoByHost(any())).willReturn(Optional.of(expected));

        var result = victim.findByHost(TEST_HOST);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void findByHost_whenDoNotExist_thenReturnOptionalEmpty() {
        given(repository.findDtoByHost(any())).willReturn(Optional.empty());

        var result = victim.findByHost(TEST_HOST);

        assertTrue(result.isEmpty());
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void saveOrUpdateAsync_whenRequest_thenDelegate() {
        var entity = newGeoLocationEntity();
        var dto = newGeoLocationDto(entity);
        given(mapper.dtoToEntity(dto)).willReturn(entity);
        given(repository.save(entity)).willReturn(entity);

        assertThatNoException().isThrownBy(() -> victim.saveOrUpdateAsync(dto));

        verifyNoMoreInteractions(mapper, repository);
    }

}