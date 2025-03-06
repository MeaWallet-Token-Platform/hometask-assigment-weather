package com.paymentology.weather.infra.location;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ip-api")
public class IpApiConfig {

    @URL
    @NotBlank
    private final String url;

    @ConstructorBinding
    public IpApiConfig(@URL @NotBlank String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
