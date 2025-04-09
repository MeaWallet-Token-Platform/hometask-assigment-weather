package com.paymentology.weather.domain.weather;

public final class model {
    private model() {
    }

    public record Temperature(double value, TemperatureUnit unit) {
    }

    public enum TemperatureUnit {
        Celsius,
        Fahrenheit,
        Kelvin;

        public static TemperatureUnit fromString(String text) {
            if (text == null || text.isEmpty()) {
                return Celsius; // Default value
            }

            try {
                return valueOf(text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid temperature unit: " + text, e);
            }
        }

        public String toApiString() {
            return switch (this) {
                case Celsius -> "celsius";
                case Fahrenheit -> "fahrenheit";
                case Kelvin -> throw new UnsupportedOperationException("OpenMeteo API does not support Kelvin");
            };
        }
    }


    public record Wind(double speed, double direction) {
    }

    public record Weather(Temperature temperature, Wind wind) {
        public Weather withTemperatureUnit(TemperatureUnit requestedUnit) {
            if (temperature.unit().equals(requestedUnit)) {
                return this;
            }

            return new Weather(
                    new Temperature(
                            TemperatureConverter.convertTemperatureUnit(this, requestedUnit),
                            requestedUnit),
                    wind
            );
        }
    }
}
