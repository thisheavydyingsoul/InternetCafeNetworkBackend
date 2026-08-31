package com.internetcafe.repository;

import com.internetcafe.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByUsername(String username);
    boolean existsByEmail(String email);
}