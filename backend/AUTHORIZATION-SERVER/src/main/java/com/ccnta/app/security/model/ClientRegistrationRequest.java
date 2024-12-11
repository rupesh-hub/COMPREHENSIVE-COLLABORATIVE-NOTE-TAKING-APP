package com.ccnta.app.security.model;

import lombok.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientRegistrationRequest {

    private String clientId;
    private String secret;
    private String name;
    private TokenSettingRequest tokenSetting;
    private Set<String> scopes;
    private Set<String> redirectUris;
    private Set<String> grantTypes;
    private Set<String> authenticationMethods;

    public static RegisteredClient toRegisterClient(ClientRegistrationRequest request) {
        return RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(request.getClientId())
                .clientSecret(request.getSecret())
                .clientName(request.getName())
                .clientIdIssuedAt(new Date().toInstant())
                .clientAuthenticationMethods(methods -> methods.addAll(
                        request.getAuthenticationMethods()
                                .stream()
                                .map(ClientAuthenticationMethod::new)
                                .collect(Collectors.toSet())
                ))
                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(
                        request.getGrantTypes().stream()
                                .map(AuthorizationGrantType::new)
                                .collect(Collectors.toSet())
                ))
                .scopes(scopes -> scopes.addAll(request.getScopes()))
                .redirectUris(redirectUris -> redirectUris.addAll(request.getRedirectUris()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(request.getTokenSetting().getAccessTokenTimeToLive()))
                        .refreshTokenTimeToLive(Duration.ofHours(request.getTokenSetting().getRefreshTokenTimeToLive()))
                        .authorizationCodeTimeToLive(Duration.ofHours(request.getTokenSetting().getAuthorizationCodeTimeToLive()))
                        .build()
                )
                .build();
    }
}