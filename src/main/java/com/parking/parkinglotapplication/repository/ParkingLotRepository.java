package com.parking.parkinglotapplication.repository;

import com.parking.parkinglotapplication.entity.Availability;
import com.parking.parkinglotapplication.entity.ParkingLot;
import com.parking.parkinglotapplication.entity.ParkingSpace;
import com.parking.parkinglotapplication.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    List<ParkingLot> findByParkingSpaceAndVehicleTypeAndAvailability(
            ParkingSpace parkingSpace,
            VehicleType vehicleType,
            Availability availability);

    @Query("SELECT COUNT(p) FROM ParkingLot p WHERE p.parkingSpace.level = ?1 AND p.vehicleType = ?2 AND p.availability = ?3")
    int countByLevelAndVehicleTypeAndAvailability(int level, VehicleType vehicleType, Availability availability);
}