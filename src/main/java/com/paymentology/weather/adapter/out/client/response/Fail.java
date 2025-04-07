package com.paymentology.weather.adapter.out.client.response;

/**
 * Represents a failed response from the IP-API service.
 * Example JSON: {"status": "fail", "message": "...", "query": "..."}
 */
public record Fail(String message, String query) implements IpApiResponse {

    public static final String STATUS = "fail";

    @Override
    public String status() {
        return STATUS;
    }
}
