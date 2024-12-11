package com.ccnta.app.note.service;

import com.ccnta.app.client.AuthorizationServerClient;
import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.collaborator.repository.CollaboratorRepository;
import com.ccnta.app.exception.ProjectException;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import com.ccnta.app.media.repository.ImageRepository;
import com.ccnta.app.note.entity.Note;
import com.ccnta.app.note.model.NoteRequest;
import com.ccnta.app.note.model.NoteResponse;
import com.ccnta.app.note.repository.NoteRepository;
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
public class NoteService implements INoteService {

    @Value("${application.image.upload-dir:assets/images/notes}")
    private String imageUploadDir;

    private static final int MAX_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String IMAGE_UPLOAD_ERROR = "Failed to save image: {}";
    private static final String NOTE_NOT_FOUND = "Note not found with id: %s";
    private static final String PROJECT_NOT_FOUND = "No project exists with ID %s for user %s";
    private static final String UNAUTHORIZED_USER = "User is neither a collaborator nor an owner";
    private static final String INSUFFICIENT_PERMISSIONS = "User does not have permission to perform this operation";

    private final NoteRepository noteRepository;
    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AuthorizationServerClient authorizationClient;

    @Transactional
    @Override
    public GlobalResponse<Void> createNote(String username, String projectId, NoteRequest request, Set<MultipartFile> images) {
        validateRequest(request, projectId, username);

        Project project = projectRepository.findByProjectIdAndUsername(projectId, username)
                .orElseThrow(() -> new ProjectException(String.format(PROJECT_NOT_FOUND, projectId, username)));

        try {
            Note note = createAndSaveNote(request, project, username);
            processImages(images, note);
            return GlobalResponse.success("Note created successfully");
        } catch (Exception e) {
            log.error("Failed to create note: {}", e.getMessage(), e);
            throw new ProjectException("Failed to create note: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GlobalResponse<Void> updateNote(String noteId, String projectId, String username, NoteRequest request, Set<MultipartFile> images) {
        validateRequest(request, projectId, username);
        Note note = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ProjectException(String.format(NOTE_NOT_FOUND, noteId)));

        try {
            updateNoteFields(note, request, images, username);
            noteRepository.save(note);
            return GlobalResponse.success("Note updated successfully");
        } catch (Exception e) {
            log.error("Failed to update note: {}", e.getMessage(), e);
            throw new ProjectException("Failed to update note: " + e.getMessage());
        }
    }

    @Override
    public GlobalResponse<NoteResponse> noteDetails(String noteId, String username) {
        Note note = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ProjectException(String.format(NOTE_NOT_FOUND, noteId)));

        return GlobalResponse.success(noteResponse(note));
    }

    @Override
    @Transactional
    public GlobalResponse<Void> deleteNote(String noteId, String username) {
        Note note = noteRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ProjectException(String.format(NOTE_NOT_FOUND, noteId)));
        try {
            deleteNoteAndAssociatedImages(note);
            return GlobalResponse.success("Note deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete note: {}", e.getMessage(), e);
            throw new ProjectException("Failed to delete note: " + e.getMessage());
        }
    }

    @Override
    public GlobalResponse<List<NoteResponse>> getAllNotes(String username, int number, int limit) {
        PageRequest pageRequest = createPageRequest(number, limit);
        Page<Note> page = noteRepository.findAllNotes(username, pageRequest);

        return GlobalResponse.success(
                noteResponseList(page.getContent()),
                createPaging(page, number, limit)
        );
    }

    @Override
    public GlobalResponse<List<NoteResponse>> getNotesByProjectId(String projectId, String username, int number, int limit) {
        PageRequest pageRequest = createPageRequest(number, limit);
        Page<Note> page = noteRepository.findNotesByProjectAndUser(projectId, username, pageRequest);

        return GlobalResponse.success(
                noteResponseList(page.getContent()),
                createPaging(page, number, limit)
        );
    }

    // Helper methods for image handling
    private void processImages(Set<MultipartFile> images, Note note) {
        if (images == null || images.isEmpty()) {
            return;
        }

        images.stream()
                .map(this::uploadImage)
                .filter(Objects::nonNull)
                .peek(image -> {
                    image.setNote(note);
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
    private void validateRequest(NoteRequest request, String projectId, String username) {
        if (request == null || username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Request and username cannot be null or empty");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Note title cannot be null or empty");
        }
    }

    // Note operations helper methods
    private Note createAndSaveNote(NoteRequest request, Project project, String username) {
        Note note = Note.builder()
                .noteId(UUID.randomUUID().toString())
                .title(request.getTitle())
                .content(request.getContent())
                .project(project)
                .build();

        return noteRepository.save(note);
    }

    private void deleteNoteAndAssociatedImages(Note note) {
        if (note.getImages() != null && !note.getImages().isEmpty()) {
            List<String> imagePaths = note.getImages().stream()
                    .map(Image::getPath)
                    .collect(Collectors.toList());

            note.getImages().clear();
            deleteImageFiles(imagePaths);
        }
        noteRepository.delete(note);
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

    private Paging createPaging(Page<Note> page, int number, int limit) {
        return Paging.builder()
                .first(page.isFirst())
                .last(page.isLast())
                .page(number)
                .size(limit)
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .build();
    }

    private List<NoteResponse> noteResponseList(List<Note> notes) {
        return notes.stream()
                .map(this::noteResponse)
                .toList();
    }

    private NoteResponse noteResponse(Note note) {
        return NoteResponse.builder()
                .noteId(note.getNoteId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt().toString())
                .updatedAt(note.getUpdatedAt().toString())
                .createdBy(findUserByUsernameAndMap(note.getCreatedBy()))
                .updatedBy(findUserByUsernameAndMap(note.getUpdatedBy()))
                .images(buildImageResponseSet(note.getImages()))
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

    private void updateNoteFields(Note note, NoteRequest request, Set<MultipartFile> files, String username) {
        updateBasicFields(note, request, username);
        updateImages(note, files);
    }

    private void updateBasicFields(Note note, NoteRequest request, String username) {
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        note.setUpdatedBy(username);
    }

    private void updateImages(Note note, Set<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        List<String> oldImagePaths = note.getImages().stream()
                .map(Image::getPath)
                .collect(Collectors.toList());

        note.getImages().clear();
        processImages(files, note);
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