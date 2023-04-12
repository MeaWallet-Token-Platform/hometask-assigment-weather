package com.paymentology.weather.config;

import com.paymentology.weather.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WeatherResponseExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        var statusCode = response.getStatusCode().value();
        var httpStatus = HttpStatus.resolve(statusCode);
        return httpStatus != null ? httpStatus.isError() : hasError(statusCode);
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) throws IOException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getBody()))) {

            String httpBodyResponse = reader.lines()
                    .collect(Collectors.joining(""));

            throw new ApiException(httpBodyResponse);
        }

    }

    protected boolean hasError(int unknownStatusCode) {
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }


}
