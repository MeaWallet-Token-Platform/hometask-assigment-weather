package com.paymentology.weather.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;


@Component
@Slf4j
public class IpApiGeoLocationUtil {


    public static final String UNABLE_TO_DETERMINE = "Unable to determine request IP address: ";


    public Optional<InetAddress> findAddressByHost(String host) {
        try {
            return Optional.ofNullable(InetAddress.getByName(host));
        } catch (UnknownHostException | SecurityException e) {
            log.warn(UNABLE_TO_DETERMINE + host, e);
            return Optional.empty();
        }
    }

}
