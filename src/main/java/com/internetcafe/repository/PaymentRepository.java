package com.internetcafe.repository;

import com.internetcafe.entity.Payment;
import com.internetcafe.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByRentId(String rentId);
    List<Payment> findByStatus(PaymentStatus status);
}