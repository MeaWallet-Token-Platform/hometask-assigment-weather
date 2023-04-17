package com.paymentology.weather.controller;

import com.paymentology.weather.test.config.TestRedisConfig;
import com.paymentology.weather.model.ClientApiKeyDto;
import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.repository.ClientKeyRepository;
import com.paymentology.weather.repository.GeoLocationRepository;
import com.paymentology.weather.repository.WeatherRepository;
import com.paymentology.weather.service.GeoLocationApiService;
import com.paymentology.weather.service.GeoLocationEntityService;
import com.paymentology.weather.service.WeatherApiService;
import com.paymentology.weather.service.WeatherEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.paymentology.weather.constant.TemperatureUnit.CELSIUS;
import static com.paymentology.weather.test.uti.TestUtil.newClientApiKeyDto;
import static com.paymentology.weather.test.uti.TestUtil.newClientApiKeyEntity;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newIpApiResponseDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherDto;
import static com.paymentology.weather.test.uti.TestUtil.newWeatherEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestRedisConfig.class)
@AutoConfigureMockMvc
//@AutoConfigureDataJpa
@ActiveProfiles("test")
class WeatherControllerIT {

    private String apiKey;
    private ClientApiKeyDto clientApiKeyDto;
    private String host;
    IpApiResponseDto ipApiResponseDto;

    @MockBean
    GeoLocationApiService geoLocationApiService;
    @MockBean
    WeatherApiService weatherApiService;


    @SpyBean
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

    @BeforeEach
    void setUp() {
        apiKey = "testApiKey";
        host = "testHost";
        clientApiKeyDto = newClientApiKeyDto(newClientApiKeyEntity(apiKey));
        ipApiResponseDto = newIpApiResponseDto("SUCCESS");
    }

    @Test
    void cachedAfterFirstFindInDb() throws Exception {
        var geoLocationDto = newGeoLocationDto(newGeoLocationEntity());
        var weatherDto = newWeatherDto(newWeatherEntity());
        given(clientKeyRepository.findByApiKey(apiKey)).willReturn(Optional.of(clientApiKeyDto));
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));


        MvcResult result = mvc.perform(get("http://localhost/weather")
                .header("X-API-KEY", apiKey)
                .header("X-Forwarded-For", host))
                .andExpect(status().isOk())
                .andReturn();

        var geoLocationOne = geoLocationEntityService.findByHost(host);
        var geoLocationTwo = geoLocationEntityService.findByHost(host);

        var weatherOne = weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);
        var weatherTwo = weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);

        assertEquals(geoLocationOne, geoLocationTwo);
        assertEquals(weatherOne, weatherTwo);
        verify(clientKeyRepository, times(1)).findByApiKey(apiKey);
        verify(geoLocationApiService, times(1)).findByHost(host);
        verify(weatherApiService, times(1)).findByLocationAndUnit(geoLocationDto, CELSIUS);
        verify(geoLocationRepository, times(1)).save(any());
        verify(geoLocationRepository, times(1)).findDtoByHost(host);
        verify(weatherRepository, times(1)).save(any());
        verify(weatherRepository, times(1)).findById(weatherDto.host());
    }

    @Test
    void cacheEvictRemovesValuesFromCache() throws Exception {
        var geoLocationEntity = newGeoLocationEntity();
        var geoLocationDto = newGeoLocationDto(geoLocationEntity);
        var weatherEntity = newWeatherEntity();
        var weatherDto = newWeatherDto(weatherEntity);given(clientKeyRepository.findByApiKey(apiKey)).willReturn(Optional.of(clientApiKeyDto));
        given(geoLocationApiService.findByHost(host)).willReturn(Optional.of(geoLocationDto));
        given(geoLocationRepository.findDtoByHost(geoLocationDto.host())).willReturn(Optional.of(geoLocationDto));
        given(weatherApiService.findByLocationAndUnit(geoLocationDto, CELSIUS)).willReturn(Optional.of(weatherDto));
        given(geoLocationRepository.save(geoLocationEntity)).willReturn(geoLocationEntity);
        given(weatherRepository.save(weatherEntity)).willReturn(weatherEntity);

        mvc.perform(get("http://localhost/weather")
                        .header("X-API-KEY", apiKey)
                        .header("X-Forwarded-For", host))
                .andExpect(status().isOk())
                .andReturn();

        geoLocationEntityService.findByHost(geoLocationDto.host());
        weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);

        mvc.perform(get("http://localhost/weather")
                        .header("X-API-KEY", apiKey)
                        .header("X-Forwarded-For", host))
                .andExpect(status().isOk())
                .andReturn();

        geoLocationEntityService.findByHost(geoLocationDto.host());
        weatherEntityService.findByLocationAndUnit(geoLocationDto, CELSIUS);

        verify(clientKeyRepository, times(2)).findByApiKey(apiKey);
        verify(geoLocationApiService, times(2)).findByHost(host);
        verify(weatherApiService, times(2)).findByLocationAndUnit(geoLocationDto, CELSIUS);
        verify(geoLocationRepository, times(2)).save(any());
        verify(geoLocationRepository, times(2)).findDtoByHost(host);
        verify(weatherRepository, times(2)).save(any());
        verify(weatherRepository, times(2)).findById(weatherDto.host());
    }
}