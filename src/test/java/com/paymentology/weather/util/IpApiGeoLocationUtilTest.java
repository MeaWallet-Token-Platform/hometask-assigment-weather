package com.paymentology.weather.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.paymentology.weather.test.uti.TestUtil.TEST_HOST;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@ExtendWith({MockitoExtension.class})
class IpApiGeoLocationUtilTest {


    @InjectMocks
    IpApiGeoLocationUtil victim;

    @Test
    void findAddressByHost_whenFound_thenReturnOptionalOf() throws UnknownHostException {
        var localHost = InetAddress.getLocalHost();

        try (MockedStatic<InetAddress> mock = mockStatic(InetAddress.class)) {
            mock.when(() -> InetAddress.getByName(TEST_HOST)).thenReturn(localHost);

            var result = victim.findAddressByHost(TEST_HOST);

            assertTrue(result.isPresent());
        }
    }

    @Test
    void findAddressByHost_whenUnknownHostException_thenReturnOptionalEmpty() {
        try (MockedStatic<InetAddress> mock = mockStatic(InetAddress.class)) {
            mock.when(() -> InetAddress.getByName(TEST_HOST)).thenThrow(new UnknownHostException());

            var result = victim.findAddressByHost(TEST_HOST);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void findAddressByHost_whenSecurityException_thenReturnOptionalEmpty() {
        try (MockedStatic<InetAddress> mock = mockStatic(InetAddress.class)) {
            mock.when(() -> InetAddress.getByName(TEST_HOST)).thenThrow(new SecurityException());

            var result = victim.findAddressByHost(TEST_HOST);

            assertTrue(result.isEmpty());
        }
    }

}
