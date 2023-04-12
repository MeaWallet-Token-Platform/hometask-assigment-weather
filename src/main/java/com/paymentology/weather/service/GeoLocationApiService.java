package com.paymentology.weather.service;

import com.paymentology.weather.model.GeoLocationDto;

import java.util.Optional;

public interface GeoLocationApiService {

    Optional<GeoLocationDto> findByHost(String host);


}
