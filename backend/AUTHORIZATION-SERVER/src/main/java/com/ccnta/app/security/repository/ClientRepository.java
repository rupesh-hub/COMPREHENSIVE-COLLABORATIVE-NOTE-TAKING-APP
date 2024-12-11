package com.ccnta.app.security.repository;

import com.ccnta.app.security.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query(name="Client.findByClientId")
    Optional<Client> findByClientId(@Param("clientId") String clientId);

}