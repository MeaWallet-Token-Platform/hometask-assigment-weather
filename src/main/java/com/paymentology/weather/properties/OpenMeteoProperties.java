package com.paymentology.weather.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "open-meteo")
@Validated
@Data
public class OpenMeteoProperties {

    @URL
    @NotBlank
    private String currentWeatherUrl;
}
