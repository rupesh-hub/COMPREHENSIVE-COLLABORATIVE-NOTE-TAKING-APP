package com.ccnta.app.note.entity;

import com.ccnta.app.comment.entity.Comment;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_notes")
@DynamicUpdate
public class Note extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_note_id_seq_generator")
    @SequenceGenerator(name = "_note_id_seq_generator", sequenceName = "_note_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "note_id", nullable = false, updatable = false, unique = true)
    private String noteId;

    private String title;
    private String content;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images = new HashSet<>();

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    // Helper method to add a comment
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setNote(this);
    }

    // Helper method to remove a comment
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setNote(null);
    }

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    public void setImages(Set<Image> images) {
        this.images = images;
        for (Image image : images) {
            image.setNote(this);
        }
    }

    // Helper method to manage bidirectional relationship
    public void addImage(Image image) {
        images.add(image);
        image.setNote(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setNote(null);
    }
}
