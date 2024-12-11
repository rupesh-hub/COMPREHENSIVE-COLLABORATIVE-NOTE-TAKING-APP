package com.ccnta.app.security.repository;

import com.ccnta.app.security.entity.RedirectUri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedirectUriRepository extends JpaRepository<RedirectUri, Long> {

    Optional<RedirectUri> findByUri(String uri);

}