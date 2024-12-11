package com.ccnta.app.configuration.interceptor;

import com.ccnta.app.configuration.provider.TokenProvider;
import com.ccnta.app.exception.TokenNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthorizationInterceptor implements ClientHttpRequestInterceptor  {

    private final TokenProvider tokenProvider;

    public AuthorizationInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        String token = tokenProvider.getToken();
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException("No valid token found in security context");
        }

        request.getHeaders().setBearerAuth(token);
        return execution.execute(request, body);
    }

}
