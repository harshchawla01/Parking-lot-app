package com.parking.parkinglotapplication.service;

import com.parking.parkinglotapplication.entity.ParkingSpace;

import java.util.List;

public interface AdminService {
    public int parKingLotAvailability();

    List<ParkingSpace> fetchparkingSpaceDetails();
}
