package com.ccnta.app.note.repository;

import com.ccnta.app.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT N FROM Note N WHERE N.project.projectId = :projectId")
    List<Note> findAllByProjectId(String projectId);

    @Query("SELECT n FROM Note n WHERE n.project.id IN ( " +
            "SELECT DISTINCT p.id FROM Project p " +
            "LEFT JOIN p.collaborators c " +
            "WHERE p.createdBy = :username OR c.username = :username " +
            ")")
    Page<Note> findAllNotes(String username, PageRequest pageRequest);

    @Query("SELECT n FROM Note n WHERE n.noteId = :noteId")
    Optional<Note> findByNoteId(String noteId);

    @Query("SELECT n FROM Note n WHERE n.project.projectId = :projectId AND " +
            "(n.project.createdBy = :username OR " +
            "EXISTS (SELECT c FROM n.project.collaborators c WHERE c.username = :username))")
    Page<Note> findNotesByProjectAndUser(@Param("projectId") String projectId,
                                         @Param("username") String username,
                                         PageRequest pageRequest);

}
