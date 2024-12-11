package com.ccnta.app.authority.service;

import com.ccnta.app.authority.model.AuthorityRequest;
import com.ccnta.app.shared.GlobalResponse;

import java.util.Map;
import java.util.Set;

public interface IAuthorityService {

    GlobalResponse<Void> create(Set<AuthorityRequest> requests);
    GlobalResponse<Map<String, Set<String>>> getAll();
    GlobalResponse<Map<String, Set<String>>> getByAuthority(String authority);
    GlobalResponse<Void> update(String authorityId, Set<AuthorityRequest> requests);

    GlobalResponse<Void> delete(String authorityName);
}
