package com.ccnta.app.project.entity;

import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.draft.entity.Draft;
import com.ccnta.app.note.entity.Note;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_projects")
//@ToString(exclude = {"notes", "drafts", "collaborators"})
@NamedQueries({
        @NamedQuery(name = "Project.findByProjectId", query = "SELECT P FROM Project P WHERE P.projectId=:projectId"),
        @NamedQuery(name = "Project.findByTitleAndCreator", query = "SELECT P FROM Project P WHERE P.title=:title AND P.createdBy=:creator"),
        @NamedQuery(name = "Project.byProjectIdAndCreator", query = "SELECT P FROM Project P WHERE P.projectId=:projectId AND P.createdBy=:creator"),
        @NamedQuery(name = "Project.allProjects", query = """
                    SELECT DISTINCT p
                        FROM Project p
                        LEFT JOIN p.collaborators c
                        WHERE p.createdBy = :username\s
                           OR c.username = :username
                """),
        @NamedQuery(name = "Project.findByTitleContainingOrDescriptionContaining", query = """
                    SELECT DISTINCT p
                    FROM Project p
                    LEFT JOIN p.collaborators c
                    WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
                    AND (p.createdBy = :authenticatedUser 
                         OR c.username = :authenticatedUser)
                """),
        @NamedQuery(
                name = "Project.findByProjectIdAndUsername",
                query = """
                        SELECT p
                        FROM Project p
                        LEFT JOIN p.collaborators c
                        WHERE p.projectId = :projectId
                        AND (:username = p.createdBy OR c.username = :username)
                        """
        )

})
@DynamicUpdate
public class Project extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_project_id_seq_generator")
    @SequenceGenerator(name = "_project_id_seq_generator", sequenceName = "_project_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "project_id", nullable = false, updatable = false, unique = true)
    private String projectId;

    private String title;
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Draft> drafts = new HashSet<>();

    @ManyToMany(mappedBy = "projects", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Set<Collaborator> collaborators = new HashSet<>();

    public void addCollaborator(Collaborator collaborator) {
        if (collaborator != null) {
            if (collaborators == null) {
                collaborators = new HashSet<>();
            }
            collaborators.add(collaborator);
            // Ensure the collaborator's projects set is initialized
            if (collaborator.getProjects() == null) {
                collaborator.setProjects(new HashSet<>());
            }
            collaborator.getProjects().add(this);
        }
    }

    public void removeCollaborator(Collaborator collaborator) {
        if (collaborator != null && this.collaborators.contains(collaborator)) {
            this.collaborators.remove(collaborator);
            collaborator.getProjects().remove(this);
        }
    }

    public void removeCollaborators(Set<String> usernames) {
        if (usernames != null && !usernames.isEmpty()) {
            Set<Collaborator> collaboratorsToRemove = this.collaborators.stream()
                    .filter(c -> usernames.contains(c.getUsername()))
                    .collect(Collectors.toSet());

            for (Collaborator collaborator : collaboratorsToRemove) {
                removeCollaborator(collaborator);
            }
        }
    }

}
