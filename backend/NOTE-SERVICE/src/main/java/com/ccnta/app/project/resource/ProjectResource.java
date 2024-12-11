package com.ccnta.app.project.resource;

import com.ccnta.app.project.model.ProjectRequest;
import com.ccnta.app.project.model.ProjectResponse;
import com.ccnta.app.project.service.IProjectService;
import com.ccnta.app.shared.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"projects"})
@RequiredArgsConstructor
public class ProjectResource {

    private final IProjectService projectService;

    @GetMapping(path = {"all"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GlobalResponse<List<ProjectResponse>>> allProjects(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.allProjects(authentication.getName(), page, limit));
    }

    @GetMapping(path = {"details"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GlobalResponse<ProjectResponse>> getProjectDetails(
            @RequestParam(name = "projectId") String projectId,
            Authentication authentication

    ) {
        return ResponseEntity.ok(projectService.projectDetails(projectId, authentication.getName()));
    }

    @PostMapping(path = {"create"})
    public ResponseEntity<GlobalResponse<Boolean>> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.createProject(request, authentication.getName()));
    }

    @GetMapping(path = {"filter"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GlobalResponse<List<ProjectResponse>>> filterProjects(
            @RequestParam(name = "query", defaultValue = "") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            Authentication authentication) {
        return ResponseEntity.ok(projectService.filterProject(authentication.getName(), query, page, limit));
    }

    @PutMapping(path = {"update"})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<GlobalResponse<Boolean>> updateProject(
            @RequestParam(name = "projectId") String projectId,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request, authentication.getName()));
    }

    @DeleteMapping(path = {"delete"})
    public ResponseEntity<GlobalResponse<Boolean>> deleteProject(
            @RequestParam(name = "projectId") String projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.deleteProject(projectId, authentication.getName()));
    }

}
