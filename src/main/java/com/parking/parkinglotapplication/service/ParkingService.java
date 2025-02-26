package com.parking.parkinglotapplication.service;

import com.parking.parkinglotapplication.dto.AvailabilityResponse;
import com.parking.parkinglotapplication.dto.ParkingAssignmentRequest;
import com.parking.parkinglotapplication.dto.ParkingAssignmentResponse;
import com.parking.parkinglotapplication.dto.ParkingUnlockRequest;
import com.parking.parkinglotapplication.dto.ParkingUnlockResponse;

import java.util.List;
import java.util.Map;

public interface ParkingService {

    /**
     * Get availability of parking spaces by level and vehicle type
     * @return Map of level to AvailabilityResponse
     */
    Map<Integer, AvailabilityResponse> getAvailabilityByLevel();

    /**
     * Get availability for public users - only shows if slots are available or not
     * @return Map of level to availability status
     */
    Map<Integer, Boolean> getPublicAvailabilityByLevel(String vehicleType);

    /**
     * Assign and lock a parking space
     * @param request The parking assignment request
     * @return The assignment response with lot information
     */
    ParkingAssignmentResponse assignParkingSpace(ParkingAssignmentRequest request);

    /**
     * Unlock a parking space and calculate fees
     * @param request The unlock request
     * @return The unlock response with fee information
     */
    ParkingUnlockResponse unlockParkingSpace(ParkingUnlockRequest request);
}