package com.internetcafe.repository;

import com.internetcafe.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, String> {
    Optional<Administrator> findByEmail(String email);
    Optional<Administrator> findByUsername(String username);
    long countByIsHrTrueAndIsActiveTrue(); // для проверки последнего HR
}