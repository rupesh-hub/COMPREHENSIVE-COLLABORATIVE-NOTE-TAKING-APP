package com.ccnta.app.draft.service;

import com.ccnta.app.draft.model.DraftRequest;
import com.ccnta.app.draft.model.DraftResponse;
import com.ccnta.app.shared.GlobalResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface IDraftService {

    GlobalResponse<Void> create(String username, String projectId, DraftRequest request, Set<MultipartFile> images);

    GlobalResponse<Void> update(String draftId, String projectId, String username, DraftRequest request, Set<MultipartFile> images);
    GlobalResponse<DraftResponse> draftDetails(String draftId, String username);

    GlobalResponse<Void> delete(String draftId, String username);

    GlobalResponse<List<DraftResponse>> getAllDrafts(String username, int pageNumber, int limit);

}
