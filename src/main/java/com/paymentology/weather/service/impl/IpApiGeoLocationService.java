package com.paymentology.weather.service.impl;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.IpApiResponseDto;
import com.paymentology.weather.properties.IpApiProperties;
import com.paymentology.weather.service.GeoLocationApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpApiGeoLocationService implements GeoLocationApiService {


    private int counter = 0;
    public static final String SUCCESS = "success";
    public static final String UNABLE_TO_DETERMINE = "Unable to determine request IP address: ";

    private final IpApiProperties properties;
    private final RestTemplate restTemplate;

    @Override
    public Optional<GeoLocationDto> findByHost(String host) {
        var inetAddressOptional = findAddressByHost(host);

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

            counter++;

            if (counter >= 3) {
                counter = 0;
                return Optional.empty();
            } else {
                return Optional.of(geoLocation);
            }

        } catch (RestClientException ex) {
            log.warn(this.getClass().getSimpleName() + " caught exception on api call: " + ex);
            return Optional.empty();
        }

    }

    private Optional<InetAddress> findAddressByHost(String host) {
        try {
            return Optional.ofNullable(InetAddress.getByName(host));
        } catch (UnknownHostException | SecurityException e) {
            log.warn(UNABLE_TO_DETERMINE + host, e);
            return Optional.empty();
        }
    }

}
