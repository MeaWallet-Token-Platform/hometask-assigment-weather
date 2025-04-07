package com.paymentology.weather.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for IP API integration.
 * Expected usage: ip-api.url in application.yml
 */
@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "ip-api")
public class IpApiConfig {

    @NotBlank
    @URL
    private String url;

}
