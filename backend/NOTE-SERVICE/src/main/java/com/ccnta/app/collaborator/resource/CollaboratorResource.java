package com.ccnta.app.collaborator.resource;

import com.ccnta.app.collaborator.model.CollaboratorRequest;
import com.ccnta.app.collaborator.service.ICollaboratorService;
import com.ccnta.app.shared.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(path = {"collaborators"})
@RequiredArgsConstructor
public class CollaboratorResource {

    private final ICollaboratorService collaboratorService;

    @PutMapping(path = {"add.collaborator"})
    public ResponseEntity<GlobalResponse<Void>> addCollaborator(
            @RequestBody Set<CollaboratorRequest> requests,
            @RequestParam(name = "projectId") String projectId,
            Authentication authentication) {
        return ResponseEntity.ok(collaboratorService.addCollaborator(projectId, requests, authentication.getName()));
    }

    @PutMapping(path = {"remove.collaborator"})
    public ResponseEntity<GlobalResponse<Void>> removeCollaborator(
            @RequestParam(name = "projectId") String projectId,
            @RequestParam(name = "usernames") Set<String> usernames,
            Authentication authentication) {
        return ResponseEntity.ok(collaboratorService.removeCollaborator(projectId, usernames, authentication.getName()));
    }

    @PostMapping("/{collaboratorId}/assign/{authorityId}")
    public ResponseEntity<GlobalResponse<Void>> assignAuthorityToCollaborator(
            @PathVariable String authorityId,
            @PathVariable String collaboratorId
    ) {
        return ResponseEntity.ok(collaboratorService.assignAuthority(collaboratorId, authorityId));
    }

    @DeleteMapping("/{collaboratorId}/remove/{authorityId}")
    public ResponseEntity<GlobalResponse<Void>> removeAuthorityFromCollaborator(
            @PathVariable String authorityId,
            @PathVariable String collaboratorId
    ) {
        return ResponseEntity.ok(collaboratorService.removeAuthority(collaboratorId, authorityId));
    }

}
