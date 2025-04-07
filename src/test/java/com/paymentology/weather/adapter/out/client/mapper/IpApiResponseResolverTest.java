package com.paymentology.weather.adapter.out.client.mapper;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.paymentology.weather.adapter.out.client.response.Fail;
import com.paymentology.weather.adapter.out.client.response.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IpApiResponseResolverTest {

    private IpApiResponseResolver resolver;
    private DatabindContext context;

    @BeforeEach
    void setUp() {
        resolver = new IpApiResponseResolver();
        context = mock(DatabindContext.class);
    }

    @Test
    void idFromValue_shouldReturnStatus() {
        Success success = new Success(
                "8.8.8.8",
                "USA",
                "US",
                "CA",
                "California",
                "Mountain View",
                "94043",
                37.751,
                -97.822,
                "America/Los_Angeles",
                "Google",
                "Google Inc.",
                "AS15169 Google LLC"
        );
        String status = resolver.idFromValue(success);
        assertEquals("success", status);
    }

    @Test
    void idFromValue_shouldReturnFailStatus() {
        Fail fail = new Fail("some error", "8.8.8.8");
        String status = resolver.idFromValue(fail);
        assertEquals("fail", status);
    }

    @Test
    void typeFromId_shouldReturnCorrectType_forSuccess() throws Exception {
        JavaType javaType = resolver.typeFromId(context, "success");
        assertEquals(Success.class, javaType.getRawClass());
    }

    @Test
    void typeFromId_shouldReturnCorrectType_forFail() throws Exception {
        JavaType javaType = resolver.typeFromId(context, "fail");
        assertEquals(Fail.class, javaType.getRawClass());
    }

    @Test
    void typeFromId_shouldThrowException_forUnknownType() {
        assertThrows(IllegalArgumentException.class,
                () -> resolver.typeFromId(context, "unknown"));
    }
}
