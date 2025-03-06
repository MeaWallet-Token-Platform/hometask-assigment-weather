package com.paymentology.weather.infra.location;

import com.paymentology.weather.domain.location.GeoLocation;
import com.paymentology.weather.domain.location.GeoLocationService;
import com.paymentology.weather.infra.cache.location.GeoLocationCacheService;
import com.paymentology.weather.infra.location.response.Fail;
import com.paymentology.weather.infra.location.response.IpApiResponse;
import com.paymentology.weather.infra.location.response.Success;
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
    private final GeoLocationCacheService geoLocationCacheService;

    @Autowired
    public IpApiGeoLocationService(IpApiConfig ipApiConfig, RestTemplate restTemplate,
                                   GeoLocationCacheService geoLocationCacheService) {
        this.ipApiConfig = ipApiConfig;
        this.restTemplate = restTemplate;
        this.geoLocationCacheService = geoLocationCacheService;
    }

    @Override
    public Optional<GeoLocation> detectByIp(InetAddress ip) {
        Optional<GeoLocation> cachedEntry = geoLocationCacheService.getCached(ip);
        if (cachedEntry.isPresent()) {
            return cachedEntry;
        }

        var response = tryDetect(ip);
        return switch (response) {
            case Success s -> {
                var geoLocation = new GeoLocation(s.lat(), s.lon());
                geoLocationCacheService.createOrUpdateCacheEntry(ip, geoLocation);
                yield Optional.of(geoLocation);
            }
            case Fail f -> {
                log.error("Failed to fetch geo location by ip {}, result {}", ip, f);
                yield Optional.empty();
            }
        };
    }

    private IpApiResponse tryDetect(InetAddress ip) {
        try {
            var url = String.format("%s/%s", ipApiConfig.getUrl(), ip.getHostAddress());
            return restTemplate.getForObject(url, IpApiResponse.class);
        } catch (Exception ex) {
            return failOf(ex, ip);
        }
    }

    private Fail failOf(Exception ex, InetAddress ip) {
        return switch (ex) {
            case RestClientResponseException e ->
                    new Fail("IpApi invocation status " + e.getStatusCode().value() + ". Message: " + e.getMessage(), ip.toString());
            default ->
                    new Fail("IpApi invocation failed with no status. Original message: " + ex.getMessage(), ip.toString());
        };
    }
}
