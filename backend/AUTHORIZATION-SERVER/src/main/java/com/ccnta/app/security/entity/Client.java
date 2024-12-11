package com.ccnta.app.security.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_clients")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@NamedQueries(
        {
                @NamedQuery(name = "Client.findByClientId", query = "SELECT C FROM Client C WHERE C.clientId=:clientId")
        }
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_client_id_seq_generator")
    @SequenceGenerator(name = "_client_id_seq_generator", sequenceName = "_client_id_seq_generator", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    private String secret;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "token_setting_id")
    private TokenSetting tokenSetting;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "_client_scope",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "scope_id")
    )
    private List<Scope> scopes;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "_client_redirect_uri",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "redirect_uri_id")
    )
    private List<RedirectUri> redirectUris;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "_client_grant_type",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "grant_type_id")
    )
    private List<GrantType> grantTypes;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "_client_authentication_method",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authentication_method_id")
    )
    private List<AuthenticationMethod> authenticationMethods;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedOn;

    public static RegisteredClient to(Client client) {
        var registeredClient = RegisteredClient
                .withId(String.valueOf(client.getId()))
                .clientId(client.getClientId())
                .clientName(client.getName())
                .clientSecret(client.getSecret())
                .clientAuthenticationMethods(convert(client.getAuthenticationMethods(), auth -> new ClientAuthenticationMethod(auth.getAuthenticationMethod())))
                .authorizationGrantTypes(convert(client.getGrantTypes(), grant -> new AuthorizationGrantType(grant.getGrantType())))
                .scopes(convert(client.getScopes(), scope -> scope.getScope()))
                .redirectUris(convert(client.getRedirectUris(), uri -> uri.getUri()))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(client.getTokenSetting().getAccessTokenTimeToLive()))
                        .refreshTokenTimeToLive(Duration.ofHours(client.getTokenSetting().getRefreshTokenTimeToLive()))
                        .authorizationCodeTimeToLive(Duration.ofHours(client.getTokenSetting().getAuthenticationCodeTimeToLive()))
                        .build()

                )
                .build();

        return registeredClient;
    }

    private static <T, R> Consumer<Set<R>> convert(List<T> source, Function<T, R> mapper) {
        return target -> target.addAll(source.stream().map(mapper).collect(Collectors.toSet()));
    }

}