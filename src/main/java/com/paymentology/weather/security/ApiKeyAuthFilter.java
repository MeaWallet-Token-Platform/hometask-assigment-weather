package com.paymentology.weather.security;

import com.paymentology.weather.model.ClientApiKeyDto;
import com.paymentology.weather.repository.ClientKeyRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    public static final String X_API_KEY = "X-API-KEY";
    private final ClientKeyRepository clientKeyRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (permit(request)) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(403);
        }
    }

    private boolean permit(HttpServletRequest request) {
        return Optional
                .ofNullable(request.getHeader(X_API_KEY))
                .flatMap(clientKeyRepository::findByApiKey)
                .map(ClientApiKeyDto::revoked)
                .orElse(false);
    }
}
