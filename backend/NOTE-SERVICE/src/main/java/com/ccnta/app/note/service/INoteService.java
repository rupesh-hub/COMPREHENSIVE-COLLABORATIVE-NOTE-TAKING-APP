package com.ccnta.app.note.service;

import com.ccnta.app.note.model.NoteRequest;
import com.ccnta.app.note.model.NoteResponse;
import com.ccnta.app.shared.GlobalResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface INoteService {

    GlobalResponse<Void> createNote(String username, String projectId, NoteRequest request, Set<MultipartFile> images);

    GlobalResponse<Void> updateNote(String noteId, String projectId, String username, NoteRequest request, Set<MultipartFile> images);
    GlobalResponse<NoteResponse> noteDetails(String noteId, String username);

    GlobalResponse<Void> deleteNote(String noteId, String username);

//    void archiveNote(String noteId, String username);
//
//    void restoreNote(String noteId, String username);

    GlobalResponse<List<NoteResponse>> getAllNotes(String username, int pageNumber, int limit);

    GlobalResponse<List<NoteResponse>> getNotesByProjectId(String projectId, String name, int page, int size);
}
