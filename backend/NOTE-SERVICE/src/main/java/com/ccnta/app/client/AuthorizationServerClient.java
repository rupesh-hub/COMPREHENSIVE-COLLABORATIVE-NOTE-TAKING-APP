package com.ccnta.app.client;

import com.ccnta.app.client.response.UserResponse;
import com.ccnta.app.shared.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServerClient {

    @Value("${application.url.authorization-server}")
    private String authorizationServerUrl;

    private final RestTemplate restTemplate;

    /**
     * Fetches user details by username from the authorization server
     *
     * @param username The username to look up
     * @return UserResponse containing user details
     * @throws RuntimeException if the server returns an error
     */
    public GlobalResponse<UserResponse> userByUsername(final String username) {
        String url = String.format("%s/users/by.username/%s", authorizationServerUrl, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        try {
            log.debug("Fetching user details for username: {} from URL: {}", username, url);

            ResponseEntity<GlobalResponse<UserResponse>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            GlobalResponse<UserResponse> response = responseEntity.getBody();
            if (response == null) {
                throw new RuntimeException("No response body received for user: " + username);
            }

            return response;

        } catch (RestClientException e) {
            log.error("Failed to fetch user details for username: {}", username, e);
            throw new RuntimeException("Failed to fetch user details: " + e.getMessage(), e);
        }
    }

}
