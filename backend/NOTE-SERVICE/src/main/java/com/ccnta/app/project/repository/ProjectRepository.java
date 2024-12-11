package com.ccnta.app.project.repository;

import com.ccnta.app.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(name = "Project.findByTitleAndCreator")
    List<Project> findByTitleAndCreator(String title, String creator);

    @Query(name = "Project.allProjects")
    Page<Project> allProjects(@Param("username") String username, Pageable pageable);

    @Query(name = "Project.byProjectIdAndCreator")
    Optional<Project> findByProjectId(@Param("projectId") String projectId, @Param("creator") String authenticatedUser);

    @Query(name = "Project.findByTitleContainingOrDescriptionContaining")
    Page<Project> findByTitleContainingOrDescriptionContaining(@Param("query") String query, String authenticatedUser, PageRequest pageRequest);

    @Query(name = "Project.findByProjectIdAndUsername")
    Optional<Project> findByProjectIdAndUsername(@Param("projectId") String projectId, @Param("username") String username);
}
