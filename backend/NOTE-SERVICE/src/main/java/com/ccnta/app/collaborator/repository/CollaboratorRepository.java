package com.ccnta.app.collaborator.repository;

import com.ccnta.app.collaborator.entity.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {

    @Query(value = """
            SELECT c
                    FROM Collaborator c
                    WHERE c.username = :username
                    OR LOWER(c.email) = LOWER(:username)
                """)
    Optional<Collaborator> findByUsername(String username);

    @Query("""
                SELECT c FROM Collaborator c
                JOIN c.projects p
                WHERE (c.username = :username OR LOWER(c.email) = LOWER(:username))
                AND p.projectId = :projectId
            """)
    Optional<Collaborator> findByUsernameAndProjectId(@Param("username") String username, @Param("projectId") String projectId);

    @Query("""
                SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collaborator c
                JOIN c.projects p
                JOIN p.notes n
                WHERE n.noteId = :noteId
                AND c.collaboratorId = :collaboratorId
            """)
    boolean existsByNoteIdAndCollaboratorId(@Param("noteId") String noteId, @Param("collaboratorId") Long collaboratorId);

    @Query("""
                SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Collaborator c
                JOIN c.projects p
                JOIN p.drafts d
                WHERE d.draftId = :draftId
                AND c.collaboratorId = :collaboratorId
            """)
    boolean existsByDraftIdAndCollaboratorId(@Param("draftId") String draftId, @Param("collaboratorId") Long collaboratorId);

    @Query("SELECT C FROM Collaborator C WHERE C.collaboratorId = :collaboratorId")
    Optional<Collaborator> findByCollaboratorId(@Param("collaboratorId") String collaboratorId);
}
