package com.internetcafe.repository;

import com.internetcafe.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromoRepository extends JpaRepository<Promo, String> {
    @Query("SELECT p FROM Promo p " +
            "WHERE p.startDate <= :now " +
            "AND p.endDate >= :now")
    List<Promo> findActivePromos(@Param("now") LocalDateTime now);
}