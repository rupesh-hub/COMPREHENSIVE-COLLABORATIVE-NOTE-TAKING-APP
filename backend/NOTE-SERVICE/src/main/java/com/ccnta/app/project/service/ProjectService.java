package com.ccnta.app.project.service;

import com.ccnta.app.audit.enums.AuditAction;
import com.ccnta.app.audit.service.AuditLogService;
import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.authority.repository.AuthorityRepository;
import com.ccnta.app.client.AuthorizationServerClient;
import com.ccnta.app.client.response.UserResponse;
import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.collaborator.model.CollaboratorRequest;
import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.collaborator.repository.CollaboratorRepository;
import com.ccnta.app.draft.entity.Draft;
import com.ccnta.app.draft.model.DraftResponse;
import com.ccnta.app.draft.repository.DraftRepository;
import com.ccnta.app.exception.ProjectException;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import com.ccnta.app.media.repository.ImageRepository;
import com.ccnta.app.note.entity.Note;
import com.ccnta.app.note.model.NoteResponse;
import com.ccnta.app.note.repository.NoteRepository;
import com.ccnta.app.notification.entity.Notification;
import com.ccnta.app.notification.enums.NotificationTypes;
import com.ccnta.app.notification.service.NotificationService;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.project.model.ProjectRequest;
import com.ccnta.app.project.model.ProjectResponse;
import com.ccnta.app.project.repository.ProjectRepository;
import com.ccnta.app.shared.GlobalResponse;
import com.ccnta.app.shared.Paging;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Service implementation for Project management operations.
 * Handles CRUD operations and related business logic for projects.
 */
@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String PROJECT_CACHE = "projectCache";
    private static final String DEFAULT_AUTHORITY = "viewer";

    private final ProjectRepository projectRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AuthorizationServerClient authorizationClient;
    private final NoteRepository noteRepository;
    private final DraftRepository draftRepository;
    private final ImageRepository imageRepository;
    private final AuditLogService auditService;
    private final NotificationService notificationService;
    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional
    @CacheEvict(value = PROJECT_CACHE, allEntries = true)
    public GlobalResponse<Boolean> createProject(final ProjectRequest request, final String creator) {
        validateProjectRequest(request, creator);

        Project project = buildProject(request);

        if (hasCollaborators(request)) {
            addCollaboratorsToProject(project, request.getCollaborators());
        }

        try {
            Project savedProject = projectRepository.save(project);

            auditService.audit(
                    project(savedProject),
                    AuditAction.PROJECT_CREATION,
                    creator,
                    null,
                    format("New project created by %s on %s.", creator, LocalDateTime.now())
            );

            notifyCollaborators(savedProject, NotificationTypes.PROJECT_CREATED);

            log.info("Project created successfully with ID: {}", project.getProjectId());
            return GlobalResponse.success(true);
        } catch (Exception e) {
            log.error("Failed to create project", e);
            throw new ProjectException("Failed to create project", e);
        }
    }

    @Override
    @Cacheable(value = PROJECT_CACHE, key = "#authenticatedUser + #pageNumber + #pageSize")
    public GlobalResponse<List<ProjectResponse>> allProjects(
            String authenticatedUser,
            int pageNumber,
            int pageSize
    ) {
        try {
            validatePaginationParams(pageNumber, pageSize);
            PageRequest pageRequest = createPageRequest(pageNumber, pageSize);
            Page<Project> projectPage = projectRepository.allProjects(authenticatedUser, pageRequest);

            return createProjectResponse(projectPage);
        } catch (Exception e) {
            log.error("Error fetching projects for user: {}", authenticatedUser, e);
            throw new ProjectException("Failed to fetch projects", e);
        }
    }

    @Override
    @Cacheable(value = PROJECT_CACHE, key = "#projectId + #authenticatedUser")
    public GlobalResponse<ProjectResponse> projectDetails(String projectId, String authenticatedUser) {
        return projectRepository.findByProjectIdAndUsername(projectId, authenticatedUser)
                .map(this::mapToDetailedProjectResponse)
                .map(GlobalResponse::success)
                .orElseThrow(() -> new ProjectException(
                        format("Project not exists / you have limited access - ID: %s, User: %s", projectId, authenticatedUser)
                ));
    }

    @Override
    @Transactional
    @CacheEvict(value = PROJECT_CACHE, allEntries = true)
    public GlobalResponse<Boolean> updateProject(
            String projectId,
            ProjectRequest request,
            String authenticatedUser
    ) {

        Project project = projectRepository.findByProjectIdAndUsername(projectId, authenticatedUser)
                .orElseThrow(() -> new ProjectException(
                        format("Project not found - ID: %s, User: %s", projectId, authenticatedUser)
                ));

        updateProjectFields(project, request, authenticatedUser);

        try {
            Project updatedProject = projectRepository.save(project);

            auditService.audit(
                    project(updatedProject),
                    AuditAction.PROJECT_UPDATE,
                    authenticatedUser,
                    project(project),
                    format("Project details updated by %s on %s.", authenticatedUser, LocalDateTime.now())
            );

            notifyCollaborators(updatedProject, NotificationTypes.PROJECT_UPDATED);

            return GlobalResponse.success(true);
        } catch (Exception e) {
            log.error("Failed to update project - ID: {}", projectId, e);
            throw new ProjectException("Failed to update project", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = PROJECT_CACHE, allEntries = true)
    public GlobalResponse<Boolean> deleteProject(String projectId, String authenticatedUser) {
        try {
            Project project = findAndValidateProjectForDelete(projectId, authenticatedUser);

            deleteProjectResources(project);
            projectRepository.delete(project);

            auditService.audit(
                    project(project),
                    AuditAction.PROJECT_DELETION,
                    authenticatedUser,
                    null,
                    format("Project soft deleted by %s on %s", authenticatedUser, LocalDateTime.now())
            );

            notifyCollaborators(project, NotificationTypes.PROJECT_DELETED);

            log.info("Successfully deleted project with ID: {}", projectId);
            return GlobalResponse.success(true);
        } catch (Exception e) {
            log.error("Failed to delete project - ID: {}", projectId, e);
            throw new ProjectException("Failed to delete project", e);
        }
    }

    @Override
    public GlobalResponse<List<ProjectResponse>> filterProject(
            String authenticatedUser,
            String query,
            int page,
            int limit
    ) {
        try {
            validatePaginationParams(page, limit);
            PageRequest pageRequest = createPageRequest(page, limit);
            Page<Project> projectPage = projectRepository.findByTitleContainingOrDescriptionContaining(
                    query, authenticatedUser, pageRequest
            );

            return createProjectResponse(projectPage);
        } catch (Exception e) {
            log.error("Error filtering projects - User: {}, Query: {}", authenticatedUser, query, e);
            throw new ProjectException("Failed to filter projects", e);
        }
    }

    private GlobalResponse<List<ProjectResponse>> createProjectResponse(Page<Project> page) {
        return GlobalResponse.success(page
                        .getContent()
                        .stream()
                        .map(project -> ProjectResponse
                                .builder()
                                .projectId(project.getProjectId())
                                .createdAt(project.getCreatedAt().toString())
                                .createdBy(findUserByUsernameAndMap(project.getCreatedBy()))
                                .updatedAt(project.getUpdatedAt().toString())
                                .updatedBy(findUserByUsernameAndMap(project.getUpdatedBy()))
                                .notes(project.getNotes().isEmpty() ? null : mapToNoteResponse(project.getNotes()))
                                .drafts(project.getDrafts().isEmpty() ? null : mapToDraftResponse(project.getDrafts()))
                                .build()
                        )
                        .toList(),
                Paging.builder()
                        .first(page.isFirst())
                        .last(page.isLast())
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElement(page.getTotalElements())
                        .totalPage(page.getTotalPages())
                        .build()
        );
    }

    private CollaboratorResponse findUserByUsernameAndMap(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        try {
            return Optional.ofNullable(authorizationClient.userByUsername(username))
                    .map(GlobalResponse::getData)
                    .map(response -> CollaboratorResponse.builder()
                            .name(response.getFirstName() + " " + response.getLastName())
                            .username(response.getUsername())
                            .profile(response.getProfile())
                            .email(response.getEmail())
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        } catch (Exception e) {
            throw new ProjectException("Failed to fetch user details for: " + username, e);
        }
    }

    private Set<NoteResponse> mapToNoteResponse(Set<Note> notes) {
        return notes == null ? Collections.emptySet() :
                notes.stream()
                        .filter(Objects::nonNull)
                        .map(note -> NoteResponse.builder()
                                .noteId(note.getNoteId())
                                .title(note.getTitle())
                                .content(note.getContent())
                                .images(
                                        note.getImages().isEmpty() ? null : note.getImages().stream()
                                                .map(image -> ImageResponse
                                                        .builder()
                                                        .imageId(image.getImageId())
                                                        .path(image.getPath())
                                                        .name(image.getName())
                                                        .type(image.getType())
                                                        .size(image.getSize())
                                                        .createdAt(image.getCreatedAt().toString())
                                                        .updatedAt(image.getUpdatedAt().toString())
                                                        .createdBy(findUserByUsernameAndMap(image.getCreatedBy()))
                                                        .updatedBy(findUserByUsernameAndMap(image.getUpdatedBy()))
                                                        .build())
                                                .collect(Collectors.toSet())
                                )
                                .createdAt(note.getCreatedAt().toString())
                                .updatedAt(note.getUpdatedAt().toString())
                                .createdBy(findUserByUsernameAndMap(note.getCreatedBy()))
                                .updatedBy(findUserByUsernameAndMap(note.getUpdatedBy()))
                                .build())
                        .collect(Collectors.toSet());
    }

    private Set<DraftResponse> mapToDraftResponse(Set<Draft> drafts) {
        return drafts == null ? Collections.emptySet() :
                drafts.stream()
                        .map(note -> DraftResponse.builder()
                                .draftId(note.getDraftId())
                                .title(note.getTitle())
                                .content(note.getContent())
                                .images(
                                        note.getImages().isEmpty() ? null : note.getImages().stream()
                                                .map(image -> ImageResponse
                                                        .builder()
                                                        .imageId(image.getImageId())
                                                        .path(image.getPath())
                                                        .name(image.getName())
                                                        .type(image.getType())
                                                        .size(image.getSize())
                                                        .createdAt(image.getCreatedAt().toString())
                                                        .updatedAt(image.getUpdatedAt().toString())
                                                        .createdBy(findUserByUsernameAndMap(image.getCreatedBy()))
                                                        .updatedBy(findUserByUsernameAndMap(image.getUpdatedBy()))
                                                        .build())
                                                .collect(Collectors.toSet())
                                )
                                .createdAt(note.getCreatedAt().toString())
                                .updatedAt(note.getUpdatedAt().toString())
                                .createdBy(findUserByUsernameAndMap(note.getCreatedBy()))
                                .updatedBy(findUserByUsernameAndMap(note.getUpdatedBy()))
                                .build()
                        )
                        .collect(Collectors.toSet());
    }

    private void validateProjectRequest(ProjectRequest request, String creator) {
        if (request == null) {
            throw new ProjectException("Project request cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ProjectException("Project title cannot be empty");
        }

        List<Project> existingProjects = projectRepository.findByTitleAndCreator(
                request.getTitle(), creator
        );

        if (!existingProjects.isEmpty()) {
            throw new ProjectException(
                    format("Project with title '%s' already exists", request.getTitle())
            );
        }

    }

    private Project buildProject(ProjectRequest request) {
        return Project.builder()
                .projectId(UUID.randomUUID().toString())
                .title(request.getTitle())
                .description(request.getDescription())
                .collaborators(new HashSet<>())
                .build();
    }

    private boolean hasCollaborators(ProjectRequest request) {
        return Objects.nonNull(request.getCollaborators())
                && !request.getCollaborators().isEmpty();
    }

    private void validateCollaborators(Set<CollaboratorRequest> collaborators, String creator) {
        if (collaborators.contains(creator)) {
            throw new ProjectException("Creator cannot be added as collaborator");
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
                        String.format("Failed to add collaborator: %s", request.getUsername()), e
                );
            }
        }

    }

    private Authority authority(String authorityName) {
        return authorityRepository.findByName(authorityName)
                .orElseThrow(() -> new ProjectException(format("Authority %s not found", authorityName)));
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
                .profile(user.getProfile().getPath())
                .build();
    }

    private void validatePaginationParams(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    format("Page size must be between 1 and %d", MAX_PAGE_SIZE)
            );
        }
    }

    private PageRequest createPageRequest(int pageNumber, int pageSize) {
        return PageRequest.of(
                pageNumber,
                Math.min(pageSize, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD)
        );
    }

    private ProjectResponse mapToDetailedProjectResponse(Project project) {
        return ProjectResponse.builder()
                .projectId(project.getProjectId())
                .title(project.getTitle())
                .description(project.getDescription())
                .collaborators(mapCollaborators(project.getCollaborators()))
                .createdAt(project.getCreatedAt().toString())
                .updatedAt(project.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(project.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(project.getUpdatedBy()))
                .notes(mapNotes(project.getNotes()))
                .drafts(mapDrafts(project.getDrafts()))
                .build();
    }

    private CollaboratorResponse mapToCollaboratorResponse(Collaborator collaborator) {
        return CollaboratorResponse.builder()
                .collaboratorId(collaborator.getCollaboratorId())
                .name(collaborator.getName())
                .username(collaborator.getUsername())
                .email(collaborator.getEmail())
                .profile(imageResponse(collaborator.getProfile()))
                .build();
    }

    private Set<CollaboratorResponse> mapCollaborators(Set<Collaborator> collaborators) {
        return collaborators.stream()
                .map(this::mapToCollaboratorResponse)
                .collect(Collectors.toSet());
    }

    private Set<NoteResponse> mapNotes(Set<Note> notes) {
        return notes.stream()
                .map(this::mapToNoteResponse)
                .collect(Collectors.toSet());
    }

    private NoteResponse mapToNoteResponse(Note note) {
        return NoteResponse.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt().toString())
                .updatedAt(note.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(note.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(note.getUpdatedBy()))
                .images(
                        note.getImages().isEmpty() ? null : note.getImages().stream()
                                .map(this::imageResponse)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    private Set<DraftResponse> mapDrafts(Set<Draft> drafts) {
        return drafts.stream()
                .map(this::mapToDraftResponse)
                .collect(Collectors.toSet());
    }

    private DraftResponse mapToDraftResponse(Draft draft) {
        return DraftResponse.builder()
                .draftId(draft.getDraftId())
                .title(draft.getTitle())
                .content(draft.getContent())
                .createdAt(draft.getCreatedAt().toString())
                .updatedAt(draft.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(draft.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(draft.getUpdatedBy()))
                .images(draft.getImages().isEmpty() ? null : draft.getImages().stream()
                        .map(this::imageResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    private Project findAndValidateProjectForUpdate(String projectId, String authenticatedUser) {
        Project project = projectRepository.findByProjectIdAndUsername(projectId, authenticatedUser)
                .orElseThrow(() -> new ProjectException(
                        format("Project not found - ID: %s, User: %s", projectId, authenticatedUser)
                ));

//        if (!isOwner(project, authenticatedUser)) {
//            throw new ProjectException("User not authorized to modify project");
//        }

        return project;
    }

    private Project findAndValidateProjectForDelete(String projectId, String authenticatedUser) {
        Project project = findAndValidateProjectForUpdate(projectId, authenticatedUser);

        // Additional validation specific to deletion if needed
        validateProjectDeletion(project);

        return project;
    }

    private void validateProjectDeletion(Project project) {
        // Add any specific validation rules for project deletion
        // For example, checking if there are any dependent resources
        // that need to be handled before deletion
    }

    private void updateProjectFields(Project project, ProjectRequest request, String authenticatedUser) {
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            project.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (hasCollaborators(request)) {
            validateCollaborators(request.getCollaborators(), authenticatedUser);
            addCollaboratorsToProject(project, request.getCollaborators());
        }

        project.setUpdatedBy(authenticatedUser);
    }

    private void removeCollaboratorFromProject(Project project, Set<String> usernames) {
        project.getCollaborators().removeIf(c -> usernames.contains(c.getUsername()));
    }

    private void deleteProjectResources(Project project) {
        deleteNotes(project);
        deleteDrafts(project);
        deleteImages(project);
    }

    private void deleteNotes(Project project) {
        log.debug("Deleting notes for project: {}", project.getProjectId());
        List<Note> notes = noteRepository.findAllByProjectId(project.getProjectId());

        notes.forEach(note -> {
            deleteImagesForEntity(note.getImages());
            noteRepository.delete(note);
        });

        log.debug("Deleted {} notes for project: {}", notes.size(), project.getProjectId());
    }

    private void deleteDrafts(Project project) {
        log.debug("Deleting drafts for project: {}", project.getProjectId());
        List<Draft> drafts = draftRepository.findAllByProjectId(project.getProjectId());

        drafts.forEach(draft -> {
            deleteImagesForEntity(draft.getImages());
            draftRepository.delete(draft);
        });

        log.debug("Deleted {} drafts for project: {}", drafts.size(), project.getProjectId());
    }

    private void deleteImages(Project project) {
        log.debug("Deleting images for project: {}", project.getProjectId());
        Set<Image> images = new HashSet<>();
        for (Note note : project.getNotes()) {
            images.addAll(note.getImages());
        }

        for (Draft draft : project.getDrafts()) {
            images.addAll(draft.getImages());
        }

        images.forEach(this::deleteImage);
        log.debug("Deleted {} images for project: {}", images.size(), project.getProjectId());
    }

    private void deleteImagesForEntity(Set<Image> images) {
        if (images != null) {
            images.forEach(this::deleteImage);
        }
    }

    private void deleteImage(Image image) {
        try {
            imageRepository.delete(image);
        } catch (Exception e) {
            log.error("Failed to delete image: {}", image.getImageId(), e);
        }
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

    private ImageResponse imageResponse(Image image) {
        return ImageResponse
                .builder()
                .imageId(image.getImageId())
                .path(image.getPath())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .createdAt(image.getCreatedAt().toString())
                .updatedAt(image.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(image.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(image.getUpdatedBy()))
                .build();
    }

    private ImageResponse imageResponse(String path) {
        return ImageResponse
                .builder()
                .path(path)
                .build();
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
}
