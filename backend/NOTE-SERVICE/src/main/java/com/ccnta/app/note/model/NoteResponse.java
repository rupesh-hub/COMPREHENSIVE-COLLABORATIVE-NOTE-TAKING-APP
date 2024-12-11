package com.ccnta.app.note.model;

import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoteResponse {

    private String noteId;
    private String title;
    private String content;
    private Set<ImageResponse> images;
    private CollaboratorResponse createdBy;
    private CollaboratorResponse updatedBy;
    private String createdAt;
    private String updatedAt;

}
