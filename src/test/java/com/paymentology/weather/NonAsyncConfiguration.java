package com.paymentology.weather;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("synchronous")
public class NonAsyncConfiguration {
}
