package com.ccnta.app.project.model;

import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.draft.model.DraftResponse;
import com.ccnta.app.note.model.NoteResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private String projectId;
    private String title;
    private String description;
    private String createdAt;
    private String updatedAt;
    private CollaboratorResponse createdBy;
    private CollaboratorResponse updatedBy;
    private Set<CollaboratorResponse> collaborators;
    private Set<NoteResponse> notes;
    private Set<DraftResponse> drafts;

}
