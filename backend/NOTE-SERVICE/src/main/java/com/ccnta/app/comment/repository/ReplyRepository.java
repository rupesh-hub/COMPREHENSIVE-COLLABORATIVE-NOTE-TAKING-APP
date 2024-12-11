package com.ccnta.app.comment.repository;


import com.ccnta.app.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
                SELECT R FROM Reply R WHERE R.comment.commentId = :commentId ORDER BY R.createdAt DESC
            """)
    List<Reply> findByCommentIdOrderByCreatedAtDesc(String commentId);

    @Query("""
            SELECT R FROM Reply R WHERE R.replyId = :replyId
            """)
    Optional<Reply> findByReplyId(String replyId);
}

