package com.paymentology.weather.infrastructure.api.openmeteo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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


    @Data
    @Accessors(chain = true)
    public static class CurrentWeather {

        @JsonProperty("temperature")
        Double temperature;

        @JsonProperty("windspeed")
        Double windSpeed;

        @JsonProperty("winddirection")
        Double windDirection;

    }

}
