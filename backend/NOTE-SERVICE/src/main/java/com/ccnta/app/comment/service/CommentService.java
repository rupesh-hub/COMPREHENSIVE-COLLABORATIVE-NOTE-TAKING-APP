package com.ccnta.app.comment.service;

import com.ccnta.app.comment.entity.Comment;
import com.ccnta.app.comment.entity.Reply;
import com.ccnta.app.comment.model.CommentRequest;
import com.ccnta.app.comment.model.ReplyRequest;
import com.ccnta.app.comment.repository.CommentRepository;
import com.ccnta.app.comment.repository.ReplyRepository;
import com.ccnta.app.note.entity.Note;
import com.ccnta.app.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final NoteRepository noteRepository;

    @Transactional
    public Comment createComment(final CommentRequest request) {
        Note note = noteRepository.findByNoteId(request.getNoteId())
                .orElseThrow(() -> new RuntimeException("Note not found"));

        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID().toString());
        comment.setContent(request.getContent());
        comment.setNote(note);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(String commentId, String content, String username) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getCreatedBy().equals(username)) {
            throw new RuntimeException("You are not authorized to update this comment");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getCreatedBy().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public Comment likeComment(String commentId, String username) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.getLikes().add(username);
        comment.getDislikes().remove(username);

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment dislikeComment(String commentId, String username) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.getDislikes().add(username);
        comment.getLikes().remove(username);

        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsByNoteId(String noteId) {
        return commentRepository.findByNoteIdOrderByCreatedAtDesc(noteId);
    }

    public List<Comment> getAllCommentsByNoteIdAndUsername(String noteId, String username) {
        return commentRepository.findByNoteIdAndCreatedByOrderByCreatedAtDesc(noteId, username);
    }

    // Reply CRUD operations

    @Transactional
    public Reply createReply(ReplyRequest request) {
        Comment comment = commentRepository.findByCommentId(request.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Reply reply = new Reply();
        reply.setReplyId(UUID.randomUUID().toString());
        reply.setContent(request.getContent());
        reply.setComment(comment);

        return replyRepository.save(reply);
    }

    @Transactional
    public Reply updateReply(String replyId, String content, String username) {
        Reply reply = replyRepository.findByReplyId(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getCreatedBy().equals(username)) {
            throw new RuntimeException("You are not authorized to update this reply");
        }

        reply.setContent(content);
        return replyRepository.save(reply);
    }

    @Transactional
    public void deleteReply(String replyId, String username) {
        Reply reply = replyRepository.findByReplyId(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getCreatedBy().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this reply");
        }

        replyRepository.delete(reply);
    }

    @Transactional
    public Reply likeReply(String replyId, String username) {
        Reply reply = replyRepository.findByReplyId(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        reply.getLikes().add(username);
        reply.getDislikes().remove(username);

        return replyRepository.save(reply);
    }

    @Transactional
    public Reply dislikeReply(String replyId, String username) {
        Reply reply = replyRepository.findByReplyId(replyId)
                .orElseThrow(() -> new RuntimeException("Reply not found"));

        reply.getDislikes().add(username);
        reply.getLikes().remove(username);

        return replyRepository.save(reply);
    }

    public List<Reply> getAllRepliesByCommentId(String commentId) {
        return replyRepository.findByCommentIdOrderByCreatedAtDesc(commentId);
    }

}
