package com.paymentology.weather.adapter.out.client;

import com.paymentology.weather.adapter.out.cache.GeoLocationCacheAdapter;
import com.paymentology.weather.adapter.out.client.response.Fail;
import com.paymentology.weather.adapter.out.client.response.IpApiResponse;
import com.paymentology.weather.adapter.out.client.response.Success;
import com.paymentology.weather.config.IpApiConfig;
import com.paymentology.weather.domain.model.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetAddress;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class IpApiGeoLocationClientAdapterTest {

    private IpApiConfig ipApiConfig;
    private WebClient webClient;
    private GeoLocationCacheAdapter cache;
    private IpApiGeoLocationClientAdapter adapter;

    private WebClient.RequestHeadersUriSpec uriSpec;
    private WebClient.RequestHeadersSpec headersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ipApiConfig = mock(IpApiConfig.class);
        webClient = mock(WebClient.class);
        cache = mock(GeoLocationCacheAdapter.class);

        uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        adapter = new IpApiGeoLocationClientAdapter(ipApiConfig, webClient, cache);
    }

    @Test
    void detectByIp_shouldReturnFromCache_whenExists() throws Exception {
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        GeoLocation cachedLocation = new GeoLocation(10.0, 20.0);

        when(ipApiConfig.getUrl()).thenReturn("http://test-ip-api.com/json");
        when(cache.get("127.0.0.1")).thenReturn(Mono.just(cachedLocation));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(IpApiResponse.class)).thenReturn(Mono.empty()); // heç istifadə olunmayacaq

        StepVerifier.create(adapter.detectByIp(ip))
                .expectNext(cachedLocation)
                .verifyComplete();

        verify(cache).get("127.0.0.1");
    }


    @Test
    void detectByIp_shouldFetchAndCache_whenCacheEmpty_andSuccessResponse() throws Exception {
        InetAddress ip = InetAddress.getByName("8.8.8.8");
        when(ipApiConfig.getUrl()).thenReturn("http://test-ip-api.com/json");
        when(cache.get("8.8.8.8")).thenReturn(Mono.empty());

        double lat = 40.0;
        double lon = 49.0;

        Success success = new Success(
                "8.8.8.8", "success", "USA", "California", "Mountain View",
                "94043", "America/Los_Angeles", lat, lon, "Google LLC",
                "Google Inc.", "AS15169 Google LLC", null
        );

        when(responseSpec.bodyToMono(IpApiResponse.class)).thenReturn(Mono.just(success));
        when(cache.put("8.8.8.8", new GeoLocation(lat, lon))).thenReturn(Mono.empty());

        StepVerifier.create(adapter.detectByIp(ip))
                .expectNext(new GeoLocation(lat, lon))
                .verifyComplete();

        verify(cache).get("8.8.8.8");
        verify(webClient).get();
        verify(uriSpec).uri("http://test-ip-api.com/json/8.8.8.8");
        verify(cache).put("8.8.8.8", new GeoLocation(lat, lon));
    }

    @Test
    void detectByIp_shouldReturnEmpty_whenApiResponseIsFail() throws Exception {
        InetAddress ip = InetAddress.getByName("8.8.4.4");
        when(ipApiConfig.getUrl()).thenReturn("http://test-ip-api.com/json");
        when(cache.get("8.8.4.4")).thenReturn(Mono.empty());

        Fail fail = new Fail("some error", "8.8.4.4");
        when(responseSpec.bodyToMono(IpApiResponse.class)).thenReturn(Mono.just(fail));

        StepVerifier.create(adapter.detectByIp(ip))
                .verifyComplete();

        verify(cache).get("8.8.4.4");
        verifyNoMoreInteractions(cache);
    }

    @Test
    void detectByIp_shouldFallbackToCache_onError() throws Exception {
        InetAddress ip = InetAddress.getByName("1.1.1.1");
        when(ipApiConfig.getUrl()).thenReturn("http://test-ip-api.com/json");
        when(cache.get("1.1.1.1")).thenReturn(Mono.empty());

        when(responseSpec.bodyToMono(IpApiResponse.class))
                .thenReturn(Mono.error(new RuntimeException("API down")));

        GeoLocation fallbackLoc = new GeoLocation(99.0, 99.0);
        when(cache.get("1.1.1.1")).thenReturn(Mono.just(fallbackLoc));

        StepVerifier.create(adapter.detectByIp(ip))
                .expectNext(fallbackLoc)
                .verifyComplete();
    }
}