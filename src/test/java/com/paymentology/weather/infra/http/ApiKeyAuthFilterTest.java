package com.paymentology.weather.infra.http;

import com.paymentology.weather.domain.client.ClientApiKey;
import com.paymentology.weather.domain.client.ClientKeyRepository;
import com.paymentology.weather.infra.security.ApiKeyAuthFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthFilterTest {

    @Mock
    private ClientKeyRepository keyRepo;

    @InjectMocks
    private ApiKeyAuthFilter victim;

    @Test
    void shouldReturn401WhenApiKeyIsMissing() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = mock(FilterChain.class);

        victim.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals("Missing API key", response.getErrorMessage());
    }

    @Test
    void shouldReturn403WhenApiKeyNotFound() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = mock(FilterChain.class);
        request.addHeader("X-API-KEY", "non-existent-key");
        given(keyRepo.find(anyString())).willReturn(Optional.empty());

        victim.doFilterInternal(request, response, filterChain);

        assertEquals(403, response.getStatus());
        assertEquals("Invalid API key", response.getErrorMessage());
    }

    @Test
    void shouldReturn403WhenApiKeyIsRevoked() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = mock(FilterChain.class);
        var apiKey = "valid-but-revoked-key";
        request.addHeader("X-API-KEY", apiKey);
        given(keyRepo.find(apiKey)).willReturn(Optional.of(new ClientApiKey(1L, apiKey, true)));

        victim.doFilterInternal(request, response, filterChain);

        assertEquals(403, response.getStatus());
        assertEquals("Invalid API key", response.getErrorMessage());
    }

    @Test
    void shouldContinueFilterChainWhenApiKeyIsValid() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = mock(FilterChain.class);
        var apiKey = "valid-key";
        request.addHeader("X-API-KEY", apiKey);
        given(keyRepo.find(apiKey)).willReturn(Optional.of(new ClientApiKey(1L, apiKey, false)));

        victim.doFilterInternal(request, response, filterChain);

        assertEquals(200, response.getStatus());
        verify(filterChain).doFilter(request, response);
    }
}