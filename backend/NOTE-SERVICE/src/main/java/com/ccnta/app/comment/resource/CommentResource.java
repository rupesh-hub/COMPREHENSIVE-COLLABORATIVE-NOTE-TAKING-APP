package com.ccnta.app.comment.resource;

import com.ccnta.app.comment.entity.Comment;
import com.ccnta.app.comment.entity.Reply;
import com.ccnta.app.comment.model.CommentRequest;
import com.ccnta.app.comment.model.ReplyRequest;
import com.ccnta.app.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
public class CommentResource {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.createComment(request);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId,
                                                 @RequestParam String content,
                                                 Authentication authentication) {
        Comment comment = commentService.updateComment(commentId, content, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId,
                                              Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable String commentId,
                                               Authentication authentication) {
        Comment comment = commentService.likeComment(commentId, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<Comment> dislikeComment(@PathVariable String commentId,
                                                  Authentication authentication) {
        Comment comment = commentService.dislikeComment(commentId, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Comment>> getAllCommentsByNoteId(@PathVariable String noteId) {
        List<Comment> comments = commentService.getAllCommentsByNoteId(noteId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/note/{noteId}/user")
    public ResponseEntity<List<Comment>> getAllCommentsByNoteIdAndUsername(@PathVariable String noteId,
                                                                           Authentication authentication) {
        List<Comment> comments = commentService.getAllCommentsByNoteIdAndUsername(noteId, authentication.getName());
        return ResponseEntity.ok(comments);
    }

    // Reply endpoints

    @PostMapping("replies")
    public ResponseEntity<Reply> createReply(@RequestBody ReplyRequest request) {
        Reply reply = commentService.createReply(request);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    @PutMapping("/replies/{replyId}")
    public ResponseEntity<Reply> updateReply(@PathVariable String replyId,
                                             @RequestParam String content,
                                             Authentication authentication) {
        Reply reply = commentService.updateReply(replyId, content, authentication.getName());
        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable String replyId,
                                            Authentication authentication) {
        commentService.deleteReply(replyId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/replies/{replyId}/like")
    public ResponseEntity<Reply> likeReply(@PathVariable String replyId,
                                           Authentication authentication) {
        Reply reply = commentService.likeReply(replyId, authentication.getName());
        return ResponseEntity.ok(reply);
    }

    @PostMapping("/replies/{replyId}/dislike")
    public ResponseEntity<Reply> dislikeReply(@PathVariable String replyId,
                                              Authentication authentication) {
        Reply reply = commentService.dislikeReply(replyId, authentication.getName());
        return ResponseEntity.ok(reply);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Reply>> getAllRepliesByCommentId(@PathVariable String commentId) {
        List<Reply> replies = commentService.getAllRepliesByCommentId(commentId);
        return ResponseEntity.ok(replies);
    }
}
