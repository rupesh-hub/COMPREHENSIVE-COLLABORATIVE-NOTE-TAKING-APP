package com.ccnta.app.role.service;


import com.ccnta.app.util.GlobalResponse;

import java.util.Set;

public interface IRoleService {

    GlobalResponse<Void> save(Set<String> request);
    GlobalResponse<Set<String>> getAll(int page, int size);
    GlobalResponse<Set<String>> getByName(String name);

}