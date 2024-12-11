package com.ccnta.app.authority.entity;

import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_authorities")
public class Authority extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_authority_id_seq_generator")
    @SequenceGenerator(name = "_authority_id_seq_generator", sequenceName = "_authority_id_seq", allocationSize = 50, initialValue = 50)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "authority_id", nullable = false, updatable = false, unique = true)
    private String authorityId;

    private String name;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Permission> permissions = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    private Collaborator collaborator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Authority authority)) return false;
        return Objects.equals(getName(), authority.getName()) && Objects.equals(getPermissions(), authority.getPermissions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPermissions());
    }

    public void addPermission(Permission permission) {
        if (this.permissions == null) {
            this.permissions = new HashSet<>();
        }

        if (permission != null) {
            this.permissions.add(permission);
            permission.setAuthority(this);
        }
    }

    public void removePermission(Permission permission) {
        if (this.permissions != null && permission != null) {
            this.permissions.remove(permission);
            permission.setAuthority(null);
        }
    }

    public void clearPermissions() {
        if (this.permissions != null) {
            Set<Permission> permissionsToRemove = new HashSet<>(this.permissions);
            permissionsToRemove.forEach(this::removePermission);
            this.permissions.clear();
        }
    }
}
