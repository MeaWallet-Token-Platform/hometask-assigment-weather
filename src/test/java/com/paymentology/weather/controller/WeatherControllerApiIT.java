package com.paymentology.weather.controller;

import com.paymentology.weather.core.model.ClientApiKeyDto;
import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.model.WeatherDto;
import com.paymentology.weather.core.repository.ClientKeyRepository;
import com.paymentology.weather.core.repository.GeoLocationRepository;
import com.paymentology.weather.core.repository.WeatherRepository;
import com.paymentology.weather.core.service.GeoLocationApiService;
import com.paymentology.weather.core.service.GeoLocationEntityService;
import com.paymentology.weather.core.service.WeatherApiService;
import com.paymentology.weather.core.service.WeatherEntityService;
import com.paymentology.weather.test.config.TestRedisConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.paymentology.weather.core.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.TEST_API_KEY;
import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static com.paymentology.weather.test.uti.TestUtil.TEST_HTTP_LOCALHOST_WEATHER;
import static com.paymentology.weather.test.uti.TestUtil.TEST_X_API_KEY;
import static com.paymentology.weather.test.uti.TestUtil.TEST_X_FORWARDED_FOR;
import static com.paymentology.weather.test.uti.TestUtil.newClientApiKeyDto;
import static com.paymentology.weather.test.uti.TestUtil.newClientApiKeyEntity;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WeatherControllerApiIT {

    @MockBean
    GeoLocationApiService geoLocationApiService;
    @MockBean
    WeatherApiService weatherApiService;
    @MockBean
    ClientKeyRepository clientKeyRepository;

    @SpyBean
    GeoLocationRepository geoLocationRepository;
    @SpyBean
    WeatherRepository weatherRepository;

    @Autowired
    GeoLocationEntityService geoLocationEntityService;
    @Autowired
    WeatherEntityService weatherEntityService;
    @Autowired
    MockMvc mvc;


    private ClientApiKeyDto clientApiKeyDto;
    private GeoLocationDto geoLocationDto;
    private WeatherDto weatherDto;


    @BeforeEach
    void setUp() {
        clientApiKeyDto = newClientApiKeyDto(newClientApiKeyEntity(TEST_API_KEY));
        geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        weatherDto = newWeatherDto(newWeatherEntity());
    }


    @Test
    void cachedAfterFirstFindInDb() throws Exception {
        given(clientKeyRepository.findByApiKey(TEST_API_KEY)).willReturn(Optional.of(clientApiKeyDto));
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));

        doHttpGetRequestWith200();
        var geoLocationOne = geoLocationEntityService.findByHost(TEST_HOST);
        var geoLocationTwo = geoLocationEntityService.findByHost(TEST_HOST);
        var weatherOne = weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);
        var weatherTwo = weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertEquals(geoLocationOne, geoLocationTwo);
        assertEquals(weatherOne, weatherTwo);
        verify(geoLocationRepository, times(1)).findDtoByHost(TEST_HOST);
        verify(weatherRepository, times(1)).findDtoById(weatherDto.host());
    }

    @Test
    void cacheEvictRemovesValuesFromCache() throws Exception {
        given(clientKeyRepository.findByApiKey(TEST_API_KEY)).willReturn(Optional.of(clientApiKeyDto));
        given(geoLocationApiService.findByHost(TEST_HOST)).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));

        doHttpGetRequestWith200();
        geoLocationEntityService.findByHost(geoLocationDto.host());
        weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);
        doHttpGetRequestWith200();
        geoLocationEntityService.findByHost(geoLocationDto.host());
        weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);

        verify(geoLocationRepository, times(2)).findDtoByHost(TEST_HOST);
        verify(weatherRepository, times(2)).findDtoById(weatherDto.host());
    }

    @Test
    void whenRequestWithoutApiKey_thenReturnForbidden() throws Exception {
        mvc.perform(get(TEST_HTTP_LOCALHOST_WEATHER)
                        .header(TEST_X_API_KEY, TEST_API_KEY)
                        .header(TEST_X_FORWARDED_FOR, TEST_HOST))
                .andExpect(status().isForbidden());
    }

    private void doHttpGetRequestWith200() throws Exception {
        mvc.perform(get(TEST_HTTP_LOCALHOST_WEATHER)
                        .header(TEST_X_API_KEY, TEST_API_KEY)
                        .header(TEST_X_FORWARDED_FOR, TEST_HOST))
                .andExpect(status().isOk());
    }


}
