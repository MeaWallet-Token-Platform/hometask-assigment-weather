package com.paymentology.weather.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "connection")
@Data
@Component
@Validated
public class ConnectionProperties {

    @NotNull
    private Integer maxTotalConnections;
    @NotNull
    private Integer maxConnectionsPerRouteDefault;
    @NotNull
    private Integer connectionRequestTimeout;
    @NotNull
    private Integer socketTimeout;
    @NotNull
    private Integer connectionTimeout;
    @NotNull
    private Integer responseTimeout;
    @NotNull
    private Long connectionKeepAlive;

}
