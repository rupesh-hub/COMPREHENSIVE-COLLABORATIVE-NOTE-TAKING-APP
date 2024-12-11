package com.ccnta.app.draft.entity;

import com.ccnta.app.authority.entity.Authority;
import com.ccnta.app.media.entity.Image;
import com.ccnta.app.project.entity.Project;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "_draft")
public class Draft extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_draft_id_seq_generator")
    @SequenceGenerator(name = "_draft_id_seq_generator", sequenceName = "_draft_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "draft_id", nullable = false, updatable = false, unique = true)
    private String draftId;

    private String title;
    private String content;

    @OneToMany(mappedBy = "draft", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Image> images = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    public void setImages(Set<Image> images) {
        this.images = images;
        for (Image image : images) {
            image.setDraft(this);
        }
    }
}
