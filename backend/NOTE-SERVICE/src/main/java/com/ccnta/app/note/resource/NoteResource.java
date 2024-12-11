package com.ccnta.app.note.resource;

import com.ccnta.app.note.model.NoteRequest;
import com.ccnta.app.note.model.NoteResponse;
import com.ccnta.app.note.service.INoteService;
import com.ccnta.app.shared.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteResource {

    private final INoteService noteService;

    @PostMapping
    public ResponseEntity<GlobalResponse<Void>> createNote(
            @Valid NoteRequest request,
            @RequestParam(value = "images", required = false) Set<MultipartFile> images,
            @RequestParam(value= "projectId", required = true) String projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                noteService.createNote(authentication.getName(), projectId, request, images)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<GlobalResponse<List<NoteResponse>>> getAllNotes(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                noteService.getAllNotes(authentication.getName(), page, size)
        );
    }

    @GetMapping("/by.projectId/{projectId}")
    public ResponseEntity<GlobalResponse<List<NoteResponse>>> getAllNotesByProjectId(
            @PathVariable(name = "projectId") String projectId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ){
        return ResponseEntity.ok(
                noteService.getNotesByProjectId(projectId, authentication.getName(), page, size)
        );
    }

    //update note
    @PutMapping("/{noteId}")
    public ResponseEntity<GlobalResponse<Void>> updateNote(
            @PathVariable(name = "noteId") String noteId,
            @Valid NoteRequest request,
            @RequestParam(value = "images", required = false) Set<MultipartFile> images,
            @RequestParam(value="projectId", required = true) String projectId,
            Authentication authentication) {
        return ResponseEntity.ok(
                noteService.updateNote(noteId, projectId, authentication.getName(), request, images)
        );
    }

    //delete note
    @DeleteMapping("/{noteId}")
    public ResponseEntity<GlobalResponse<Void>> deleteNote(
            @PathVariable(name = "noteId") String noteId,
            Authentication authentication) {
        return ResponseEntity.ok(
                noteService.deleteNote(noteId, authentication.getName())
        );
    }

    //note details
    @GetMapping("/{noteId}")
    public ResponseEntity<GlobalResponse<NoteResponse>> noteDetails(
            @PathVariable(name = "noteId") String noteId,
            Authentication authentication) {
        return ResponseEntity.ok(
                noteService.noteDetails(noteId, authentication.getName())
        );
    }
}
