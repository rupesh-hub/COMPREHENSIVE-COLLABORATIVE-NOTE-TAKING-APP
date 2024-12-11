package com.ccnta.app.project.service;

import com.ccnta.app.collaborator.model.CollaboratorRequest;
import com.ccnta.app.project.model.ProjectRequest;
import com.ccnta.app.project.model.ProjectResponse;
import com.ccnta.app.shared.GlobalResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface IProjectService {

    GlobalResponse<Boolean> createProject(final ProjectRequest request, final String creator);
    GlobalResponse<List<ProjectResponse>> allProjects(final String authenticatedUser, final int page, final int limit);
    GlobalResponse<ProjectResponse> projectDetails(final String projectId, final String creator);
    GlobalResponse<Boolean> updateProject(final String projectId, final ProjectRequest request, final String creator);
    GlobalResponse<Boolean> deleteProject(final String projectId, final String creator);
    GlobalResponse<List<ProjectResponse>> filterProject(final String creator, final String query, final int page, final int limit);

//    GlobalResponse<Boolean> addNote(final Long projectId, final String noteContent, final String authenticatedUser);
//    GlobalResponse<Boolean> updateNote(final Long projectId, final Long noteId, final String noteContent, final String authenticatedUser);
//    GlobalResponse<Boolean> deleteNote(final Long projectId, final Long noteId, final String authenticatedUser);
//    GlobalResponse<Boolean> addDraft(final Long projectId, final String draftContent, final String authenticatedUser);
//    GlobalResponse<Boolean> updateDraft(final Long projectId, final Long draftId, final String draftContent, final String authenticatedUser);
//    GlobalResponse<Boolean> deleteDraft(final Long projectId, final Long draftId, final String authenticatedUser);
//    GlobalResponse<Boolean> shareProject(final Long projectId, final String shareWith, final String authenticatedUser);
//    GlobalResponse<Boolean> unshareProject(final Long projectId, final String shareWith, final String authenticatedUser);
//    GlobalResponse<Boolean> archiveProject(final Long projectId, final String authenticatedUser);
//    GlobalResponse<Boolean> unarchiveProject(final Long projectId, final String authenticatedUser);
//    GlobalResponse<Boolean> lockProject(final Long projectId, final String authenticatedUser);
//    GlobalResponse<Boolean> unlockProject(final Long projectId, final String authenticatedUser);
//    GlobalResponse<Boolean> addPermission(final Long projectId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> removePermission(final Long projectId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> addCollaboratorPermission(final Long projectId, final String collaboratorId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> removeCollaboratorPermission(final Long projectId, final String collaboratorId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> addNotePermission(final Long projectId, final Long noteId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> removeNotePermission(final Long projectId, final Long noteId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> addDraftPermission(final Long projectId, final Long draftId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> removeDraftPermission(final Long projectId, final Long draftId, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> addSharedProjectPermission(final Long projectId, final String sharedWith, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> removeSharedProjectPermission(final Long projectId, final String sharedWith, final Long permissionId, final String authenticatedUser);
//    GlobalResponse<Boolean> addArchivedProjectPermission(final Long projectId, final String authenticatedUser, final Long permissionId);
//    GlobalResponse<Boolean> removeArchivedProjectPermission(final Long projectId, final String authenticatedUser, final Long permissionId);
//    GlobalResponse<Boolean> addLockedProjectPermission(final Long projectId, final String authenticatedUser, final Long permissionId);
//    GlobalResponse<Boolean> removeLockedProjectPermission(final Long projectId, final String authenticatedUser, final Long permissionId);
//


}
