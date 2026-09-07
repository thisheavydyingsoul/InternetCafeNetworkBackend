package com.internetcafe.repository;

import com.internetcafe.entity.Device;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByOfficeId(String officeId);

    @Query("SELECT d FROM Device d " +
            "WHERE d.office.id = :officeId " +
            "AND d.condition = 'WORKING'")
    List<Device> findAvailableDevicesByOffice(@Param("officeId") String officeId);

    @EntityGraph(attributePaths = {"office", "games"})
    Optional<Device> findWithDetailsById(String id);

    boolean existsByGames_Id(String gameId);
}