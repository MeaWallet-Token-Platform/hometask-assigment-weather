package com.paymentology.weather.adapter.out.client.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.paymentology.weather.adapter.out.client.mapper.IpApiResponseResolver;

/**
 * Represents the sealed response from the IP-API external service.
 * Will be deserialized polymorphically based on the 'status' field.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "status",
        defaultImpl = Fail.class
)
@JsonTypeIdResolver(IpApiResponseResolver.class)
public sealed interface IpApiResponse permits Success, Fail {

    /**
     * The status field used for polymorphic deserialization.
     * Expected values: "success" or "fail"
     */
    String status();

    /**
     * The original IP query in the response.
     */
    String query();
}
