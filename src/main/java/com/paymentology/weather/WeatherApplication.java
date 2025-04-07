package com.paymentology.weather;

import com.paymentology.weather.config.IpApiConfig;
import com.paymentology.weather.config.OpenMeteoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        OpenMeteoConfig.class,
        IpApiConfig.class
})
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
}
