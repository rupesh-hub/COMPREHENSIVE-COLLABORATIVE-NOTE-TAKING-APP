package com.ccnta.app.comment.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyRequest {

    private String commentId;
    private String content;

}
