package com.ccnta.app.draft.service;

import com.ccnta.app.client.AuthorizationServerClient;
import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.draft.entity.Draft;
import com.ccnta.app.draft.model.DraftRequest;
import com.ccnta.app.draft.model.DraftResponse;
import com.ccnta.app.draft.repository.DraftRepository;
import com.ccnta.app.exception.ProjectException;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import com.ccnta.app.media.repository.ImageRepository;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.project.repository.ProjectRepository;
import com.ccnta.app.shared.GlobalResponse;
import com.ccnta.app.shared.Paging;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DraftService implements IDraftService {

    @Value("${application.image.upload-dir:assets/images/notes}")
    private String imageUploadDir;

    private static final int MAX_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String IMAGE_UPLOAD_ERROR = "Failed to save image: {}";
    private static final String DRAFT_NOT_FOUND = "Draft not found with id: %s";
    private static final String PROJECT_NOT_FOUND = "No project exists with ID %s for user %s";

    private final DraftRepository draftRepository;
    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;
    private final AuthorizationServerClient authorizationClient;


    @Transactional
    @Override
    public GlobalResponse<Void> create(String username, String projectId, DraftRequest request, Set<MultipartFile> images) {
        validateRequest(projectId, request, username);
        Project project = projectRepository.findByProjectIdAndUsername(projectId, username)
                .orElseThrow(() -> new ProjectException(String.format(PROJECT_NOT_FOUND, projectId, username)));

        try {
            Draft draft = createAndSaveDraft(request, project, username);
            processImages(images, draft);
            return GlobalResponse.success("Draft created successfully");
        } catch (Exception e) {
            log.error("Failed to create draft: {}", e.getMessage(), e);
            throw new ProjectException("Failed to create draft: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public GlobalResponse<Void> update(String draftId, String projectId, String username, DraftRequest request, Set<MultipartFile> images) {
        validateRequest(projectId, request, username);

        Draft draft = draftRepository.findByDraftId(draftId)
                .orElseThrow(() -> new ProjectException(String.format(DRAFT_NOT_FOUND, draftId)));

        try {
            updateDraftFields(draft, request, images, username);
            draftRepository.save(draft);
            return GlobalResponse.success("Draft updated successfully");
        } catch (Exception e) {
            log.error("Failed to update draft: {}", e.getMessage(), e);
            throw new ProjectException("Failed to update draft: " + e.getMessage());
        }
    }

    @Override
    public GlobalResponse<DraftResponse> draftDetails(String draftId, String username) {
       Draft draft = draftRepository.findByDraftId(draftId)
                .orElseThrow(() -> new ProjectException(String.format(DRAFT_NOT_FOUND, draftId)));
        return GlobalResponse.success(draftResponse(draft));
    }

    @Transactional
    @Override
    public GlobalResponse<Void> delete(String draftId, String username) {
        Draft draft = draftRepository.findByDraftId(draftId)
                .orElseThrow(() -> new ProjectException(String.format(DRAFT_NOT_FOUND, draftId)));

        try {
            deleteDraftAndAssociatedImages(draft);
            return GlobalResponse.success("Draft deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete draft: {}", e.getMessage(), e);
            throw new ProjectException("Failed to delete draft: " + e.getMessage());
        }
    }

    @Override
    public GlobalResponse<List<DraftResponse>> getAllDrafts(String username, int number, int limit) {
        PageRequest pageRequest = createPageRequest(number, limit);
        Page<Draft> page = draftRepository.findAllDrafts(username, pageRequest);

        return GlobalResponse.success(
                draftResponseList(page.getContent()),
                createPaging(page)
        );
    }

    // Helper methods for image handling
    private void processImages(Set<MultipartFile> images, Draft draft) {
        if (images == null || images.isEmpty()) {
            return;
        }

        images.stream()
                .map(this::uploadImage)
                .filter(Objects::nonNull)
                .peek(image -> {
                    image.setDraft(draft);
                    image.setEnabled(true);
                    image.setImageId(UUID.randomUUID().toString());
                })
                .forEach(imageRepository::save);
    }

    private Image uploadImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            createUploadDirectoryIfNotExists();
            String fileName = generateFileName(file);
            Path filePath = saveFile(file, fileName);

            return buildImageEntity(file, fileName, filePath);
        } catch (IOException e) {
            log.error(IMAGE_UPLOAD_ERROR, e.getMessage(), e);
            return null;
        }
    }

    private void createUploadDirectoryIfNotExists() {
        File uploadDir = new File(imageUploadDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    private String generateFileName(MultipartFile file) {
        return System.currentTimeMillis() + "_" +
                Optional.ofNullable(file.getOriginalFilename())
                        .orElse("image.jpg");
    }

    private Path saveFile(MultipartFile file, String fileName) throws IOException {
        Path filePath = Paths.get(imageUploadDir, fileName);
        Files.write(filePath, file.getBytes());
        return filePath;
    }

    private Image buildImageEntity(MultipartFile file, String fileName, Path filePath) {
        return Image.builder()
                .imageId(UUID.randomUUID().toString())
                .name(fileName)
                .path(filePath.toString())
                .type(file.getContentType())
                .size(file.getSize())
                .build();
    }

    // Validation and authorization methods
    private void validateRequest(String projectId, DraftRequest request, String username) {
        if (request == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Request and username cannot be null or empty");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Draft title cannot be null or empty");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Draft content cannot be null or empty");
        }
    }

    // Note operations helper methods
    private Draft createAndSaveDraft(DraftRequest request, Project project, String username) {
        return draftRepository.save(
                Draft.builder()
                        .draftId(UUID.randomUUID().toString())
                        .title(request.getTitle())
                        .content(request.getContent())
                        .project(project)
                        .build()
        );
    }

    private void deleteDraftAndAssociatedImages(Draft draft) {
        if (draft.getImages() != null && !draft.getImages().isEmpty()) {
            List<String> imagePaths = draftResponse(draft)
                    .getImages()
                    .stream()
                    .map(ImageResponse::getPath)
                    .collect(Collectors.toList());

            draftResponse(draft)
                    .getImages()
                    .clear();

            deleteImageFiles(imagePaths);
        }
        draftRepository.delete(draft);
    }

    private void deleteImageFiles(List<String> imagePaths) {
        imagePaths.forEach(path -> {
            try {
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                log.warn("Failed to delete image file: {}", path, e);
            }
        });
    }

    // Response building methods
    private PageRequest createPageRequest(int number, int size) {
        return PageRequest.of(
                number,
                Math.min(size, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD)
        );
    }

    private Paging createPaging(Page<Draft> page) {
        return Paging.builder()
                .first(page.isFirst())
                .last(page.isLast())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .build();
    }

    private List<DraftResponse> draftResponseList(List<Draft> drafts) {
        return drafts.stream()
                .map(this::draftResponse)
                .toList();
    }

    private DraftResponse draftResponse(Draft draft) {
        return DraftResponse.builder()
                .draftId(draft.getDraftId())
                .title(draft.getTitle())
                .content(draft.getContent())
                .createdAt(draft.getCreatedAt().toString())
                .updatedAt(draft.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(draft.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(draft.getUpdatedBy()))
                .images(buildImageResponseSet(draft.getImages()))
                .build();
    }

    private Set<ImageResponse> buildImageResponseSet(Set<Image> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .map(this::buildImageResponse)
                .collect(Collectors.toSet());
    }

    private ImageResponse buildImageResponse(Image image) {
        return ImageResponse.builder()
                .imageId(image.getImageId())
                .path(image.getPath())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .createdAt(image.getCreatedAt().toString())
                .updatedAt(image.getUpdatedAt().toString())
                .createdBy(image.getCreatedBy() != null ? findUserByUsernameAndMap(image.getCreatedBy()) : null)
                .updatedBy(image.getUpdatedBy() != null ? findUserByUsernameAndMap(image.getUpdatedBy()) : null)
                .build();
    }

    private CollaboratorResponse findUserByUsernameAndMap(String username) {
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

    private void updateDraftFields(Draft draft, DraftRequest request, Set<MultipartFile> files, String username) {
        updateBasicFields(draft, request);
        updateImages(draft, files);
    }

    private void updateBasicFields(Draft draft, DraftRequest request) {
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            draft.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            draft.setContent(request.getContent());
        }
    }

    private void updateImages(Draft draft, Set<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        List<String> oldImagePaths = draft.getImages().stream()
                .map(Image::getPath)
                .collect(Collectors.toList());

        draft.getImages().clear();
        processImages(files, draft);
        deleteImageFiles(oldImagePaths);
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
}