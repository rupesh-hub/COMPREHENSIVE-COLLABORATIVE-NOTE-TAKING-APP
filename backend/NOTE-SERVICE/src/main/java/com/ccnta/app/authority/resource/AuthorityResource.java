package com.ccnta.app.authority.resource;

import com.ccnta.app.authority.model.AuthorityRequest;
import com.ccnta.app.authority.service.IAuthorityService;
import com.ccnta.app.shared.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/authorities")
@RequiredArgsConstructor
public class AuthorityResource {

    private final IAuthorityService authorityService;

    @PostMapping
    public ResponseEntity<GlobalResponse<Void>> createAuthorities(
            @Valid @RequestBody Set<AuthorityRequest> requests
    ) {
        return ResponseEntity.ok(authorityService.create(requests));
    }

    @GetMapping
    public ResponseEntity<GlobalResponse<Map<String, Set<String>>>> getAllAuthorities() {
        return ResponseEntity.ok(authorityService.getAll());
    }

    @GetMapping("/{authority}")
    public ResponseEntity<GlobalResponse<Map<String, Set<String>>>> getAuthorityDetails(
            @PathVariable String authority
    ) {
        return ResponseEntity.ok(authorityService.getByAuthority(authority));
    }

    @PutMapping("/{authorityId}")
    public ResponseEntity<GlobalResponse<Void>> updateAuthority(
            @PathVariable String authorityId,
            @Valid @RequestBody Set<AuthorityRequest> requests
    ) {
        return ResponseEntity.ok(authorityService.update(authorityId, requests));
    }

    @DeleteMapping("/{authorityName}")
    public ResponseEntity<GlobalResponse<Void>> deleteAuthority(@PathVariable String authorityName) {
        return ResponseEntity.ok(authorityService.delete(authorityName));
    }

}
