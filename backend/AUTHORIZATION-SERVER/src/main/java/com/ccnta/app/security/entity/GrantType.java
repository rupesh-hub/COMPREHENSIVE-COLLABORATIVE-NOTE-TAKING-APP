package com.ccnta.app.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_grant_types")
public class GrantType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_grant_type_id_seq_generator")
    @SequenceGenerator(name = "_grant_type_id_seq_generator", sequenceName = "_grant_type_id_seq_generator", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name="grant_type", unique = true, nullable = false)
    private String grantType;

    @ManyToMany(mappedBy = "grantTypes")
    @JsonIgnore
    private List<Client> clients;

    public static GrantType from(AuthorizationGrantType authGrantType, Client client) {
        return GrantType
                .builder()
                .grantType(authGrantType.getValue())
                .clients(List.of(client))
                .build();
    }

}