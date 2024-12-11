package com.ccnta.app.authority.service;

import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.authority.entity.Permission;
import com.ccnta.app.authority.model.AuthorityRequest;
import com.ccnta.app.authority.repository.AuthorityRepository;
import com.ccnta.app.authority.repository.PermissionRepository;
import com.ccnta.app.exception.ProjectException;
import com.ccnta.app.shared.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthorityService implements IAuthorityService {

    private final AuthorityRepository authorityRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public GlobalResponse<Void> create(Set<AuthorityRequest> requests) {
        validateCreateRequest(requests);
        try {
            createAuthoritiesWithPermissions(requests);
            log.info("Successfully created {} authorities", requests.size());
            return GlobalResponse.success();
        } catch (Exception e) {
            log.error("Error creating authorities", e);
            throw new ProjectException("Failed to create authorities: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse<Map<String, Set<String>>> getAll() {
        try {
            // Retrieve all authorities and group by some criteria
            Map<String, Set<String>> authorityMap = authorityRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(
                            Authority::getName,
                            Collectors.mapping(
                                    Authority::getName,
                                    Collectors.toSet()
                            )
                    ));

            return GlobalResponse.success(authorityMap);
        } catch (Exception e) {
            log.error("Error retrieving all authorities", e);
            throw new ProjectException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GlobalResponse<Map<String, Set<String>>> getByAuthority(String authority) {
        try {
            // Validate input
            if (authority == null || authority.trim().isEmpty()) {
                log.warn("Attempt to get authorities with null or empty authority");
                throw new ProjectException("Invalid authority");
            }

            // Find authorities matching the given authority
            Optional<Authority> authorityOptional = authorityRepository.findByName(authority);

            if (authorityOptional.isEmpty()) {
                log.warn("No authorities found for: {}", authority);
                throw new ProjectException("No authorities found");
            }

            // Group authorities by their permissions
            Map<String, Set<String>> authorityDetails = authorityOptional
                    .stream()
                    .collect(Collectors.toMap(
                            Authority::getName,
                            auth -> auth.getPermissions().stream()
                                    .map(Permission::getName)
                                    .collect(Collectors.toSet()),
                            (v1, v2) -> v1 // In case of duplicate keys, keep the first set
                    ));

            return GlobalResponse.success(authorityDetails);
        } catch (Exception e) {
            log.error("Error retrieving authorities for {}", authority, e);
            throw new ProjectException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public GlobalResponse<Void> update(String authorityId, Set<AuthorityRequest> requests) {
        validateUpdateRequest(authorityId, requests);
        try {
            Authority existingAuthority = findAuthorityByAuthorityId(authorityId);
            AuthorityRequest request = requests.iterator().next();
            updateAuthorityDetails(existingAuthority, request);
            updateAuthorityPermissions(existingAuthority, request);
            log.info("Successfully updated authority: {}", authorityId);
            return GlobalResponse.success();
        } catch (NoSuchElementException e) {
            log.warn("Update failed: Authority not found - {}", authorityId);
            return GlobalResponse.failure("Authority not found");
        } catch (Exception e) {
            log.error("Error updating authority: {}", authorityId, e);
            throw new ProjectException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public GlobalResponse<Void> delete(String authorityName) {
        try {
            validateAuthorityName(authorityName);
            Authority existingAuthority = findAuthorityByName(authorityName);
            deletePermissionsForAuthority(existingAuthority);
            authorityRepository.delete(existingAuthority);
            log.info("Successfully deleted authority: {}", authorityName);
            return GlobalResponse.success();
        } catch (NoSuchElementException e) {
            log.warn("Delete failed: Authority not found - {}", authorityName);
            throw new ProjectException("Authority not found");
        } catch (Exception e) {
            log.error("Error deleting authority: {}", authorityName, e);
            throw new ProjectException("Failed to delete authority: " + e.getMessage());
        }
    }

    private void validateCreateRequest(Set<AuthorityRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            log.warn("Attempt to create authorities with empty request set");
            throw new IllegalArgumentException("No authority requests provided");
        }
        boolean hasInvalidRequests = requests.stream()
                .anyMatch(request -> request == null ||
                        request.getAuthority() == null ||
                        request.getAuthority().trim().isEmpty());

        if (hasInvalidRequests) {
            log.warn("Invalid authority requests detected");
            throw new IllegalArgumentException("Invalid authority requests");
        }

    }

    private void createAuthoritiesWithPermissions(Set<AuthorityRequest> requests) {
        for (AuthorityRequest request : requests) {
            Authority authority = createAuthority(request);
            createPermissionsForAuthority(authority, request.getPermissions());
        }
    }

    private Authority createAuthority(AuthorityRequest request) {
        Authority authority = new Authority();
        authority.setAuthorityId(UUID.randomUUID().toString());
        authority.setName(request.getAuthority());

        return authorityRepository.save(authority);
    }

    private void createPermissionsForAuthority(Authority authority, Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return;
        }
        Set<Permission> permissions = permissionNames.stream()
                .map(permName -> {
                    Permission permission = new Permission();
                    permission.setPermissionId(UUID.randomUUID().toString());
                    permission.setName(permName);
                    permission.setAuthority(authority);
                    return permission;
                })
                .collect(Collectors.toSet());
        permissionRepository.saveAll(permissions);
    }

    private void validateUpdateRequest(String authorityId, Set<AuthorityRequest> requests) {
        if (authorityId == null || authorityId.trim().isEmpty()) {
            log.warn("Attempt to update with null or empty authority ID");
            throw new IllegalArgumentException("Invalid authority ID");
        }

        if (requests == null || requests.isEmpty()) {
            log.warn("Attempt to update authority with empty request set");
            throw new IllegalArgumentException("No update requests provided");
        }
    }

    private Authority findAuthorityByAuthorityId(String authorityId) {
        return authorityRepository.findByAuthorityId(authorityId)
                .orElseThrow(() -> {
                    log.warn("Authority not found with ID: {}", authorityId);
                    return new NoSuchElementException("Authority not found");
                });
    }

    private void updateAuthorityDetails(Authority existingAuthority, AuthorityRequest request) {
        if (request.getAuthority() != null && !request.getAuthority().trim().isEmpty()) {
            existingAuthority.setName(request.getAuthority());
            authorityRepository.save(existingAuthority);
        }
    }

    private void updateAuthorityPermissions(Authority existingAuthority, AuthorityRequest request) {
        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            removeExistingPermissions(existingAuthority);
            createAndAddNewPermissions(existingAuthority, request.getPermissions());
        }
    }

    private void removeExistingPermissions(Authority existingAuthority) {
        if (!existingAuthority.getPermissions().isEmpty()) {
            permissionRepository.deleteAll(existingAuthority.getPermissions());
            existingAuthority.clearPermissions();
            authorityRepository.save(existingAuthority);
        }
    }

    private void createAndAddNewPermissions(Authority existingAuthority, Set<String> permissionNames) {
        Set<Permission> newPermissions = permissionNames.stream()
                .map(permName -> Permission.builder()
                        .permissionId(UUID.randomUUID().toString())
                        .name(permName)
                        .authority(existingAuthority)  // Crucially, set the authority here
                        .build())
                .collect(Collectors.toSet());
        permissionRepository.saveAll(newPermissions);
        newPermissions.forEach(existingAuthority::addPermission);
        authorityRepository.save(existingAuthority);
    }

    private void validateAuthorityName(String authorityName) {
        if (authorityName == null || authorityName.trim().isEmpty()) {
            log.warn("Attempt to delete with null or empty authority name");
            throw new IllegalArgumentException("Invalid authority name");
        }
    }

    private Authority findAuthorityByName(String authorityName) {
        return authorityRepository.findByName(authorityName)
                .orElseThrow(() -> {
                    log.warn("Authority not found with name: {}", authorityName);
                    return new NoSuchElementException("Authority not found");
                });
    }

    private void deletePermissionsForAuthority(Authority authority) {
        if (authority.getPermissions() != null && !authority.getPermissions().isEmpty()) {
            permissionRepository.deleteAll(authority.getPermissions());
            authority.clearPermissions();
        }
    }

}