package com.internetcafe.repository;

import com.internetcafe.entity.Rent;
import com.internetcafe.enums.RentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, String> {
    List<Rent> findByClientId(String clientId);
    List<Rent> findByDeviceId(String deviceId);
    List<Rent> findByStatus(RentStatus status);

    @Query("SELECT r FROM Rent r " +
            "WHERE r.device.id = :deviceId " +
            "AND r.status IN ('PENDING', 'ACTIVE') " +
            "AND r.startDateTime < :endDate AND r.endDateTime > :startDate")
    List<Rent> findOverlappingRents(@Param("deviceId") String deviceId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
}