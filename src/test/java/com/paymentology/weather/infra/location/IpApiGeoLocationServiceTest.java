package com.paymentology.weather.infra.location;

import com.paymentology.weather.domain.location.GeoLocation;
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

@ExtendWith(MockitoExtension.class)
class IpApiGeoLocationServiceTest {

    private static final String BASE_URL = "http://ip-api.com/json";

    @Mock
    IpApiConfig ipApiConfig;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    IpApiGeoLocationService ipApiGeoLocationService;

    @BeforeEach
    void setUp() {
        given(ipApiConfig.getUrl()).willReturn(BASE_URL);
    }

    @Test
    void shouldReturnSuccess() throws Exception {
        // given
        String ip = "81.198.87.60";
        String url = BASE_URL + "/" + ip;
        Success response = new Success(
                ip,
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
        );
        given(restTemplate.getForObject(url, IpApiResponse.class))
                .willReturn(response);

        var result = ipApiGeoLocationService.detectByIp(InetAddress.getByName(ip));

        assertEquals(Optional.of(new GeoLocation(56.9496, 24.0978)), result);
    }

    @Test
    void shouldReturnReservedRangeError() throws Exception {
        String ip = "0.0.0.0";
        String url = BASE_URL + "/" + ip;
        given(restTemplate.getForObject(url, IpApiResponse.class))
                .willReturn(new Fail("reserved range", ip));

        var result = ipApiGeoLocationService.detectByIp(InetAddress.getByName(ip));

        assertEquals(Optional.empty(), result);
    }

    @Test
    void shouldReturnNotFoundError() throws Exception {
        String ip = "93.139.203.118";
        String url = BASE_URL + "/" + ip;
        given(restTemplate.getForObject(url, IpApiResponse.class))
                .willThrow(new ResponseStatusException(HttpStatusCode.valueOf(404)));

        var result = ipApiGeoLocationService.detectByIp(InetAddress.getByName(ip));

        assertEquals(Optional.empty(), result);
    }

    @Test
    void shouldReturnErrorThrowable() throws Exception {
        String ip = "93.139.203.118";
        String url = BASE_URL + "/" + ip;
        given(restTemplate.getForObject(url, IpApiResponse.class))
                .willThrow(new RestClientException("mock"));

        var result = ipApiGeoLocationService.detectByIp(InetAddress.getByName(ip));

        assertEquals(Optional.empty(), result);
    }
}