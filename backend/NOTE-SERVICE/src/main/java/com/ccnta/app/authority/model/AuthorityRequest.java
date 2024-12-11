package com.ccnta.app.authority.model;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityRequest {

    private String authority;
    private Set<String> permissions;

}
