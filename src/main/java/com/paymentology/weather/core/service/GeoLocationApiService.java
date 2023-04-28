package com.paymentology.weather.core.service;

import com.paymentology.weather.core.model.GeoLocationDto;

import java.util.Optional;

public interface GeoLocationApiService {

   Optional<GeoLocationDto> findByHost(String host);

}
