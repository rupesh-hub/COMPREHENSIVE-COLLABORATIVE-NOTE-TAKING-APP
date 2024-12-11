package com.ccnta.app.comment.entity;

import com.ccnta.app.note.entity.Note;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_comments")
public class Comment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_comment_id_seq_generator")
    @SequenceGenerator(name = "_comment_id_seq_generator", sequenceName = "_comment_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "comment_id", nullable = false, updatable = false, unique = true)
    private String commentId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    @JsonIgnore
    private Note note;

    @ElementCollection
    @CollectionTable(name = "_comment_likes", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "username")
    private Set<String> likes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "_comment_dislikes", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "username")
    private Set<String> dislikes = new HashSet<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reply> replies = new HashSet<>();

    // Helper method to add a reply
    public void addReply(Reply reply) {
        replies.add(reply);
        reply.setComment(this);
    }

    // Helper method to remove a reply
    public void removeReply(Reply reply) {
        replies.remove(reply);
        reply.setComment(null);
    }
}
