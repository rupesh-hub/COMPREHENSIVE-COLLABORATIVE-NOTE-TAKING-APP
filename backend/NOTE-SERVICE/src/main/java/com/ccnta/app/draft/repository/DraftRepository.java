package com.ccnta.app.draft.repository;

import com.ccnta.app.draft.entity.Draft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

    @Query("SELECT D FROM Draft D WHERE D.project.projectId = :projectId")
    List<Draft> findAllByProjectId(String projectId);

    @Query("SELECT D FROM Draft D WHERE D.draftId = :draftId")
    Optional<Draft> findByDraftId(String draftId);

    @Query("SELECT D FROM Draft D WHERE D.project.id IN ( " +
            "SELECT DISTINCT p.id FROM Project p " +
            "LEFT JOIN p.collaborators c " +
            "WHERE p.createdBy = :username OR c.username = :username " +
            ")")
    Page<Draft> findAllDrafts(String username, PageRequest pageRequest);
}
