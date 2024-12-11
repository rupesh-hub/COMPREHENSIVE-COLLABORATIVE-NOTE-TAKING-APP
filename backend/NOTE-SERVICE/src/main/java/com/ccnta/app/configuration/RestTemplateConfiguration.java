package com.ccnta.app.configuration;

import com.ccnta.app.configuration.interceptor.AuthorizationInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(AuthorizationInterceptor authorizationInterceptor, ObjectMapper objectMapper) {
        // Create message converter with custom media type support
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        List<MediaType> supportedMediaTypes = new ArrayList<>(messageConverter.getSupportedMediaTypes());
        // Add support for text/html as some servers might return JSON with incorrect content type
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        messageConverter.setSupportedMediaTypes(supportedMediaTypes);

        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .interceptors(authorizationInterceptor)
                .messageConverters(List.of(messageConverter))
                .errorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        try {
                            HttpStatusCode statusCode = response.getStatusCode();
                            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
                        } catch (Exception ex) {
                            log.error("Error checking response status", ex);
                            return true;
                        }
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        try {
                            HttpStatusCode statusCode = response.getStatusCode();
                            String responseBody = new String(getResponseBody(response));
                            log.error("HTTP Error - Status: {}, Body: {}", statusCode, responseBody);

                            if (statusCode.is5xxServerError()) {
                                throw new RuntimeException("Server error occurred: " + statusCode +
                                        ", Response: " + responseBody);
                            } else if (statusCode.is4xxClientError()) {
                                throw new RuntimeException("Client error occurred: " + statusCode +
                                        ", Response: " + responseBody);
                            }
                        } catch (IOException ex) {
                            log.error("Error handling response", ex);
                            throw ex;
                        }
                    }
                })
                .build();
    }

}