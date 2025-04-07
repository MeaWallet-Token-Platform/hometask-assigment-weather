package com.paymentology.weather.application.service;

import com.paymentology.weather.domain.model.GeoLocation;
import com.paymentology.weather.domain.port.in.GeoLocationUseCase;
import com.paymentology.weather.domain.port.out.LocationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoLocationServiceImpl implements GeoLocationUseCase {

    private final LocationProvider locationProvider;

    @Override
    public Mono<GeoLocation> detectByIp(InetAddress ip) {
        return locationProvider.detectByIp(ip)
                .doOnError(e -> log.error("Failed to resolve IP or fetch geolocation: {}", ip, e));
    }
}
