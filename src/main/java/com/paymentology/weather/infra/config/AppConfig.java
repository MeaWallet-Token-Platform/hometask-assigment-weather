package com.paymentology.weather.infra.config;

import com.paymentology.weather.infra.location.IpApiConfig;
import com.paymentology.weather.infra.weather.OpenMeteoConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConfigurationProperties(prefix = "ip-api")
    @Validated
    public IpApiConfig ipApiConfig() {
        return new IpApiConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "open-meteo")
    @Validated
    public OpenMeteoConfig openMeteoConfig() {
        return new OpenMeteoConfig();
    }

}