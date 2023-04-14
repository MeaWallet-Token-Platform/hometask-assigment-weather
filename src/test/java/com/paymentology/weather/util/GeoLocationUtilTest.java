package com.paymentology.weather.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeoLocationUtilTest {

    @InjectMocks
    GeoLocationUtil victim;

    @Test
    void getStandardLocation_whenRequest_thenReturnResponse() {
        var testHost = "testHost";
        var result = victim.getStandardLocation(testHost);

        assertEquals(testHost, result.host());
        assertNotNull(result.latitude());
        assertNotNull(result.longitude());
    }

}