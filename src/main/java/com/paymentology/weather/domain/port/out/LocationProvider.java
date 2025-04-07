package com.paymentology.weather.domain.port.out;

import com.paymentology.weather.domain.model.GeoLocation;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

public interface LocationProvider {
    /**
     * Detects the geographic location by IP address.
     *
     * @param ip The InetAddress to be resolved
     * @return A Mono emitting the resolved GeoLocation, or empty if detection fails
     */
    Mono<GeoLocation> detectByIp(InetAddress ip);}
