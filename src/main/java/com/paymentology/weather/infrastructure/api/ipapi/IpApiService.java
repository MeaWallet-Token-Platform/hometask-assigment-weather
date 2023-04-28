package com.paymentology.weather.infrastructure.api.ipapi;

import com.paymentology.weather.core.model.GeoLocationDto;
import com.paymentology.weather.core.service.GeoLocationApiService;
import com.paymentology.weather.infrastructure.service.RestTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IpApiService implements GeoLocationApiService {


    public static final String SUCCESS = "success";


    private final IpApiProperties properties;
    private final RestTemplateService<IpApiResponseDto> restTemplateService;
    private final IpApiUtil util;


    @Override
    public Optional<GeoLocationDto> findByHost(String host) {
        var inetAddressOptional = util.findAddressByHost(host);

        if (inetAddressOptional.isEmpty()) {
            return Optional.empty();
        }

        var ipAddress = inetAddressOptional.get().getHostAddress();

        var uri = util.getJsonUri(ipAddress, properties.getUrlJson());

        var geoLocationDtoOptional =  restTemplateService
                .getForObject(uri, IpApiResponseDto.class);

        if (geoLocationDtoOptional.isEmpty() || !SUCCESS.equalsIgnoreCase(geoLocationDtoOptional.get().status())) {
            return Optional.empty();
        }

        return geoLocationDtoOptional
                .map(response -> new GeoLocationDto(host, response));
    }


}
