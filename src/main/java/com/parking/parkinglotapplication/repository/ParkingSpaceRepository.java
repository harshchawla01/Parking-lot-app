package com.parking.parkinglotapplication.repository;

import com.parking.parkinglotapplication.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Integer> {
    ParkingSpace findByLevel(int level);
}