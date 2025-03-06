package com.paymentology.weather.domain.weather;

import java.time.Instant;

public record Weather(Temperature temperature, Wind wind, Instant timestamp) {
}
