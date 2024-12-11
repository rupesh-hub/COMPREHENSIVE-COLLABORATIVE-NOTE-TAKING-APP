package com.ccnta.app.media.model;

import com.ccnta.app.collaborator.model.CollaboratorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse {

    private String imageId;
    private String name;
    private String path;
    private Long size;
    private String type;
    private String createdAt;
    private String updatedAt;
    private CollaboratorResponse createdBy;
    private CollaboratorResponse updatedBy;

}
