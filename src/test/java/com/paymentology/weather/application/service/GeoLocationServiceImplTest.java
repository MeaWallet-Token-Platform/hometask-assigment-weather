package com.paymentology.weather.application.service;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.port.out.LocationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetAddress;

import static org.mockito.Mockito.*;

class GeoLocationServiceImplTest {

    private LocationProvider locationProvider;
    private GeoLocationServiceImpl geoLocationService;

    @BeforeEach
    void setUp() {
        locationProvider = mock(LocationProvider.class);
        geoLocationService = new GeoLocationServiceImpl(locationProvider);
    }

    @Test
    void detectByIp_shouldReturnGeoLocation_whenLocationProviderSucceeds() throws Exception {
        InetAddress ip = InetAddress.getByName("8.8.8.8");
        GeoLocation location = new GeoLocation(40.0, 49.0);

        when(locationProvider.detectByIp(ip)).thenReturn(Mono.just(location));

        StepVerifier.create(geoLocationService.detectByIp(ip))
                .expectNext(location)
                .verifyComplete();

        verify(locationProvider).detectByIp(ip);
    }

    @Test
    void detectByIp_shouldReturnError_whenLocationProviderFails() throws Exception {
        InetAddress ip = InetAddress.getByName("1.1.1.1");
        RuntimeException error = new RuntimeException("Service unavailable");

        when(locationProvider.detectByIp(ip)).thenReturn(Mono.error(error));

        StepVerifier.create(geoLocationService.detectByIp(ip))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Service unavailable")
                ).verify();

        verify(locationProvider).detectByIp(ip);
    }
}
