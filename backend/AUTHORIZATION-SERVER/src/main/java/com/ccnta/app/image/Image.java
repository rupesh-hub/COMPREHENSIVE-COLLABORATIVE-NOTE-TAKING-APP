package com.ccnta.app.image;

import com.ccnta.app.shared.AbstractEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "_images")
@Data
@DynamicUpdate
public class Image extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "_image_id_seq_generator")
    @SequenceGenerator(name = "_image_id_seq_generator", sequenceName = "_image_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private long size;

}
