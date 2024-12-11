package com.ccnta.app.authority.repository;

import com.ccnta.app.authority.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @Query("SELECT A FROM Authority A WHERE LOWER(A.name) = LOWER(:authority)")
    Optional<Authority> findByName(@Param("authority") String authority);

    @Query("SELECT A FROM Authority A WHERE A.authorityId = :authorityId")
    Optional<Authority> findByAuthorityId(@Param("authorityId") String authorityId);
}
