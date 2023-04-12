package com.paymentology.weather.service.impl;

import com.paymentology.weather.properties.IpApiProperties;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.service.GeoLocationApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpApiGeoLocationService implements GeoLocationApiService {

    public static final String SUCCESS = "success";

    private final IpApiProperties properties;
    private final RestTemplate restTemplate;


    @Override
    public Optional<GeoLocationDto> findByHost(String host) {
        Optional<InetAddress> inetAddressOptional = findAddressByHost(host);

        if (inetAddressOptional.isEmpty()) {
            return Optional.empty();
        }

        var ipAddress = inetAddressOptional.get().getHostAddress();

        var response = restTemplate
                .getForObject(properties.getUrlJson(), IpApiResponseDto.class, ipAddress);

        if (response == null || !SUCCESS.equalsIgnoreCase(response.getStatus())) {
            return Optional.empty();
        }

        var geoLocation = new GeoLocationDto(host, response);

        return Optional.of(geoLocation);
    }

    private static Optional<InetAddress> findAddressByHost(String host) {
        try {
            return Optional.ofNullable(InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            log.info("Unable to determine request IP address: " + host, e);
            return Optional.empty();
        }
    }

}
