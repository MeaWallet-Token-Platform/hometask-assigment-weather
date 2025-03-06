package com.paymentology.weather.infra.location;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.infra.cache.location.GeoLocationCacheService;
import com.paymentology.weather.infra.location.response.Fail;
import com.paymentology.weather.infra.location.response.IpApiResponse;
import com.paymentology.weather.infra.location.response.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IpApiGeoLocationServiceTest {

    private static final String URL = "http://ip-api.com/json";

    @Mock
    private IpApiConfig ipApiConfig;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private GeoLocationCacheService geoLocationCacheService;

    @InjectMocks
    private IpApiGeoLocationService victim;

    @BeforeEach
    void setUp() {
        given(ipApiConfig.getUrl()).willReturn(URL);
    }

    @Test
    public void success_noCache() throws Exception {
        given(restTemplate.getForObject(URL + "/81.198.87.60", IpApiResponse.class))
                .willReturn(new Success(
                        "81.198.87.60",
                        "Latvia",
                        "LV",
                        "RIX",
                        "Riga",
                        "Riga",
                        "LV-1063",
                        56.9496,
                        24.0978,
                        "Europe/Riga",
                        "Lattelekom",
                        "Bridge Group",
                        "AS12578 SIA Tet"
                ));
        var inetAddress = InetAddress.getByName("81.198.87.60");

        var result = victim.detectByIp(inetAddress);

        var expectedGeolocation = new GeoLocation(56.9496, 24.0978);
        assertEquals(Optional.of(expectedGeolocation), result);
        verify(geoLocationCacheService).createOrUpdateCacheEntry(inetAddress, expectedGeolocation);
    }

    @Test
    public void error200_noCache() throws Exception {
        given(restTemplate.getForObject(URL + "json/0.0.0.0", IpApiResponse.class))
                .willReturn(new Fail("reserved range", "0.0.0.0"));
        var result = victim.detectByIp(InetAddress.getByName("0.0.0.0"));
        assertEquals(Optional.empty(), result);
    }

    @Test
    void error4xx() throws Exception {
        given(restTemplate.getForObject(URL + "/81.198.87.60", IpApiResponse.class))
                .willThrow(new ResponseStatusException(HttpStatusCode.valueOf(404)));
        var result = victim.detectByIp(InetAddress.getByName("81.198.87.60"));
        assertEquals(Optional.empty(), result);
    }

    @Test
    void errorThrowable() throws Exception {
        given(restTemplate.getForObject(URL + "/81.198.87.60", IpApiResponse.class))
                .willThrow(new RestClientException("mock"));
        var result = victim.detectByIp(InetAddress.getByName("81.198.87.60"));
        assertEquals(Optional.empty(), result);
    }
}