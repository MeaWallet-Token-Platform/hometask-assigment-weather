package com.paymentology.weather;

import com.paymentology.weather.infra.location.IpApiConfig;
import com.paymentology.weather.infra.weather.OpenMeteoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.paymentology.weather.infra.*")
@EntityScan(basePackages = "com.paymentology.weather.infra.*")
@EnableConfigurationProperties(value = {OpenMeteoConfig.class, IpApiConfig.class})
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
