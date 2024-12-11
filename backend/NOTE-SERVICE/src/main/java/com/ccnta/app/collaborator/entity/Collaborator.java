package com.ccnta.app.collaborator.entity;


import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_collaborators")
public class Collaborator extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_collaborator_id_seq_generator")
    @SequenceGenerator(name = "_collaborator_id_seq_generator", sequenceName = "_collaborator_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "collaborator_id", nullable = false, updatable = false, unique = true)
    private String collaboratorId;

    private String name;
    private String email;
    private String username;
    private String profile;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "projects_collaborators",
            joinColumns = @JoinColumn(name = "collaborator_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    @JsonManagedReference
    private Set<Project> projects = new HashSet<>();  // Initialize here

    // Add helper method
    public void addProject(Project project) {
        if (project != null) {
            if (projects == null) {
                projects = new HashSet<>();
            }
            projects.add(project);
            // Ensure the project's collaborators set is initialized
            if (project.getCollaborators() == null) {
                project.setCollaborators(new HashSet<>());
            }
            project.getCollaborators().add(this);
        }
    }

    // One-to-Many or Many-to-Many mapping with Authority
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "collaborator_authorities",
            joinColumns = @JoinColumn(name = "collaborator_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new HashSet<>();

    /**
     * Adds an authority to the collaborator.
     * In a one-to-one mapping, this ensures only one authority is assigned.
     *
     * @param authority The authority to assign
     * @return true if authority was added, false if already assigned
     */
    public boolean addAuthority(Authority authority) {
        // If authorities is null, initialize it
        if (this.authorities == null) {
            this.authorities = new HashSet<>();
        }

        // In a one-to-one mapping, clear existing authorities before adding new one
        this.authorities.clear();

        // Add the new authority
        return this.authorities.add(authority);
    }

    /**
     * Removes an authority from the collaborator by its ID.
     *
     * @param authorityId The ID of the authority to remove
     * @return true if authority was removed, false if not found
     */
    public boolean removeAuthority(String authorityId) {
        if (this.authorities == null) {
            return false;
        }

        // Remove authority with matching ID
        boolean removed = this.authorities.removeIf(authority ->
                authority.getAuthorityId().equals(authorityId)
        );

        return removed;
    }

    /**
     * Checks if the collaborator has a specific authority.
     *
     * @param authorityId The ID of the authority to check
     * @return true if the collaborator has the authority, false otherwise
     */
    public boolean hasAuthority(String authorityId) {
        if (this.authorities == null) {
            return false;
        }

        return this.authorities.stream()
                .anyMatch(authority -> authority.getAuthorityId().equals(authorityId));
    }

    /**
     * Get authority names for the collaborator.
     *
     * @return Set of authority names
     */
    public Set<String> getAuthorityNames() {
        if (this.authorities == null) {
            return new HashSet<>();
        }

        return this.authorities.stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());
    }
}
