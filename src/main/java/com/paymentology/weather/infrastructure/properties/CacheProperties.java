package com.paymentology.weather.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "cache")
@Validated
@Component
@Data
public class CacheProperties {

    @NotNull
    private Integer ttl;
}
