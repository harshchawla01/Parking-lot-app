package com.parking.parkinglotapplication.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository {
    @Query("SELECT COUNT(p) FROM ParkingLot p WHERE p.availability = 'AVAILABLE'")
    public int countAvailableParkingLots();
}
