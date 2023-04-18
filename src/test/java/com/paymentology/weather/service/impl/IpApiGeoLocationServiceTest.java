package com.paymentology.weather.service.impl;

import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.properties.IpApiProperties;
import com.paymentology.weather.util.IpApiGeoLocationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.Optional;

import static com.paymentology.weather.test.uti.TestUtil.IP_API_RESPONSE_STATUS_FAIL;
import static com.paymentology.weather.test.uti.TestUtil.IP_API_RESPONSE_STATUS_SUCCESS;
import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static com.paymentology.weather.test.uti.TestUtil.TEST_IP;
import static com.paymentology.weather.test.uti.TestUtil.TEST_URL;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newIpApiResponseDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class IpApiGeoLocationServiceTest {


    @Mock
    IpApiProperties properties;
    @Mock
    RestTemplate restTemplate;
    @Mock
    IpApiGeoLocationUtil util;

    @InjectMocks
    IpApiGeoLocationService victim;


    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(properties, restTemplate, util);
    }

    @Test
    void findByHost_whenCannotFindAddressFromHost_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.empty());

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByHost_whenRestTemplateThrowsException_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        var inetAddressMock = mock(InetAddress.class);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.ofNullable(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);

        given(restTemplate.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP))
                .willThrow(new RestClientException("exceptionMessage"));

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByHost_whenRestTemplateReturnsNull_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        var inetAddressMock = mock(InetAddress.class);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.ofNullable(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);

        given(restTemplate.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP)).willReturn(null);

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByHost_whenRestTemplateResponseIsFail_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        var inetAddressMock = mock(InetAddress.class);
        var ipApiResponseDto = newIpApiResponseDto(IP_API_RESPONSE_STATUS_FAIL);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.ofNullable(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);

        given(restTemplate.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP)).willReturn(ipApiResponseDto);

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByHost_whenPositiveRequest_thenReturnOptionalOf() {
        var expected = Optional.of(newGeoLocationDto(newGeoLocationEntity()));
        var inetAddressMock = mock(InetAddress.class);
        var ipApiResponseDto = newIpApiResponseDto(IP_API_RESPONSE_STATUS_SUCCESS);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.ofNullable(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);

        given(restTemplate.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP)).willReturn(ipApiResponseDto);

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

}