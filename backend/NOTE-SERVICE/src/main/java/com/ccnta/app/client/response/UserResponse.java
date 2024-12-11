package com.ccnta.app.client.response;

import com.ccnta.app.media.entity.Image;
import com.ccnta.app.media.model.ImageResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private ImageResponse profile;
    private boolean enabled;
    private Set<String> roles;

}
