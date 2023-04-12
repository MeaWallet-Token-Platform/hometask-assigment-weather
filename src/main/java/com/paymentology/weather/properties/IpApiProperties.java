package com.paymentology.weather.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "ip-api")
@Validated
@Data
public class IpApiProperties {

    @URL
    @NotBlank
    private String urlJson;

}
