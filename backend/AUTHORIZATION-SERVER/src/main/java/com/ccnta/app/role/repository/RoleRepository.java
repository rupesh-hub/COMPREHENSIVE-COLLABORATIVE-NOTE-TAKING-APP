package com.ccnta.app.role.repository;

import com.ccnta.app.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT * FROM _roles WHERE LOWER(name) = LOWER(:name) LIMIT 1", nativeQuery = true)
    Optional<Role> findByName(@Param("name") String name);

}