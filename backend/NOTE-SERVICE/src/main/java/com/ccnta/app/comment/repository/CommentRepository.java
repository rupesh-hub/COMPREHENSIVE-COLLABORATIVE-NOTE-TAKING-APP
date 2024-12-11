package com.ccnta.app.comment.repository;

import com.ccnta.app.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
                SELECT c
                FROM Comment c
                WHERE c.note.noteId = :noteId
                ORDER BY c.createdAt DESC
            """)
    List<Comment> findByNoteIdOrderByCreatedAtDesc(String noteId);

    @Query("""
                SELECT c
                FROM Comment c
                WHERE c.note.noteId = :noteId
                AND c.createdBy = :username
                ORDER BY c.createdAt DESC
            """)
    List<Comment> findByNoteIdAndCreatedByOrderByCreatedAtDesc(String noteId, String username);

    @Query("""
                SELECT c
                FROM Comment c
                WHERE c.commentId = :commentId
            """)
    Optional<Comment> findByCommentId(String commentId);
}