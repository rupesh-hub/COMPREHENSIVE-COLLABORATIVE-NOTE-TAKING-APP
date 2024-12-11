package com.ccnta.app.collaborator.service;

import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.collaborator.model.CollaboratorRequest;
import com.ccnta.app.shared.GlobalResponse;

import java.util.Optional;
import java.util.Set;

public interface ICollaboratorService {

    Optional<Collaborator> getCollaboratorByUsernameAndProjectId(String username, String projectId);
    boolean isNoteOwnedByCollaborator(String noteId, Long collaboratorId);
    boolean isDraftOwnedByCollaborator(String draftId, Long collaboratorId);

    GlobalResponse<Void> addCollaborator(String projectId, Set<CollaboratorRequest> requests, String name);

    GlobalResponse<Void> removeCollaborator(String projectId, Set<String> usernames, String name);

    GlobalResponse<Void> assignAuthority(String collaboratorId, String authorityId);

    GlobalResponse<Void> removeAuthority(String collaboratorId, String authorityId);
}
