package com.internetcafe.repository;

import com.internetcafe.entity.OutboxEvent;
import com.internetcafe.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    @Query("SELECT e FROM OutboxEvent e " +
            "WHERE e.status = :status " +
            "ORDER BY e.createdAt ASC")
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(@Param("status") OutboxStatus status);
}