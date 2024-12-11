package com.ccnta.app.draft.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DraftRequest {

    @NotNull(message = "Title must not be null.")
    @NotBlank(message = "Title must not be blank.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    private String title;

    @Size(max = 50000, message = "Content must not exceed 50000 characters.")
    private String content;

}
