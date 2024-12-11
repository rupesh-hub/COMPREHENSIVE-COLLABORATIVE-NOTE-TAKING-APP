package com.ccnta.app.comment.entity;

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
@Table(name = "_replies")
public class Reply extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_reply_id_seq_generator")
    @SequenceGenerator(name = "_reply_id_seq_generator", sequenceName = "_reply_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "reply_id", nullable = false, updatable=false, unique = true)
    private String replyId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    @JsonIgnore
    private Comment comment;

    @ElementCollection
    @CollectionTable(name = "_reply_likes", joinColumns = @JoinColumn(name = "reply_id"))
    @Column(name = "username")
    private Set<String> likes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "_reply_dislikes", joinColumns = @JoinColumn(name = "reply_id"))
    @Column(name = "username")
    private Set<String> dislikes = new HashSet<>();
}