package com.paymentology.weather.infrastructure.config;

import com.paymentology.weather.infrastructure.properties.ConnectionProperties;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.apache.hc.core5.util.Timeout.ofMilliseconds;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final ConnectionProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        var template = new RestTemplate(requestFactory());

        template.getInterceptors()
                .add(new WeatherClientHttpRequestInterceptor());

        return template;
    }


    private ClientHttpRequestFactory requestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager())
                .setDefaultRequestConfig(requestConfig())
                .build();
    }


    private PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(properties.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(properties.getMaxConnectionsPerRouteDefault());
        return connectionManager;
    }

    private RequestConfig requestConfig() {
        return RequestConfig
                .custom()
                .setConnectionRequestTimeout(ofMilliseconds(properties.getConnectionRequestTimeout()))
                .setConnectionKeepAlive(TimeValue.ofMilliseconds(properties.getConnectionKeepAlive()))
                .setConnectTimeout(ofMilliseconds(properties.getConnectionTimeout()))
                .setResponseTimeout(ofMilliseconds(properties.getResponseTimeout()))
                .build();
    }

}
