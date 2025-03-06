package com.paymentology.weather.infra;

import com.paymentology.weather.infra.location.IpApiConfig;
import com.paymentology.weather.infra.location.response.Success;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled("Manual test to test API contract")
@SpringBootTest
public class IpApiManualTest {

    @Autowired
    private IpApiConfig ipApiConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    void verifyIpApiResponseIsMappedToSuccess() {
        String ipString = "81.198.20.21";
        var url = String.format("%s/%s", ipApiConfig.getUrl(), ipString);
        var response = restTemplate.getForEntity(url, Success.class);
        assertNotNull(response);
    }
}
