package com.ccnta.app.user.mapper;

import com.ccnta.app.image.Image;
import com.ccnta.app.role.entity.Role;
import com.ccnta.app.user.entity.User;
import com.ccnta.app.user.model.ImageResponse;
import com.ccnta.app.user.model.RegistrationRequest;
import com.ccnta.app.user.model.UserResponse;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(RegistrationRequest request) {
        return User
                .builder()
                .userId(randomString())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .confirmPassword(request.getConfirmPassword())
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse
                .builder()
                .userId(UUID.randomUUID().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .roles(
                        user
                                .getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .profile(
                        Optional.ofNullable(user.getProfile())
                                .map(UserMapper::imageResponse)
                                .orElse(null)
                )
                .build();
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static ImageResponse imageResponse(Image image){
        return ImageResponse
                .builder()
                .imageId(UUID.randomUUID().toString())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .createdAt(image.getCreatedAt().toString())
                .updatedAt(image.getUpdatedAt().toString())
                .path(image.getPath())
                .build();
    }
}
