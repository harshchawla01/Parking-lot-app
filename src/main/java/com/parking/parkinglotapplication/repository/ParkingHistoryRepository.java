package com.parking.parkinglotapplication.repository;

import com.parking.parkinglotapplication.entity.ParkingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistory, Long> {
    Optional<ParkingHistory> findByVehicleNumberAndOutTimeIsNull(String vehicleNumber);
}