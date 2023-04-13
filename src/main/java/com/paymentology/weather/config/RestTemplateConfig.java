package com.paymentology.weather.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        var template = new RestTemplate();

        template.getInterceptors()
                .add(new WeatherClientHttpRequestInterceptor());

        return template;
    }
}
