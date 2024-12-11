package com.ccnta.app.authority.entity;

import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_permissions")
@ToString
public class Permission extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_permission_id_seq_generator")
    @SequenceGenerator(name = "_permission_id_seq_generator", sequenceName = "_permission_id_seq", allocationSize = 50, initialValue = 50)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "permission_id", nullable = false, updatable = false, unique = true)
    private String permissionId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "authority_id", nullable = false)
    @JsonIgnore
    private Authority authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
