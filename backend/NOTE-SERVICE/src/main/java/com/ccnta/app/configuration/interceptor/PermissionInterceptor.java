package com.ccnta.app.configuration.interceptor;

import com.ccnta.app.authority.emums.Permissions;
import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.authority.entity.Permission;
import com.ccnta.app.collaborator.entity.Collaborator;
import com.ccnta.app.collaborator.service.CollaboratorService;
import com.ccnta.app.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PermissionInterceptor.class);

    private final CollaboratorService collaboratorService;
    private final ProjectRepository projectRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Allow project creation for all authenticated users
        if ((uri.contains("/projects/create") && method.equals("POST"))
                || (uri.contains("/projects/all") && method.equals("GET"))
                || (uri.contains("/notes/all") && method.equals("GET"))
                || (uri.contains("/authorities")
        )) {
            return true;
        }

        logger.info("Checking permissions for user: {} on {} {}", username, method, uri);

        String projectId = getVariable(request, "projectId");
        String noteId = getVariable(request, "noteId");
        String draftId = getVariable(request, "draftId");

        if (projectId == null && !uri.endsWith("/projects") && !method.equals("POST")) {
            logger.error("Project ID not found in the request");
            sendForbiddenResponse(request, response, "Invalid request: Project ID is missing");
            return false;
        }

        // Check if the user is the project owner
        var projectOpt = projectRepository.findByProjectId(projectId, username);
        if (projectOpt.isPresent() && projectOpt.get().getCreatedBy().equals(username)) {
            return true; // Project owner has full access
        }

        Optional<Collaborator> collaboratorOpt = collaboratorService.getCollaboratorByUsernameAndProjectId(username, projectId);

        if (collaboratorOpt.isEmpty()) {
            logger.warn("User {} is not a collaborator on project {}", username, projectId);
            sendForbiddenResponse(request, response, "You do not have permission to access this project.");
            return false;
        }

        Collaborator collaborator = collaboratorOpt.get();
        Set<Authority> authorities = collaborator.getAuthorities();
        boolean isAuthorized = checkAuthorization(authorities, method, uri, noteId, draftId, collaborator);

        if (!isAuthorized) {
            logger.warn("User {} with role {} is not authorized to perform {} on {}", username, authorities, method, uri);
            sendForbiddenResponse(request, response, "You are not authorized to perform this action");
            return false;
        }

        logger.info("User {} with role {} is authorized to perform {} on {}", username, authorities, method, uri);
        return true;
    }

    private boolean checkAuthorization(Set<Authority> authorities, String method, String uri, String noteId, String draftId, Collaborator collaborator) {

        Set<String> authorityNames = authorities
                .stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = authorities
                .stream()
                .flatMap(ae -> ae.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // Use pattern matching switch expression with null checks
        return switch (getEndpointType(uri)) {
            case NOTES -> checkNotePermissions(method, permissions, authorityNames, noteId, collaborator);
            case COLLABORATORS -> checkCollaboratorPermissions(method, permissions);
            case DRAFTS -> checkDraftPermissions(method, permissions, authorityNames, draftId, collaborator);
            case PROJECT -> checkProjectPermissions(method, permissions);
        };
    }

    private EndpointType getEndpointType(String uri) {
        if (uri.endsWith("/notes") || uri.contains("/notes/")) return EndpointType.NOTES;
        if (uri.contains("/collaborators")) return EndpointType.COLLABORATORS;
        if (uri.contains("/drafts")) return EndpointType.DRAFTS;
        return EndpointType.PROJECT;
    }

    private boolean checkNotePermissions(String method, Set<String> permissions, Set<String> authorities, String noteId, Collaborator collaborator) {
        return switch (method.toUpperCase()) {
            case "GET" -> permissions.contains(Permissions.VIEW_NOTE.name());
            case "POST" -> permissions.contains(Permissions.CREATE_NOTE.name());
            case "PUT" -> permissions.contains(Permissions.EDIT_NOTE.name());
            case "DELETE" -> authorities.contains("EDITOR")
                    ? permissions.contains(Permissions.DELETE_NOTE.name()) && collaboratorService.isNoteOwnedByCollaborator(noteId, collaborator.getId())
                    : permissions.contains(Permissions.DELETE_NOTE.name());
            default -> false;
        };
    }

    private boolean checkCollaboratorPermissions(String method, Set<String> permissions) {
        return method.equalsIgnoreCase("GET")
                ? permissions.contains(Permissions.VIEW_COLLABORATORS.name())
                : permissions.contains(Permissions.MANAGE_COLLABORATORS.name());
    }

    private boolean checkDraftPermissions(String method, Set<String> permissions, Set<String> authorities, String draftId, Collaborator collaborator) {
        return switch (method.toUpperCase()) {
            case "GET" -> permissions.contains(Permissions.VIEW_NOTE.name());
            case "POST" -> permissions.contains(Permissions.CREATE_NOTE.name());
            case "PUT" -> permissions.contains(Permissions.EDIT_NOTE.name());
            case "DELETE" -> authorities.contains("EDITOR")
                    ? permissions.contains(Permissions.DELETE_NOTE.name()) && collaboratorService.isDraftOwnedByCollaborator(draftId, collaborator.getId())
                    : permissions.contains(Permissions.DELETE_NOTE.name());
            default -> false;
        };
    }

    private boolean checkProjectPermissions(String method, Set<String> permissions) {
        return switch (method.toUpperCase()) {
            case "GET" -> permissions.contains(Permissions.VIEW_PROJECT.name());
            case "PUT", "DELETE" ->
                    permissions.contains(Permissions.EDIT_PROJECT.name()) || permissions.contains(Permissions.DELETE_PROJECT.name());
            default -> false;
        };
    }

    private void sendForbiddenResponse(HttpServletRequest request, HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        Map<String, Object> errorDetails = Map.of(
                "timestamp", Instant.now().toString(),
                "uri", request.getRequestURI(),
                "method", request.getMethod(),
                "message", message
        );

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }

    private static String getVariable(HttpServletRequest request, String variableName) {
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null && pathVariables.containsKey(variableName)) {
            return pathVariables.get(variableName);
        }

        String paramValue = request.getParameter(variableName);
        if (paramValue != null) {
            return paramValue;
        }

//        try {
//            String body = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
//            if (!body.isEmpty()) {
//                Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);
//                if (bodyMap.containsKey(variableName)) {
//                    return bodyMap.get(variableName).toString();
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error reading request body", e);
//        }

        return null;
    }

    private enum EndpointType {
        NOTES, COLLABORATORS, DRAFTS, PROJECT
    }
}

