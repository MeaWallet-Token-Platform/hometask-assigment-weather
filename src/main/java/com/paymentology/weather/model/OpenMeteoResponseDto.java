package com.paymentology.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class OpenMeteoResponseDto {

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    public Double getTemperature() {
        return currentWeather.temperature;
    }

    public Double getWindSpeed() {
        return currentWeather.windSpeed;
    }

    public Double getWindDirection() {
        return currentWeather.windDirection;
    }


    static class CurrentWeather {

        @JsonProperty("temperature")
        Double temperature;

        @JsonProperty("windspeed")
        Double windSpeed;

        @JsonProperty("winddirection")
        Double windDirection;
    }
}
