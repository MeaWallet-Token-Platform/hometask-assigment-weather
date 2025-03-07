package com.paymentology.weather.infra.location;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.location.GeoLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.Optional;

@Service
public class IpApiGeoLocationService implements GeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(IpApiGeoLocationService.class);

    private final IpApiConfig ipApiConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public IpApiGeoLocationService(IpApiConfig ipApiConfig, RestTemplate restTemplate) {
        this.ipApiConfig = ipApiConfig;
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<GeoLocation> detectByIp(InetAddress ip) {
        var response = tryDetect(ip);
        return switch (response) {
            case Success s -> Optional.of(new GeoLocation(s.lat(), s.lon()));
            case Fail f -> {
                log.error("Failed to fetch geo location by ip {}, result {}", ip.getHostAddress(), f);
                yield Optional.empty();
            }
        };
    }

    private IpApiResponse tryDetect(InetAddress ip) {
        try {
            String baseUrl = ipApiConfig.getUrl();
            String ipAddress = ip.getHostAddress();
            String url = baseUrl.endsWith("/") ? baseUrl + ipAddress : baseUrl + "/" + ipAddress;
            return restTemplate.getForObject(url, IpApiResponse.class);
        } catch (Throwable t) {
            return failOf(t, ip);
        }
    }

    private Fail failOf(Throwable err, InetAddress ip) {
        String message = switch (err) {
            case RestClientResponseException e ->
                    "IpApi invocation failed: " + e.getStatusCode().value() + " " + e.getStatusText();
            default ->
                    "IpApi invocation failed: " + err.getMessage();
        };
        return new Fail(message, ip.getHostAddress());
    }
}
