package com.paymentology.weather.adapter.out.client.response;

/**
 * Represents a successful response from the IP-API service.
 * Example JSON: {
 *   "status": "success",
 *   "query": "8.8.8.8",
 *   "lat": 37.751,
 *   "lon": -97.822,
 *   ...
 * }
 */
public record Success(
        String query,
        String country,
        String countryCode,
        String region,
        String regionName,
        String city,
        String zip,
        double lat,
        double lon,
        String timezone,
        String isp,
        String org,
        String as
) implements IpApiResponse {

    public static final String STATUS = "success";

    @Override
    public String status() {
        return STATUS;
    }
}
