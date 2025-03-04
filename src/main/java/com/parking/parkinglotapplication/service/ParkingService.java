package com.parking.parkinglotapplication.service;

import com.parking.parkinglotapplication.dto.AvailabilityResponse;
import com.parking.parkinglotapplication.dto.ParkingAssignmentRequest;
import com.parking.parkinglotapplication.dto.ParkingAssignmentResponse;
import com.parking.parkinglotapplication.dto.ParkingUnlockRequest;
import com.parking.parkinglotapplication.dto.ParkingUnlockResponse;

import java.util.List;
import java.util.Map;

public interface ParkingService {

    Map<Integer, AvailabilityResponse> getAvailabilityByLevel();

    Map<Integer, Boolean> getPublicAvailabilityByLevel(String vehicleType);

    ParkingAssignmentResponse assignParkingSpace(ParkingAssignmentRequest request);

    ParkingUnlockResponse unlockParkingSpace(ParkingUnlockRequest request);
}