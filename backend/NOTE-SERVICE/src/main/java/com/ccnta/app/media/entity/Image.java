package com.ccnta.app.media.entity;

import com.ccnta.app.draft.entity.Draft;
import com.ccnta.app.note.entity.Note;
import com.ccnta.app.shared.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_images")
public class Image extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_image_id_seq_generator")
    @SequenceGenerator(name = "_image_id_seq_generator", sequenceName = "_image_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "image_id", nullable = false, updatable = false, unique = true)
    private String imageId;

    private String name;
    private String path;
    private Long size;
    private String type;

    @ManyToOne
    @JoinColumn(name = "note_id", nullable = true)
    @JsonIgnore
    private Note note;

    @ManyToOne
    @JoinColumn(name = "draft_id", nullable = true)
    @JsonIgnore
    private Draft draft;

}
