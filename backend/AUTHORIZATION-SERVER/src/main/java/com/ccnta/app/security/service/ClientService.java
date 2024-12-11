package com.ccnta.app.security.service;

import com.ccnta.app.security.entity.*;
import com.ccnta.app.security.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void save(RegisteredClient registeredClient) {
        Client client = new Client();
        client.setClientId(registeredClient.getClientId());
        client.setSecret(passwordEncoder.encode(registeredClient.getClientSecret()));
        client.setName(registeredClient.getClientName());

        client.setAuthenticationMethods(
                registeredClient.getClientAuthenticationMethods()
                        .stream()
                        .map(auth -> AuthenticationMethod.from(auth, client))
                        .toList()
        );

        client.setGrantTypes(
                registeredClient.getAuthorizationGrantTypes()
                        .stream()
                        .map(grant -> GrantType.from(grant, client))
                        .collect(Collectors.toList())
        );

        client.setRedirectUris(
                registeredClient.getRedirectUris()
                        .stream()
                        .map(uri -> RedirectUri.from(uri, client))
                        .collect(Collectors.toList())
        );

        client.setScopes(
                registeredClient.getScopes()
                        .stream()
                        .map(scope -> Scope.from(scope, client))
                        .collect(Collectors.toList())
        );

        client.setTokenSetting(
                TokenSetting
                        .builder()
                        .accessTokenTimeToLive((int) registeredClient.getTokenSettings().getAccessTokenTimeToLive().toHours())
                        .refreshTokenTimeToLive((int) registeredClient.getTokenSettings().getRefreshTokenTimeToLive().toHours())
                        .authenticationCodeTimeToLive((int) registeredClient.getTokenSettings().getAuthorizationCodeTimeToLive().toHours())
                        .client(client)
                        .build()
        );

        clientRepository.save(client);
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientRepository.findById(Long.parseLong(id))
                .map(Client::to)
                .orElseThrow(() -> new RuntimeException(""));
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(Client::to)
                .orElseThrow(() -> new RuntimeException(":("));
    }

}