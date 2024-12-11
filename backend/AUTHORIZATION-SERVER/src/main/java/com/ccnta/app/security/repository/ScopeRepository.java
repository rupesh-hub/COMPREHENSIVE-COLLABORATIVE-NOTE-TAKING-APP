package com.ccnta.app.security.repository;

import com.ccnta.app.security.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, Long> {

    Optional<Scope> findByScope(String scope);

}