package com.paymentology.weather.infra.location;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class IpApiConfig {

    @URL
    @NotBlank
    private String url;

}
