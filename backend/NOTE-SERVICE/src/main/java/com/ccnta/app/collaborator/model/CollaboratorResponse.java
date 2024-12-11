package com.ccnta.app.collaborator.model;

import com.ccnta.app.media.model.ImageResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class CollaboratorResponse {

    private String collaboratorId;
    private String name;
    private String username;
    private String email;
    private ImageResponse profile;
    private Map<String, Set<String>> authorities;
    private String createdAt;
    private String updatedAt;

}
