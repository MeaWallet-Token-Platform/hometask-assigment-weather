package com.paymentology.weather.service.impl;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.properties.IpApiProperties;
import com.paymentology.weather.service.GeoLocationApiService;
import com.paymentology.weather.util.IpApiGeoLocationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpApiGeoLocationService implements GeoLocationApiService {


    public static final String SUCCESS = "success";


    private final IpApiProperties properties;
    private final RestTemplate restTemplate;
    private final IpApiGeoLocationUtil util;


    @Override
    public Optional<GeoLocationDto> findByHost(String host) {
        var inetAddressOptional = util.findAddressByHost(host);

        if (inetAddressOptional.isEmpty()) {
            return Optional.empty();
        }

        var ipAddress = inetAddressOptional.get().getHostAddress();

        try {
            var ipApiResponseDto = restTemplate
                    .getForObject(properties.getUrlJson(), IpApiResponseDto.class, ipAddress);

            if (ipApiResponseDto == null || !SUCCESS.equalsIgnoreCase(ipApiResponseDto.status())) {
                log.warn(this.getClass().getSimpleName() + " request failed: " + ipApiResponseDto);
                return Optional.empty();
            }

            var geoLocation = new GeoLocationDto(host, ipApiResponseDto);

            return Optional.of(geoLocation);

        } catch (RestClientException ex) {
            log.warn(this.getClass().getSimpleName() + " caught exception: " + ex);
            return Optional.empty();
        }

    }

}
