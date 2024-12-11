package com.ccnta.app.media.repository;

import com.ccnta.app.media.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Long> {
}
