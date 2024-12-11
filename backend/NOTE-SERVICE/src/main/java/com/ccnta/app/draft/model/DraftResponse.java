package com.ccnta.app.draft.model;

import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class DraftResponse {

    private String draftId;
    private String title;
    private String content;
    private Set<ImageResponse> images;
    private CollaboratorResponse createdBy;
    private CollaboratorResponse updatedBy;
    private String createdAt;
    private String updatedAt;

}
