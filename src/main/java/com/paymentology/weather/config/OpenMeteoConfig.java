package com.paymentology.weather.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


/**
 * Configuration properties for Open-Meteo API.
 * Maps from: open-meteo.url in application.yml
 */
@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "open-meteo")
public class OpenMeteoConfig {

    @NotBlank
    @URL
    private String url;

}
