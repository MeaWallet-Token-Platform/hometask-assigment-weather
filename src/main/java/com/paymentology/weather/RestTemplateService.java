package com.paymentology.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestTemplateService<T> {

    public static final String EXCEPTION_MESSAGE = "%s.getForObject() with object of type %s caught exception: %s";
    private final RestTemplate restTemplate;

    public Optional<T> getForObject(URI uri, Class<T> t) {
        try {
            return Optional.ofNullable(restTemplate.getForObject(uri, t));
        } catch (RestClientException ex) {
            log.warn(EXCEPTION_MESSAGE.formatted(this.getClass().getSimpleName(), t, ex));
            return Optional.empty();
        }
    }

}
