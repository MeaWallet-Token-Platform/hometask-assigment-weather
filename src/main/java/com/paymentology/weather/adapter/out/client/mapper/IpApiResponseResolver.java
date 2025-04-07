package com.paymentology.weather.adapter.out.client.mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.paymentology.weather.adapter.out.client.response.Fail;
import com.paymentology.weather.adapter.out.client.response.IpApiResponse;
import com.paymentology.weather.adapter.out.client.response.Success;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Custom type resolver for IP-API responses.
 * It switches between Success and Fail response types based on the 'status' field.
 */
@Slf4j
public class IpApiResponseResolver extends TypeIdResolverBase {

    @Override
    public String idFromValue(Object value) {
        if (value instanceof IpApiResponse response) {
            return response.status();
        }
        throw new IllegalArgumentException("Unexpected type: " + value.getClass().getName());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return switch (id.toLowerCase()) {
            case Success.STATUS -> TypeFactory.defaultInstance().constructType(Success.class);
            case Fail.STATUS -> TypeFactory.defaultInstance().constructType(Fail.class);
            default -> {
                log.warn("Unknown status type: {}", id);
                throw new IllegalArgumentException("Unknown status type: " + id);
            }
        };
    }
}
