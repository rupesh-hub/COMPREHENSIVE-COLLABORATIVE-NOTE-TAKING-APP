package com.ccnta.app.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_token_settings")
public class TokenSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_token_settings_id_seq_generator")
    @SequenceGenerator(name = "_token_settings_id_seq_generator", sequenceName = "_token_settings_id_seq_generator", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="token_type")
    private String tokenType;

    @Column(name="access_token_ttl")
    private int accessTokenTimeToLive;

    @Column(name="refresh_token_ttl")
    private int refreshTokenTimeToLive;

    @Column(name="authentication_token_ttl")
    private int authenticationCodeTimeToLive;

    @OneToOne(mappedBy = "tokenSetting")
    @JsonIgnore
    private Client client;

}