package com.ccnta.app.project.model;

import com.ccnta.app.collaborator.model.CollaboratorRequest;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {

    private String title;
    private String description;
    private Set<CollaboratorRequest> collaborators;

}
