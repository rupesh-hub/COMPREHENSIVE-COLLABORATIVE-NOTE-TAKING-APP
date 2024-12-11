package com.ccnta.app.collaborator.service;

import com.ccnta.app.audit.enums.AuditAction;
import com.ccnta.app.audit.service.AuditLogService;
import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.authority.repository.AuthorityRepository;
import com.ccnta.app.client.AuthorizationServerClient;
import com.ccnta.app.client.response.UserResponse;
import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.collaborator.model.CollaboratorRequest;
import com.ccnta.app.collaborator.repository.CollaboratorRepository;
import com.ccnta.app.exception.ProjectException;
import com.ccnta.app.notification.entity.Notification;
import com.ccnta.app.notification.enums.NotificationTypes;
import com.ccnta.app.notification.service.NotificationService;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.project.repository.ProjectRepository;
import com.ccnta.app.shared.GlobalResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollaboratorService implements ICollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final ProjectRepository projectRepository;
    private final AuthorizationServerClient authorizationClient;
    private final AuthorityRepository authorityRepository;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private static final String DEFAULT_AUTHORITY = "viewer";

    @Override
    public Optional<Collaborator> getCollaboratorByUsernameAndProjectId(String username, String projectId) {
        return collaboratorRepository.findByUsernameAndProjectId(username, projectId);
    }

    @Override
    public boolean isNoteOwnedByCollaborator(String noteId, Long collaboratorId) {
        return collaboratorRepository.existsByNoteIdAndCollaboratorId(noteId, collaboratorId);
    }

    @Override
    public boolean isDraftOwnedByCollaborator(String draftId, Long collaboratorId) {
        return collaboratorRepository.existsByDraftIdAndCollaboratorId(draftId, collaboratorId);
    }

    @Override
    @Transactional
    public GlobalResponse<Void> addCollaborator(String projectId, Set<CollaboratorRequest> requests, String authenticatedUser) {
        validateCollaboratorOperation(projectId, requests, authenticatedUser);

        Project project = projectRepository.findByProjectIdAndUsername(projectId, authenticatedUser)
                .orElseThrow(() -> new ProjectException(
                        format("Project not found - ID: %s, User: %s", projectId, authenticatedUser)
                ));

        // Filter out collaborators who are the project owner
        Set<CollaboratorRequest> validCollaborators = requests.stream()
                .filter(request -> !request.getUsername().equals(project.getCreatedBy()))
                .collect(Collectors.toSet());

        if (validCollaborators.isEmpty()) {
            return GlobalResponse.success("No valid collaborators to add. All requested collaborators are already the project owner.");
        }

        addCollaboratorsToProject(project, validCollaborators);

        try {
            // Save the project
            Project updatedProject = projectRepository.save(project);

            auditService.audit(
                    project(updatedProject),
                    AuditAction.COLLABORATOR_ADDED,
                    authenticatedUser,
                    project(project),
                    format("Collaborator added by %s on %s.", authenticatedUser, new Date())
            );

            notifyCollaborators(updatedProject, NotificationTypes.COLLABORATOR_ADDED);

            return GlobalResponse.success(format("Collaborator(s) added by %s on %s.", authenticatedUser, new Date()));
        } catch (Exception e) {
            log.error("Failed to add collaborator - Project: {}, User: {}", projectId, requests, e);
            throw new ProjectException("Failed to add collaborator", e);
        }
    }


    @Override
    @Transactional
    public GlobalResponse<Void> removeCollaborator(String projectId, Set<String> usernames, String authenticatedUser) {
        validateCollaboratorOperationFor(projectId, usernames, authenticatedUser);

        Project project = projectRepository.findByProjectIdAndUsername(projectId, authenticatedUser)
                .orElseThrow(() -> new ProjectException(
                        format("Project not found - ID: %s, User: %s", projectId, authenticatedUser)
                ));

        project.removeCollaborators(usernames);

        try {
            Project updatedProject = projectRepository.save(project);

            auditService.audit(
                    project(updatedProject),
                    AuditAction.COLLABORATOR_REMOVED,
                    authenticatedUser,
                    project(project),
                    format("Collaborator removed by %s on %s.", authenticatedUser, LocalDateTime.now())
            );

            notifyCollaborators(updatedProject, NotificationTypes.COLLABORATOR_REMOVED);

            return GlobalResponse.success();
        } catch (Exception e) {
            log.error("Failed to remove collaborator - Project: {}, User: {}", projectId, usernames, e);
            throw new ProjectException("Failed to remove collaborator", e);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GlobalResponse<Void> assignAuthority(String collaboratorId, String authorityId) {
        try {
            // Validate input
            if (authorityId == null || collaboratorId == null) {
                log.warn("Attempt to assign authority with null IDs");
                return GlobalResponse.failure("Invalid authority or collaborator ID");
            }

            // Verify authority exists
            Authority authority = authorityRepository.findByAuthorityId(authorityId)
                    .orElseThrow(() -> {
                        log.warn("Authority not found with ID: {}", authorityId);
                        return new NoSuchElementException("Authority not found");
                    });

            // Verify collaborator exists
            var collaborator = collaboratorRepository.findByCollaboratorId(collaboratorId)
                    .orElseThrow(() -> {
                        log.warn("Collaborator not found with ID: {}", collaboratorId);
                        return new NoSuchElementException("Collaborator not found");
                    });

            // Assign authority to collaborator
            collaborator.addAuthority(authority);
            collaboratorRepository.save(collaborator);

            log.info("Successfully assigned authority {} to collaborator {}", authorityId, collaboratorId);
            return GlobalResponse.success();
        } catch (NoSuchElementException e) {
            log.warn("Authority assignment failed: {}", e.getMessage());
            return GlobalResponse.failure(e.getMessage());
        } catch (Exception e) {
            log.error("Error assigning authority to collaborator", e);
            throw new ProjectException(e.getMessage());
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public GlobalResponse<Void> removeAuthority(String collaboratorId, String authorityId) {
        try {
            // Validate input
            if (authorityId == null || collaboratorId == null) {
                log.warn("Attempt to remove authority with null IDs");
                return GlobalResponse.failure("Invalid authority or collaborator ID");
            }

            // Verify collaborator exists
            Collaborator collaborator = collaboratorRepository.findByCollaboratorId(collaboratorId)
                    .orElseThrow(() -> {
                        log.warn("Collaborator not found with ID: {}", collaboratorId);
                        return new NoSuchElementException("Collaborator not found");
                    });

            // Remove authority from collaborator
            boolean removed = collaborator.removeAuthority(authorityId);

            if (!removed) {
                log.warn("Authority {} not found for collaborator {}", authorityId, collaboratorId);
                return GlobalResponse.failure("Authority not assigned to collaborator");
            }

            collaboratorRepository.save(collaborator);

            log.info("Successfully removed authority {} from collaborator {}", authorityId, collaboratorId);
            return GlobalResponse.success();
        } catch (NoSuchElementException e) {
            log.warn("Authority removal failed: {}", e.getMessage());
            return GlobalResponse.failure(e.getMessage());
        } catch (Exception e) {
            log.error("Error removing authority from collaborator", e);
            throw new ProjectException(e.getMessage());
        }
    }

    private void validateCollaboratorOperation(String projectId, Set<CollaboratorRequest> requests, String authenticatedUser) {
        if (projectId == null || requests == null || requests.isEmpty() || authenticatedUser == null) {
            throw new IllegalArgumentException("Invalid input parameters for collaborator operation");
        }
    }

    private void validateCollaboratorOperationFor(String projectId, Set<String> usernames, String authenticatedUser) {
        if (projectId == null || usernames == null || usernames.isEmpty() || authenticatedUser == null) {
            throw new IllegalArgumentException("Invalid input parameters for collaborator operation");
        }
    }

    private void addCollaboratorsToProject(Project project, Set<CollaboratorRequest> requests) {

        for (CollaboratorRequest request : requests) {
            String authority = request.getAuthority() != null ? request.getAuthority() : DEFAULT_AUTHORITY;
            try {
                Collaborator collaborator = getOrCreateCollaborator(request.getUsername());
                collaborator.addAuthority(authority(authority));
                project.addCollaborator(collaborator);
                collaboratorRepository.save(collaborator);
            } catch (Exception e) {
                log.error("Failed to add collaborator: {}", request.getUsername(), e);
                throw new ProjectException(
                        String.format("Failed to add collaborator: %s", request.getUsername()), e/**/
                );
            }
        }

    }

    private Collaborator getOrCreateCollaborator(String username) {
        return collaboratorRepository.findByUsername(username)
                .orElseGet(() -> createNewCollaborator(username));
    }

    private Collaborator createNewCollaborator(String username) {
        UserResponse user = authorizationClient.userByUsername(username)
                .getData();

        if (user == null) {
            throw new ProjectException("User not found: " + username);
        }

        return Collaborator.builder()
                .collaboratorId(UUID.randomUUID().toString())
                .name(format("%s %s", user.getFirstName(), user.getLastName()))
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(user.getProfile() != null ? user.getProfile().getPath() : null)
                .build();
    }

    private Authority authority(String authorityName) {
        return authorityRepository.findByName(authorityName)
                .orElseThrow(() -> new ProjectException(format("Authority %s not found", authorityName)));
    }

    private Project project(Project project) {
        Project obj = new Project();
        obj.setId(project.getId());
        obj.setTitle(project.getTitle());
        obj.setDescription(project.getDescription());
        obj.setCreatedAt(project.getCreatedAt());
        obj.setCreatedBy(project.getCreatedBy());
        obj.setUpdatedAt(project.getUpdatedAt());
        obj.setUpdatedBy(project.getUpdatedBy());
        obj.setProjectId(project.getProjectId());
        return obj;
    }

    private void notifyCollaborators(Project project, NotificationTypes type) {
        if (project.getCollaborators() != null && !project.getCollaborators().isEmpty()) {
            Notification notification = buildNotification(project, type);
            notificationService.notify(project.getCollaborators()
                            .stream()
                            .map(Collaborator::getEmail)
                            .collect(Collectors.toSet()),
                    notification);
        }
    }

    private Notification buildNotification(Project project, NotificationTypes type) {
        return Notification.builder()
                .type(type)
                .title(type.description())
                .message(format("Project '%s' has been %s",
                        project.getTitle(), type.name().toLowerCase()))
                .build();
    }

}
