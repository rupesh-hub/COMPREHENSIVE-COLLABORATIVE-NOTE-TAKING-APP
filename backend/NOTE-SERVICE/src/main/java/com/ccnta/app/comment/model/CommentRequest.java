package com.ccnta.app.comment.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {

    private String noteId;
    private String content;

}
