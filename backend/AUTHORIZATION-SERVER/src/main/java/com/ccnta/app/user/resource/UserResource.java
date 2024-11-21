package com.ccnta.app.user.resource;

import com.ccnta.app.user.model.RegistrationRequest;
import com.ccnta.app.user.model.UserResponse;
import com.ccnta.app.user.service.IUserService;
import com.ccnta.app.util.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "users")
public class UserResource {

    private final IUserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public GlobalResponse<Boolean> register(@Valid RegistrationRequest request,
                                            @RequestParam(required = false) MultipartFile profile)
            throws IOException, MethodArgumentNotValidException {
        userService.register(request, profile);
        return GlobalResponse.success(true);
    }

    @GetMapping("/by.id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<UserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/by.userId/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<UserResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @GetMapping("/by.username/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<UserResponse>> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping("/by.email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<UserResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<Boolean>> update(
            @RequestBody final RegistrationRequest request,
            @PathVariable(name="userId") String userId,
            @RequestParam(required = false) MultipartFile profile

    ) throws IOException {
        return ResponseEntity.ok(userService.update(request, userId, profile));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<Boolean>> delete(
            @PathVariable("userId") String userId
    ) throws IOException {
        return ResponseEntity.ok(userService.delete(userId));
    }

    @PutMapping("/assign.roles")
    public ResponseEntity<GlobalResponse<Boolean>> assignRole(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "names") String[] names
    ) {
        return ResponseEntity.ok(userService.assignRole(userId, names));
    }

    @PutMapping("/remove.roles")
    public ResponseEntity<GlobalResponse<Boolean>> unAssignRole(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "names") String[] names
    ) {
        return ResponseEntity.ok(userService.removeRole(userId, names));
    }

}