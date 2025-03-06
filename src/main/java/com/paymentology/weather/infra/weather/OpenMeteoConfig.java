package com.paymentology.weather.infra.weather;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "open-meteo")
public class OpenMeteoConfig {

    private final String url;

    @ConstructorBinding
    public OpenMeteoConfig(@URL @NotBlank String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
