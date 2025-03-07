package com.paymentology.weather.infra.weather;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class OpenMeteoConfig {

    @URL
    @NotBlank
    private String url;

}
