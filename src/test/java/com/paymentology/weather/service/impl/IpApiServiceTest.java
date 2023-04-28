package com.paymentology.weather.service.impl;

import com.paymentology.weather.infrastructure.api.ipapi.IpApiProperties;
import com.paymentology.weather.infrastructure.api.ipapi.IpApiResponseDto;
import com.paymentology.weather.infrastructure.api.ipapi.IpApiService;
import com.paymentology.weather.infrastructure.api.ipapi.IpApiUtil;
import com.paymentology.weather.infrastructure.service.RestTemplateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.paymentology.weather.test.uti.TestUtil.IP_API_RESPONSE_STATUS_FAIL;
import static com.paymentology.weather.test.uti.TestUtil.IP_API_RESPONSE_STATUS_SUCCESS;
import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static com.paymentology.weather.test.uti.TestUtil.TEST_IP;
import static com.paymentology.weather.test.uti.TestUtil.TEST_URL;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationDto;
import static com.paymentology.weather.test.uti.TestUtil.newGeoLocationEntity;
import static com.paymentology.weather.test.uti.TestUtil.newIpApiResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class IpApiServiceTest {


    @Mock
    IpApiProperties properties;
    @Mock
    RestTemplateService<IpApiResponseDto> restTemplateService;
    @Mock
    IpApiUtil util;

    @InjectMocks
    IpApiService victim;


    private URI uri;


    @BeforeEach
    void setUp() throws URISyntaxException {
        uri = new URI(TEST_URL);
    }

    @AfterEach
    void windDown() {
        verifyNoMoreInteractions(properties, restTemplateService, util);
    }

    @Test
    void findByHost_whenCannotFindAddressFromHost_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.empty());

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

/*    @Test
    void findByHost_whenRestTemplateThrowsException_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        var inetAddressMock = mock(InetAddress.class);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.ofNullable(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);

        given(restTemplateService.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP))
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

        given(restTemplateService.getForObject(TEST_URL, IpApiResponseDto.class, TEST_IP)).willReturn(null);

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }*/

    @Test
    void findByHost_whenRestTemplateResponseIsFail_thenReturnOptionalEmpty() {
        var expected = Optional.empty();
        var inetAddressMock = mock(InetAddress.class);
        var ipApiResponseDto = newIpApiResponseDto(IP_API_RESPONSE_STATUS_FAIL);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.of(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);
        given(util.getJsonUri(TEST_IP, TEST_URL)).willReturn(uri);
        given(properties.getUrlJson()).willReturn(TEST_URL);
        given(restTemplateService.getForObject(uri, IpApiResponseDto.class))
                .willReturn(Optional.of(ipApiResponseDto));

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

    @Test
    void findByHost_whenPositiveRequest_thenReturnOptionalOf() {
        var expected = Optional.of(newGeoLocationDto(newGeoLocationEntity()));
        var inetAddressMock = mock(InetAddress.class);
        var ipApiResponseDto = newIpApiResponseDto(IP_API_RESPONSE_STATUS_SUCCESS);
        given(util.findAddressByHost(TEST_HOST)).willReturn(Optional.of(inetAddressMock));
        given(inetAddressMock.getHostAddress()).willReturn(TEST_IP);
        given(properties.getUrlJson()).willReturn(TEST_URL);
        given(util.getJsonUri(TEST_IP, TEST_URL)).willReturn(uri);
        given(properties.getUrlJson()).willReturn(TEST_URL);
        given(restTemplateService.getForObject(uri, IpApiResponseDto.class))
                .willReturn(Optional.of(ipApiResponseDto));

        var result = victim.findByHost(TEST_HOST);

        assertEquals(expected, result);
    }

}
