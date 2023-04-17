package com.paymentology.weather.config;

import com.paymentology.weather.properties.ConnectionProperties;
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


    ClientHttpRequestFactory requestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    public HttpClient httpClient() {
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager())
                .setDefaultRequestConfig(requestConfig())
                .build();
    }


    public PoolingHttpClientConnectionManager connectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(properties.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(properties.getMaxConnectionsPerRouteDefault());
        return connectionManager;
    }

    public RequestConfig requestConfig() {
        return RequestConfig
                .custom()
                .setConnectionRequestTimeout(ofMilliseconds(properties.getConnectionRequestTimeout()))
                .setConnectionKeepAlive(TimeValue.ofMilliseconds(properties.getConnectionKeepAlive()))
                .setConnectTimeout(ofMilliseconds(properties.getConnectionTimeout()))
                .setResponseTimeout(ofMilliseconds(properties.getResponseTimeout()))
                .build();
    }

}
