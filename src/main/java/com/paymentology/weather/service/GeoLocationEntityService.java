package com.paymentology.weather.service;

import com.paymentology.weather.model.GeoLocationDto;

import java.util.Optional;

public interface GeoLocationEntityService {

    Optional<GeoLocationDto> findByHost(String host);

    GeoLocationDto save(GeoLocationDto geoLocationEntity);
}
