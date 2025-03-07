package com.paymentology.weather.infra.security;

import com.paymentology.weather.domain.client.ClientKeyRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private static final String API_KEY_HEADER = "X-API-KEY";
    private final ClientKeyRepository keyRepo;

    @Autowired
    public ApiKeyAuthFilter(ClientKeyRepository keyRepo) {
        this.keyRepo = keyRepo;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null) {
            log.warn("Missing API key");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API key");
            return;
        }

        if (!permit(request)) {
            log.warn("Invalid API key");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid API key");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean permit(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(API_KEY_HEADER))
                .flatMap(keyRepo::find)
                .map(key -> !key.revoked())
                .orElse(false);
    }
}
