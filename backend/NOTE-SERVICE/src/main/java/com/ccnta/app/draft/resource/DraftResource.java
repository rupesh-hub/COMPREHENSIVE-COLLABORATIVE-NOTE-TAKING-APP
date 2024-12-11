package com.ccnta.app.draft.resource;

import com.ccnta.app.draft.model.DraftRequest;
import com.ccnta.app.draft.model.DraftResponse;
import com.ccnta.app.draft.service.IDraftService;
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
@RequestMapping("/drafts")
@RequiredArgsConstructor
public class DraftResource {

    private final IDraftService draftService;

    //create
    @PostMapping
    public ResponseEntity<GlobalResponse<Void>> create(
            @Valid DraftRequest request,
            @RequestParam(value = "projectId", required = true) String projectId,
            @RequestParam(value = "images", required = false) Set<MultipartFile> images,
            Authentication authentication
    ) {
        ;
        return ResponseEntity.ok(
                draftService.create(authentication.getName(), projectId, request, images)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<GlobalResponse<List<DraftResponse>>> getAllDrafts(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                draftService.getAllDrafts(authentication.getName(), page, size)
        );
    }

    //update
    @PutMapping("/{draftId}")
    public ResponseEntity<GlobalResponse<Void>> update(
            @PathVariable(name = "draftId") String draftId,
            @Valid DraftRequest request,
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "images", required = false) Set<MultipartFile> images,
            Authentication authentication) {
        return ResponseEntity.ok(
                draftService.update(draftId, projectId, authentication.getName(), request, images)
        );
    }

    @DeleteMapping
    public ResponseEntity<GlobalResponse<Void>> delete(
            @RequestParam(value = "draftId", required = true) String draftId,
            Authentication authentication) {
        return ResponseEntity.ok(
                draftService.delete(draftId, authentication.getName())
        );
    }

    //note details
    @GetMapping("/{draftId}")
    public ResponseEntity<GlobalResponse<DraftResponse>> draftDetails(
            @PathVariable(name = "draftId") String draftId,
            Authentication authentication) {
        return ResponseEntity.ok(
                draftService.draftDetails(draftId, authentication.getName())
        );
    }

}
