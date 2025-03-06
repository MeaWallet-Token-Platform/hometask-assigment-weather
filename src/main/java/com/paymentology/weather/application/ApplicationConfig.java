package com.paymentology.weather.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ApplicationConfig {

    @Value("${api.connect-timeout-duration}")
    private Duration connectTimeoutMillis;

    @Value("${api.read-timeout-duration}")
    private Duration readTimeoutMillis;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder
                .setConnectTimeout(connectTimeoutMillis)
                .setReadTimeout(readTimeoutMillis)
                .build();
    }
}
